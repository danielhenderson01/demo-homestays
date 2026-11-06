import fs from "node:fs";
import path from "node:path";
import { Construct } from "constructs";
import * as cdk from "aws-cdk-lib";
import * as ec2 from "aws-cdk-lib/aws-ec2";
import * as ecs from "aws-cdk-lib/aws-ecs";
import * as ecsPatterns from "aws-cdk-lib/aws-ecs-patterns";
import * as s3 from "aws-cdk-lib/aws-s3";
import * as cloudfront from "aws-cdk-lib/aws-cloudfront";
import * as origins from "aws-cdk-lib/aws-cloudfront-origins";
import * as rds from "aws-cdk-lib/aws-rds";
import * as sm from "aws-cdk-lib/aws-secretsmanager";
import * as cognito from "aws-cdk-lib/aws-cognito";

export class AppStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const envPath = path.resolve(__dirname, "../.env");

    if (fs.existsSync(envPath)) {
      process.loadEnvFile(envPath);
    }

    const stripeSecret = sm.Secret.fromSecretAttributes(this, "StripeSecret", {
      secretCompleteArn: this.requireEnv("hs_stripe_secret_arn"),
    });

    const dbSecret = sm.Secret.fromSecretAttributes(this, "DbSecret", {
      secretCompleteArn: this.requireEnv("hs_db_secret_arn"),
    });

    const { userPool } = this.createUserPool();

    const uploadsBucket = this.createUploadsBucket();

    const frontendBucket = this.createFrontendBucket();

    const distribution = this.createCfDistribution({ frontendBucket });

    const vpc = this.createVpc();

    const cluster = this.createEcsCluster({ vpc });

    const { database, dbName } = this.createDatabase({
      vpc,
      dbSecret,
    });

    const containerEnv = this.createContainerEnv({
      dbAddress: database.dbInstanceEndpointAddress,
      dbPort: database.dbInstanceEndpointPort,
      dbName,
      uploadsBucketName: uploadsBucket.bucketName,
      cloudfrontUrl: `https://${distribution.distributionDomainName}`,
      userPoolId: userPool.userPoolId,
    });

    const containerSecrets = this.createContainerSecrets({
      stripeSecret,
      dbSecret,
    });

    const backend = this.createBackend({
      cluster,
      db: database,
      containerEnv,
      containerSecrets,
    });

    this.addLoadBalancerBehavior({
      pathPattern: "/api/*",
      distribution,
      loadBalancer: backend.loadBalancer,
    });

    this.addBucketBehavior({
      pathPattern: "/images/*",
      distribution,
      bucket: uploadsBucket,
    });
  }

  private requireEnv(name: string): string {
    const value = process.env[name];
    if (!value) {
      throw new Error(`Missing required environment variable: ${name}`);
    }
    return value;
  }

  private createVpc(): cdk.aws_ec2.Vpc {
    return new ec2.Vpc(this, "Vpc", {
      maxAzs: 2,
    });
  }

  private createEcsCluster({ vpc }: { vpc: ec2.Vpc }): ecs.Cluster {
    return new ecs.Cluster(this, "Cluster", {
      vpc,
    });
  }

  private createBackend({
    cluster,
    db,
    containerEnv,
    containerSecrets,
  }: {
    cluster: ecs.Cluster;
    db: rds.DatabaseInstance;
    containerEnv: {
      [key: string]: string;
    };
    containerSecrets: {
      [key: string]: ecs.Secret;
    };
  }): ecsPatterns.ApplicationLoadBalancedFargateService {
    const ecrRepoUri = this.requireEnv("hs_ecr_repo_uri");

    const service = new ecsPatterns.ApplicationLoadBalancedFargateService(
      this,
      "BackendAlbFargateService",
      {
        cluster,
        publicLoadBalancer: true,
        assignPublicIp: true,
        desiredCount: 1,
        cpu: 512,
        memoryLimitMiB: 1024,
        taskImageOptions: {
          image: ecs.ContainerImage.fromRegistry(ecrRepoUri),
          containerPort: 8080,
          environment: containerEnv,
          secrets: containerSecrets,
        },
      },
    );

    service.targetGroup.configureHealthCheck({
      path: "/api/health",
      healthyHttpCodes: "200",
    });

    service.node.addDependency(db);

    return service;
  }

  private createDatabase({
    vpc,
    dbSecret,
  }: {
    vpc: ec2.Vpc;
    dbSecret: sm.ISecret;
  }): {
    database: rds.DatabaseInstance;
    dbName: string;
  } {
    const dbName = this.requireEnv("hs_db_name");

    const database = new rds.DatabaseInstance(this, "Database", {
      vpc: vpc,
      engine: rds.DatabaseInstanceEngine.postgres({
        version: rds.PostgresEngineVersion.VER_18,
      }),
      instanceType: ec2.InstanceType.of(
        ec2.InstanceClass.T4G,
        ec2.InstanceSize.MICRO,
      ),
      credentials: rds.Credentials.fromSecret(dbSecret),
      allocatedStorage: 20,
      databaseName: dbName,
    });

    return { database, dbName };
  }

  private createUploadsBucket(): cdk.aws_s3.Bucket {
    const uploadsBucket = new s3.Bucket(this, "UploadsBucket", {
      bucketName: "uploads-bucket",
      cors: [
        {
          allowedMethods: [
            s3.HttpMethods.PUT,
            s3.HttpMethods.POST,
            s3.HttpMethods.HEAD,
          ],
          allowedOrigins: ["*"],
          allowedHeaders: ["*"],
          exposedHeaders: ["ETag"],
          maxAge: 3600,
        },
      ],
    });

    new cdk.CfnOutput(this, "UploadsBucketName", {
      value: uploadsBucket.bucketName,
    });

    return uploadsBucket;
  }

  private createFrontendBucket(): cdk.aws_s3.Bucket {
    const frontendBucket = new s3.Bucket(this, "FrontendBucket", {
      bucketName: "frontend-bucket",
      blockPublicAccess: s3.BlockPublicAccess.BLOCK_ALL,
    });

    new cdk.CfnOutput(this, "FrontendBucketName", {
      value: frontendBucket.bucketName,
    });

    return frontendBucket;
  }

  private createCfDistribution({
    frontendBucket,
  }: {
    frontendBucket: cdk.aws_s3.Bucket;
  }): cdk.aws_cloudfront.Distribution {
    const spaRewriteFunction = new cloudfront.Function(
      this,
      "SpaRewriteFunction",
      {
        runtime: cloudfront.FunctionRuntime.JS_2_0,
        code: cloudfront.FunctionCode.fromInline(`
          function handler(event) {
              var request = event.request;
              var uri = request.uri;
              // Defensive check
              if (uri === '/api' || uri.startsWith('/api/')) {
                  return request;
              }
              // If the request isn't for a file, let the frontend handle it.
              if (!uri.includes('.')) {
                  request.uri = '/index.html';
              }
              return request;
          }
        `),
      },
    );

    const distribution = new cloudfront.Distribution(this, "Distribution", {
      defaultRootObject: "index.html",
      defaultBehavior: {
        origin: origins.S3BucketOrigin.withOriginAccessControl(frontendBucket),
        viewerProtocolPolicy: cloudfront.ViewerProtocolPolicy.REDIRECT_TO_HTTPS,
        allowedMethods: cloudfront.AllowedMethods.ALLOW_GET_HEAD_OPTIONS,
        cachePolicy: cloudfront.CachePolicy.CACHING_OPTIMIZED,
        compress: true,
        functionAssociations: [
          {
            function: spaRewriteFunction,
            eventType: cloudfront.FunctionEventType.VIEWER_REQUEST,
          },
        ],
      },
    });

    new cdk.CfnOutput(this, "DistributionUrl", {
      value: `https://${distribution.distributionDomainName}`,
    });

    new cdk.CfnOutput(this, "DistributionId", {
      value: distribution.distributionId,
    });

    return distribution;
  }

  private addLoadBalancerBehavior({
    pathPattern,
    distribution,
    loadBalancer,
  }: {
    pathPattern: string;
    distribution: cdk.aws_cloudfront.Distribution;
    loadBalancer: cdk.aws_elasticloadbalancingv2.ApplicationLoadBalancer;
  }): void {
    distribution.addBehavior(
      pathPattern,
      new origins.LoadBalancerV2Origin(loadBalancer, {
        protocolPolicy: cloudfront.OriginProtocolPolicy.HTTP_ONLY,
      }),
      {
        viewerProtocolPolicy: cloudfront.ViewerProtocolPolicy.REDIRECT_TO_HTTPS,
        allowedMethods: cloudfront.AllowedMethods.ALLOW_ALL,
        // Don't cache authenticated API requests.
        cachePolicy: cloudfront.CachePolicy.CACHING_DISABLED,
        // Forward Authorization, cookies, query params, etc.
        originRequestPolicy: cloudfront.OriginRequestPolicy.ALL_VIEWER,
        compress: true,
      },
    );
  }

  private addBucketBehavior({
    pathPattern,
    distribution,
    bucket,
  }: {
    pathPattern: string;
    distribution: cdk.aws_cloudfront.Distribution;
    bucket: cdk.aws_s3.IBucket;
  }): void {
    distribution.addBehavior(
      pathPattern,
      origins.S3BucketOrigin.withOriginAccessControl(bucket),
      {
        viewerProtocolPolicy: cloudfront.ViewerProtocolPolicy.REDIRECT_TO_HTTPS,
        allowedMethods: cloudfront.AllowedMethods.ALLOW_GET_HEAD_OPTIONS,
        cachePolicy: cloudfront.CachePolicy.CACHING_OPTIMIZED,
        compress: true,
      },
    );
  }

  private createUserPool() {
    const userPool = new cognito.UserPool(this, "UserPool", {
      userPoolName: "homestays-user-pool",
      signInAliases: { email: true },
      accountRecovery: cognito.AccountRecovery.EMAIL_ONLY,
      customAttributes: {
        role: new cognito.StringAttribute(),
      },
    });
    const userPoolClient = userPool.addClient("UserPoolClient", {
      userPoolClientName: "homestays-client",
      authFlows: {
        userPassword: true,
        userSrp: true,
      },
    });
    new cdk.CfnOutput(this, "UserPoolId", {
      value: userPool.userPoolId,
    });
    new cdk.CfnOutput(this, "UserPoolClientId", {
      value: userPoolClient.userPoolClientId,
    });
    return { userPool, userPoolClient };
  }

  private createContainerSecrets({
    stripeSecret,
    dbSecret,
  }: {
    stripeSecret: cdk.aws_secretsmanager.ISecret;
    dbSecret: cdk.aws_secretsmanager.ISecret;
  }): {
    [key: string]: cdk.aws_ecs.Secret;
  } {
    return {
      SPRING_DATASOURCE_USERNAME: ecs.Secret.fromSecretsManager(
        dbSecret,
        "username",
      ),
      SPRING_DATASOURCE_PASSWORD: ecs.Secret.fromSecretsManager(
        dbSecret,
        "password",
      ),
      HS_STRIPE_SECRET_KEY: ecs.Secret.fromSecretsManager(
        stripeSecret,
        "stripe_secret_key",
      ),
    };
  }

  private createContainerEnv({
    dbAddress,
    dbPort,
    dbName,
    uploadsBucketName,
    cloudfrontUrl,
    userPoolId,
  }: {
    dbAddress: string;
    dbPort: string;
    dbName: string;
    uploadsBucketName: string;
    cloudfrontUrl: string;
    userPoolId: string;
  }): {
    [key: string]: string;
  } {
    const cloudEnv = this.requireEnv("hs_cloud_env");
    const awsRegion = this.requireEnv("hs_aws_region");

    const jwtIssuerUri =
      cloudEnv === "localstack"
        ? `http://localhost.localstack.cloud:4566/${userPoolId}`
        : `https://cognito-idp.${awsRegion}.amazonaws.com/${userPoolId}`;

    const jdbcUrl = `jdbc:postgresql://${dbAddress}:${dbPort}/${dbName}`;

    new cdk.CfnOutput(this, "JdbcUrl", {
      value: jdbcUrl,
    });

    const containerEnv = {
      spring_profiles_active: this.requireEnv("hs_spring_profiles_active"),
      spring_security_oauth2_resourceserver_jwt_issuer_uri: jwtIssuerUri,
      spring_security_oauth2_resourceserver_jwt_jwk_set_uri: `${jwtIssuerUri}/.well-known/jwks.json`,
      spring_datasource_url: jdbcUrl,
      hs_aws_region: awsRegion,
      hs_aws_s3_presign_duration: this.requireEnv("hs_aws_s3_presign_duration"),
      hs_aws_s3_sample_prefix: this.requireEnv("hs_aws_s3_sample_prefix"),
      hs_aws_cloudfront_url: cloudfrontUrl,
      hs_aws_s3_bucket: uploadsBucketName,
      hs_stripe_success_url: `${cloudfrontUrl}/guest/bookings/payment/success`,
    };

    return cloudEnv === "localstack"
      ? {
          ...containerEnv,
          hs_aws_s3_uri: "https://s3.localhost.localstack.cloud:4566",
        }
      : containerEnv;
  }
}

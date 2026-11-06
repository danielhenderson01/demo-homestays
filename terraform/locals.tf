data "aws_availability_zones" "available" {
  state = "available"
}

data "aws_partition" "current" {}

locals {
  azs = slice(data.aws_availability_zones.available.names, 0, 2)

  jwt_issuer_uri = var.hs_cloud_env == "aws" ? (
    "https://cognito-idp.${var.hs_aws_region}.amazonaws.com/${aws_cognito_user_pool.homestays.id}"
    ) : (
    "http://localhost.localstack.cloud:4566/${aws_cognito_user_pool.homestays.id}"
  )

  jwt_jwk_set_uri = "${local.jwt_issuer_uri}/.well-known/jwks.json"
  jdbc_url        = "jdbc:postgresql://${aws_db_instance.database.address}:${aws_db_instance.database.port}/${var.hs_db_name}"
  cloudfront_url  = "https://${aws_cloudfront_distribution.app.domain_name}"

  container_environment = merge(
    {
      hs_aws_region                                         = var.hs_aws_region
      hs_aws_s3_presign_duration                            = var.hs_aws_s3_presign_duration
      hs_aws_s3_sample_prefix                               = var.hs_aws_s3_sample_prefix
      spring_security_oauth2_resourceserver_jwt_issuer_uri  = local.jwt_issuer_uri
      spring_security_oauth2_resourceserver_jwt_jwk_set_uri = local.jwt_jwk_set_uri
      spring_profiles_active                                = var.hs_spring_profiles_active
      spring_datasource_url                                 = local.jdbc_url
      hs_aws_cloudfront_url                                 = local.cloudfront_url
      hs_aws_s3_bucket                                      = aws_s3_bucket.uploads.bucket
      hs_stripe_success_url                                 = "${local.cloudfront_url}/guest/bookings/payment/success"
    },
    var.hs_cloud_env == "localstack" ? {
      hs_aws_s3_uri = "https://s3.localhost.localstack.cloud:4566"
    } : {}
  )
}

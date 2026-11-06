#!/usr/bin/env bash

set -euo pipefail

echo "⇒ Deploying CDK infrastructure..."

hs_cdk_out="/tmp/$$.cdk-infrastructure-outputs.json"

cd "$hs_cdk_dir"

# Write variables to .env in cdk dir
cat <<EOF >.env
hs_stripe_secret_arn=$hs_stripe_secret_arn
hs_db_secret_arn=$hs_db_secret_arn
hs_ecr_repo_uri=$hs_ecr_repo_uri
hs_db_name=$hs_db_name
hs_cloud_env=$hs_cloud_env
hs_aws_region=$hs_aws_region
hs_aws_s3_presign_duration=$hs_aws_s3_presign_duration
hs_aws_s3_sample_prefix=$hs_aws_s3_sample_prefix
hs_spring_profiles_active=$hs_spring_profiles_active
EOF

if [ ! -d "node_modules" ]; then npm install; fi

${hs_cdk_cmd} bootstrap

${hs_cdk_cmd} deploy \
  --require-approval never \
  --outputs-file "${hs_cdk_out}" \
  --all

hs_frontend_bucket_name="$(awk '/FrontendBucketName/{ gsub(/"|,/, ""); print $2 }' "$hs_cdk_out")"
hs_uploads_bucket_name="$(awk '/UploadsBucketName/{ gsub(/"|,/, ""); print $2 }' "$hs_cdk_out")"
hs_cloudfront_distribution_id="$(awk '/DistributionId/{ gsub(/"|,/, ""); print $2 }' "$hs_cdk_out")"
hs_cloudfront_distribution_url="$(awk '/DistributionUrl/{ gsub(/"|,/, ""); print $2 }' "$hs_cdk_out")"
hs_jdbc_url="$(awk '/JdbcUrl/{ gsub(/"|,/, ""); print $2 }' "$hs_cdk_out")"
hs_user_pool_id="$(awk '/UserPoolId/{ gsub(/"|,/, ""); print $2 }' "$hs_cdk_out")"
hs_user_pool_client_id="$(awk '/UserPoolClientId/{ gsub(/"|,/, ""); print $2 }' "$hs_cdk_out")"

cat <<EOF >>"$hs_generated"
hs_frontend_bucket_name=$hs_frontend_bucket_name
hs_uploads_bucket_name=$hs_uploads_bucket_name
hs_cloudfront_distribution_id=$hs_cloudfront_distribution_id
hs_cloudfront_distribution_url=$hs_cloudfront_distribution_url
hs_jdbc_url=$hs_jdbc_url
hs_user_pool_id=$hs_user_pool_id
hs_user_pool_client_id=$hs_user_pool_client_id
EOF

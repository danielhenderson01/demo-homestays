#!/usr/bin/env bash

set -euo pipefail

echo "⇒ Deploying Terraform infrastructure..."

cd "$hs_tf_dir"

cat <<EOF >terraform.tfvars
hs_project_name = "$hs_project_name"
hs_stripe_secret_arn = "$hs_stripe_secret_arn"
hs_db_secret_arn = "$hs_db_secret_arn"
hs_ecr_repo_uri = "$hs_ecr_repo_uri"
hs_db_name = "$hs_db_name"
hs_cloud_env = "$hs_cloud_env"
hs_aws_region = "$hs_aws_region"
hs_aws_s3_presign_duration = "$hs_aws_s3_presign_duration"
hs_aws_s3_sample_prefix = "$hs_aws_s3_sample_prefix"
hs_spring_profiles_active = "$hs_spring_profiles_active"
hs_db_username = "$hs_db_username"
enable_nat_gateways = false
EOF

${hs_tf_cmd} init

${hs_tf_cmd} plan -out=tfplan

${hs_tf_cmd} apply tfplan

rm tfplan

hs_frontend_bucket_name="$(${hs_tf_cmd} output -raw hs_frontend_bucket_name)"
hs_uploads_bucket_name="$(${hs_tf_cmd} output -raw hs_uploads_bucket_name)"
hs_cloudfront_distribution_id="$(${hs_tf_cmd} output -raw hs_distribution_id)"
hs_cloudfront_distribution_url="$(${hs_tf_cmd} output -raw hs_distribution_url)"
hs_jdbc_url="$(${hs_tf_cmd} output -raw hs_jdbc_url)"
hs_user_pool_id="$(${hs_tf_cmd} output -raw hs_user_pool_id)"
hs_user_pool_client_id="$(${hs_tf_cmd} output -raw hs_user_pool_client_id)"

cat <<EOF >"$hs_generated"
hs_frontend_bucket_name=$hs_frontend_bucket_name
hs_uploads_bucket_name=$hs_uploads_bucket_name
hs_cloudfront_distribution_id=$hs_cloudfront_distribution_id
hs_cloudfront_distribution_url=$hs_cloudfront_distribution_url
hs_jdbc_url=$hs_jdbc_url
hs_user_pool_id=$hs_user_pool_id
hs_user_pool_client_id=$hs_user_pool_client_id
EOF

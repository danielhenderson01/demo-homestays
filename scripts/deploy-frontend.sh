#!/usr/bin/env bash

set -euo pipefail

echo "⇒ Deploying frontend..."

cd "$hs_frontend_dir"

cat <<EOF >.env
VITE_API_URL=$hs_cloudfront_distribution_url/api
VITE_IMAGES_URL=$hs_cloudfront_distribution_url/images
VITE_AWS_COGNITO_USER_POOL_ID=$hs_user_pool_id
VITE_AWS_COGNITO_USER_POOL_CLIENT_ID=$hs_user_pool_client_id
EOF

if [[ "$hs_cloud_env" = "localstack" ]]; then
  echo "VITE_AWS_COGNITO_USER_POOL_ENDPOINT=https://cognito-idp.localhost.localstack.cloud:4566" >>".env"
fi

if [[ -d "dist" ]]; then rm -rf dist; fi

if [[ ! -d "node_modules" ]]; then npm install; fi

npm run build

${hs_aws_cmd} s3 sync dist s3://$hs_frontend_bucket_name

# ${hs_aws_cmd} cloudfront create-invalidation --distribution-id "$hs_cloudfront_distribution_id" --paths "/*"

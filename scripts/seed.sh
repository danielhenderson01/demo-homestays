#!/usr/bin/env bash

set -euo pipefail

api_url="$hs_cloudfront_distribution_url/api"

### Get credentials
if secret=$(
  ${hs_aws_cmd} secretsmanager get-secret-value \
    --secret-id "$hs_seed_host_secret_id" 2>&1 |
    jq -r '.SecretString | fromjson'
); then
  user_name=$(echo "$secret" | jq -r '.name')
  user_email=$(echo "$secret" | jq -r '.email')
  user_password=$(echo "$secret" | jq -r '.password')
  user_role=$(echo "$secret" | jq -r '.role')
else
  printf 'Secret lookup failed:\n%s\n' "$secret" >&2
  exit 1
fi

### Authenticate
echo "⇒ Authenticating..."
if auth_response=$(
  ${hs_aws_cmd} cognito-idp admin-initiate-auth \
    --user-pool-id "$hs_user_pool_id" \
    --client-id "$hs_user_pool_client_id" \
    --auth-flow ADMIN_USER_PASSWORD_AUTH \
    --auth-parameters USERNAME="$user_email",PASSWORD="$user_password" \
    --query 'AuthenticationResult.IdToken' \
    --output text 2>&1
); then
  id_token="$auth_response"
elif [[ "$auth_response" = *UserNotFoundException* ]]; then
  echo "⇒ User not found..."
  echo "⇒ Creating user..."
  ${hs_aws_cmd} cognito-idp admin-create-user \
    --user-pool-id "$hs_user_pool_id" \
    --username "$user_email" \
    --user-attributes Name="email",Value="$user_email" Name="email_verified",Value="true" Name="name",Value="$user_name" Name="custom:role",Value="$user_role" \
    --message-action SUPPRESS \
    --output off

  ${hs_aws_cmd} cognito-idp admin-set-user-password \
    --user-pool-id "$hs_user_pool_id" \
    --username "$user_email" \
    --password "$user_password" \
    --permanent \
    --output off

  id_token=$(
    ${hs_aws_cmd} cognito-idp admin-initiate-auth \
      --user-pool-id "$hs_user_pool_id" \
      --client-id "$hs_user_pool_client_id" \
      --auth-flow ADMIN_USER_PASSWORD_AUTH \
      --auth-parameters USERNAME="$user_email",PASSWORD="$user_password" \
      --query 'AuthenticationResult.IdToken' \
      --output text
  )

  curl --silent \
    --request GET \
    --header "Authorization: Bearer $id_token" \
    "$api_url/users" \
    -o /dev/null
else
  printf 'Initiate auth failed:\n%s\n' "$auth_response" >&2
  exit 1
fi

###############
### Seed Images
###############

cd "$hs_data_dir/images"

for i in $(ls *.jpg *.png); do
  ${hs_aws_cmd} s3api put-object \
    --bucket $hs_uploads_bucket_name \
    --key "$hs_aws_s3_sample_prefix/$i" \
    --body $i \
    --output off
done

###################
### Seed Properties
###################

cd "$hs_data_dir"

### Read data file
echo "⇒ Reading data file..."
readarray -t properties < <(jq -c '.[]' "properties.json")

### Send properties request
echo "⇒ Sending properties request..."
for property in "${properties[@]}"; do
  curl --silent \
    --request POST \
    --header "Authorization: Bearer $id_token" \
    --header "Content-Type: application/json" \
    --data-raw "$property" \
    "$api_url/properties" | jq -c .
done

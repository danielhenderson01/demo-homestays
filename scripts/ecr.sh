#!/usr/bin/env bash

set -euo pipefail

if [[ ! -f "$hs_backend_dir/Dockerfile" ]]; then
  echo "Missing $hs_backend_dir/Dockerfile"
  exit 1
fi

if response=$(
  ${hs_aws_cmd} ecr describe-repositories \
    --repository-names "$hs_ecr_repo_name" \
    --query 'repositories[0].repositoryUri' \
    --output text 2>&1
); then
  repo_uri="$response"
  echo "⇒ ECR repository: $repo_uri"
elif [[ "$response" == *RepositoryNotFoundException* ]]; then
  echo "⇒ Creating ECR repository..."
  repo_uri=$(
    ${hs_aws_cmd} ecr create-repository \
      --repository-name "$hs_ecr_repo_name" \
      --image-scanning-configuration scanOnPush=true \
      --query 'repository.repositoryUri' \
      --output text
  ) || exit 1
else
  printf 'ECR lookup failed:\n%s\n' "$response" >&2
  exit 1
fi

echo "⇒ Building image..."
docker build -t "$repo_uri" "$hs_backend_dir"

echo "⇒ Pushing image..."
docker push "$repo_uri"

echo "hs_ecr_repo_uri=$repo_uri" >>"$hs_generated"

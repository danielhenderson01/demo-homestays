SHELL := /bin/bash

.DEFAULT_GOAL := help

hs_cdk_dir := $(CURDIR)/cdk
hs_tf_dir := $(CURDIR)/terraform
hs_backend_dir := $(CURDIR)/backend
hs_frontend_dir := $(CURDIR)/frontend
hs_data_dir := $(CURDIR)/data

# Secrets files and IDs
hs_stripe_file := secrets/stripe-secret.json
hs_stripe_secret_id := stripe-secret
hs_db_file := secrets/db-secret.json
hs_db_secret_id := db-secret
hs_seed_host_file := secrets/seed-host-secret.json
hs_seed_host_secret_id := seed-host-secret
hs_test_host_file := secrets/test-host-secret.json
hs_test_host_secret_id := test-host-secret
hs_test_guest_file := secrets/test-guest-secret.json
hs_test_guest_secret_id := test-guest-secret

hs_env_file := $(CURDIR)/.env
ifneq (,$(wildcard $(hs_env_file)))
	include $(hs_env_file)
endif

# terraform | cdk
hs_iac ?= cdk
# localstack | aws
hs_cloud_env ?= localstack
# Must exactly match the 'username' field in the DB secret
hs_db_username ?= user
hs_db_name ?= db
hs_project_name ?= demo-homestays
hs_ecr_repo_name ?= demo-homestays
hs_aws_region ?= us-east-1
hs_aws_s3_presign_duration ?= 10
hs_aws_s3_sample_prefix ?= images/samples
hs_spring_profiles_active ?= dev

ifeq ($(hs_cloud_env), localstack)
	hs_aws_cmd := lstk aws
	hs_cdk_cmd := lstk cdk
	hs_tf_cmd := lstk terraform
else
	hs_aws_cmd := aws
	hs_cdk_cmd := cdk
	hs_tf_cmd := terraform
endif

hs_generated := $(CURDIR)/.env.generated
ifneq (,$(wildcard $(hs_generated)))
	include $(hs_generated)
endif

export


##@ General

debug-env:
	@env | sort | grep -E '^hs_'

help:           ## Show available commands
	@awk 'BEGIN {FS = ":.*##"; printf "\nUsage:\n  make <target>\n"} \
		/^[a-zA-Z_0-9-]+:.*##/ {printf "  %-25s %s\n", $$1, $$2} \
		/^##@/ {printf "\n%s\n", substr($$0, 5)}' $(MAKEFILE_LIST)
	@echo

clean:          ## Remove generated files
	@if [[ -f $(hs_generated) ]]; then rm $(hs_generated); fi

start:          ## Start
	@if [[ $(hs_cloud_env) = localstack ]]; then \
		LOCALSTACK_EXTRA_CORS_ALLOWED_ORIGINS='*' lstk start --non-interactive; \
	fi

stop:           ## Stop
	@if [[ $(hs_cloud_env) = localstack ]]; then lstk stop; fi

check:          ## Check required prerequisites
	@command -v bash > /dev/null 2>&1 || { echo "bash is not installed."; exit 1; }
	@command -v jq > /dev/null 2>&1 || { echo "jq is not installed."; exit 1; }
	@command -v node > /dev/null 2>&1 || { echo "node is not installed."; exit 1; }
	@command -v npm > /dev/null 2>&1 || { echo "npm is not installed."; exit 1; }
	@command -v docker > /dev/null 2>&1 || { echo "docker is not installed."; exit 1; }
	@command -v aws > /dev/null 2>&1 || { echo "aws is not installed."; exit 1; }
	@if [[ $(hs_cloud_env) = localstack ]]; then \
		command -v lstk > /dev/null 2>&1 || { echo "lstk is not installed."; exit 1; } \
	fi
	@if [[ $(hs_iac) = cdk ]]; then \
		command -v cdk > /dev/null 2>&1 || { echo "cdk is not installed."; exit 1; } \
	fi
	@if [[ $(hs_iac) = terraform ]]; then \
		command -v terraform > /dev/null 2>&1 || { echo "terraform is not installed."; exit 1; } \
	fi
	@echo "All required prerequisites are installed."


##@ Deployment

secrets:        ## Create Secrets Manager secrets
	@bash scripts/secrets.sh

ecr:            ## Build and push image to ECR
	@bash scripts/ecr.sh

cdk:            ## Deploy infrastructure with CDK
	@if [[ $(hs_iac) = cdk ]]; then bash scripts/deploy-cdk.sh; fi

terraform:      ## Deploy infrastructure with Terraform
	@if [[ $(hs_iac) = terraform ]]; then bash scripts/deploy-terraform.sh; fi

frontend:       ## Deploy frontend
	@bash scripts/deploy-frontend.sh

deploy:         ## Deploy the full application
	@$(MAKE) clean
	@$(MAKE) check
	@if [[ $(hs_cloud_env) = localstack ]]; then $(MAKE) start; fi
	@$(MAKE) secrets
	@$(MAKE) ecr
	@if [[ $(hs_iac) = cdk ]]; then \
		$(MAKE) cdk; \
	else \
		$(MAKE) terraform; \
	fi
	@$(MAKE) frontend

seed:                  ## Seed user, properties, and images
	@bash scripts/seed.sh

.PHONY: help clean start stop secrets ecr cdk terraform frontend deploy seed create-test-users check debug-env


variable "hs_project_name" {
  description = "Short lowercase prefix for resource names, unique within your AWS account."
  type        = string
  default     = "demo-homestays"

  validation {
    condition     = can(regex("^[a-z][a-z0-9-]{1,20}$", var.hs_project_name))
    error_message = "Use 2-21 lowercase letters, digits, or hyphens, starting with a letter."
  }
}

variable "hs_aws_region" {
  type    = string
  default = "us-east-1"
}

variable "vpc_cidr" {
  type    = string
  default = "10.0.0.0/16"
}

variable "enable_nat_gateways" {
  description = "Match the CDK VPC default (two NAT gateways). Disable to keep DB subnets isolated and save cost."
  type        = bool
  default     = true
}

variable "hs_db_name" {
  type = string
}

variable "hs_db_username" {
  description = "Must exactly match the 'username' field in the pre-existing DB secret."
  type        = string
}

variable "hs_db_secret_arn" {
  description = "ARN of an existing Secrets Manager JSON secret with username and password fields."
  type        = string
}

variable "hs_db_password_version" {
  description = "Increment after changing the existing Secrets Manager database password."
  type        = number
  default     = 1
}

variable "hs_stripe_secret_arn" {
  description = "ARN of an existing Secrets Manager JSON secret with a stripe_secret_key field."
  type        = string
}

variable "hs_cloud_env" {
  type    = string
  default = "localstack"

  validation {
    condition     = contains(["aws", "localstack"], var.hs_cloud_env)
    error_message = "hs_cloud_env must be aws or localstack."
  }
}

variable "hs_aws_s3_presign_duration" {
  type = string
}

variable "hs_aws_s3_sample_prefix" {
  type = string
}

variable "hs_spring_profiles_active" {
  type    = string
  default = "dev"
}

variable "hs_ecr_repo_uri" {
  type = string
}

variable "desired_count" {
  type    = number
  default = 1
}

variable "log_retention_days" {
  type    = number
  default = 14
}

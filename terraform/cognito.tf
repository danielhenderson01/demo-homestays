resource "aws_cognito_user_pool" "homestays" {
  name = "homestays-user-pool"

  # Sign in using email addresses
  username_attributes      = ["email"]
  auto_verified_attributes = ["email"]

  # admin-created users only (CDK default)
  admin_create_user_config {
    allow_admin_create_user_only = true
  }

  # Account recovery through email only
  account_recovery_setting {
    recovery_mechanism {
      name     = "verified_email"
      priority = 1
    }
  }

  # Custom attribute: custom:role
  schema {
    name                = "role"
    attribute_data_type = "String"
    mutable             = false
    required            = false
  }
}

resource "aws_cognito_user_pool_client" "homestays" {
  name         = "homestays-client"
  user_pool_id = aws_cognito_user_pool.homestays.id

  generate_secret = false

  explicit_auth_flows = [
    "ALLOW_USER_PASSWORD_AUTH",
    "ALLOW_USER_SRP_AUTH",
    "ALLOW_REFRESH_TOKEN_AUTH",
  ]

  supported_identity_providers = ["COGNITO"]
}


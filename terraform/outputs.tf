output "hs_uploads_bucket_name" {
  value = aws_s3_bucket.uploads.bucket
}

output "hs_frontend_bucket_name" {
  value = aws_s3_bucket.frontend.bucket
}

output "hs_distribution_url" {
  value = local.cloudfront_url
}

output "hs_distribution_id" {
  value = aws_cloudfront_distribution.app.id
}

output "hs_jdbc_url" {
  value = local.jdbc_url
}

output "hs_alb_dns_name" {
  value = aws_lb.backend.dns_name
}

output "hs_user_pool_id" {
  value = aws_cognito_user_pool.homestays.id
}

output "hs_user_pool_client_id" {
  value = aws_cognito_user_pool_client.homestays.id
}

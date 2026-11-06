resource "aws_cloudfront_function" "spa_rewrite" {
  name    = "${var.hs_project_name}-spa-rewrite"
  runtime = "cloudfront-js-2.0"
  publish = true
  code    = file("${path.module}/spa-rewrite.js")
}

resource "aws_cloudfront_origin_access_control" "s3" {
  name                              = "${var.hs_project_name}-s3-oac"
  description                       = "CloudFront access to private frontend and uploads buckets"
  origin_access_control_origin_type = "s3"
  signing_behavior                  = "always"
  signing_protocol                  = "sigv4"
}

locals {
  cloudfront_cache_optimized_id = "658327ea-f89d-4fab-a63d-7e88639e58f6"
  cloudfront_cache_disabled_id  = "4135ea2d-6df8-44a3-9df3-4b5a84be39ad"
  cloudfront_all_viewer_id      = "216adef6-5c7f-47e4-b989-5492eafa07d3"
}

resource "aws_cloudfront_distribution" "app" {
  enabled             = true
  is_ipv6_enabled     = true
  default_root_object = "index.html"
  comment             = "${var.hs_project_name} frontend and API"

  origin {
    domain_name              = aws_s3_bucket.frontend.bucket_regional_domain_name
    origin_id                = "frontend-s3"
    origin_access_control_id = aws_cloudfront_origin_access_control.s3.id
    s3_origin_config {
      origin_access_identity = ""
    }
  }

  origin {
    domain_name              = aws_s3_bucket.uploads.bucket_regional_domain_name
    origin_id                = "uploads-s3"
    origin_access_control_id = aws_cloudfront_origin_access_control.s3.id
    s3_origin_config {
      origin_access_identity = ""
    }
  }

  origin {
    domain_name = aws_lb.backend.dns_name
    origin_id   = "backend-alb"

    custom_origin_config {
      http_port              = 80
      https_port             = 443
      origin_protocol_policy = "http-only" # Same as CDK: HTTPS viewer -> CloudFront -> HTTP ALB.
      origin_ssl_protocols   = ["TLSv1.2"]
    }
  }

  default_cache_behavior {
    target_origin_id       = "frontend-s3"
    viewer_protocol_policy = "redirect-to-https"
    allowed_methods        = ["GET", "HEAD", "OPTIONS"]
    cached_methods         = ["GET", "HEAD"]
    cache_policy_id        = local.cloudfront_cache_optimized_id
    compress               = true

    function_association {
      event_type   = "viewer-request"
      function_arn = aws_cloudfront_function.spa_rewrite.arn
    }
  }

  ordered_cache_behavior {
    path_pattern             = "/api/*"
    target_origin_id         = "backend-alb"
    viewer_protocol_policy   = "redirect-to-https"
    allowed_methods          = ["GET", "HEAD", "OPTIONS", "PUT", "PATCH", "POST", "DELETE"]
    cached_methods           = ["GET", "HEAD"]
    cache_policy_id          = local.cloudfront_cache_disabled_id
    origin_request_policy_id = local.cloudfront_all_viewer_id
    compress                 = true
  }

  # Additional exact-path behavior: original CDK /api/* does not also match /api.
  ordered_cache_behavior {
    path_pattern             = "/api"
    target_origin_id         = "backend-alb"
    viewer_protocol_policy   = "redirect-to-https"
    allowed_methods          = ["GET", "HEAD", "OPTIONS", "PUT", "PATCH", "POST", "DELETE"]
    cached_methods           = ["GET", "HEAD"]
    cache_policy_id          = local.cloudfront_cache_disabled_id
    origin_request_policy_id = local.cloudfront_all_viewer_id
    compress                 = true
  }

  ordered_cache_behavior {
    path_pattern           = "/images/*"
    target_origin_id       = "uploads-s3"
    viewer_protocol_policy = "redirect-to-https"
    allowed_methods        = ["GET", "HEAD", "OPTIONS"]
    cached_methods         = ["GET", "HEAD"]
    cache_policy_id        = local.cloudfront_cache_optimized_id
    compress               = true
  }

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  viewer_certificate {
    cloudfront_default_certificate = true
  }
}

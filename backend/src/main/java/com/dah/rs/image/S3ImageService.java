package com.dah.rs.image;

import com.dah.rs.exception.BadRequestException;
import com.dah.rs.user.AppRole;
import com.dah.rs.user.AppUser;
import java.net.URI;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Profile("!test")
@Service
public class S3ImageService implements ImageService {

  @Value("${hs.aws.s3.uri}")
  private String awsUri;
  @Value("${hs.aws.s3.bucket}")
  private String bucketName;
  @Value("${hs.aws.s3.presign-duration}")
  private Long presignDuration;
  @Value("${hs.aws.cloudfront.url}")
  private String cloudFrontUrl;
  @Value("${hs.aws.s3.sample.prefix}")
  private String s3Prefix;

  @Override
  public String getCloudFrontUrl() {
    return cloudFrontUrl;
  }

  @Override
  public String getS3SamplePrefix() {
    return s3Prefix;
  }

  public String generateUploadUrl(AppUser host, String objectKey) {
    validateHost(host);
    if (objectKey == null || objectKey.isBlank()) {
      throw new BadRequestException("S3 object key is required");
    }
    S3Presigner presigner = S3Presigner.builder()
      .endpointOverride(URI.create(awsUri))
      .build();
    PresignedPutObjectRequest presignedRequest;
    try {
      PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(presignDuration))
        .putObjectRequest(req -> req.bucket(bucketName).key(objectKey))
        .build();
      presignedRequest = presigner.presignPutObject(presignRequest);
    } finally {
      presigner.close();
    }
    return presignedRequest.url().toString();
  }

  private void validateHost(AppUser host) {
    if (host.getRole() != AppRole.HOST) {
      throw new AccessDeniedException("User role must be HOST");
    }
  }

}

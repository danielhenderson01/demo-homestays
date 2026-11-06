package com.dah.rs.image;

import com.dah.rs.user.AppUser;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("test")
@Service
public class TestImageService implements ImageService {

  @Override
  public String generateUploadUrl(AppUser host, String objectKey) {
    return "upload-url";
  }

  @Override
  public String getCloudFrontUrl() {
    return "";
  }

  @Override
  public String getS3SamplePrefix() {
    return "";
  }

}

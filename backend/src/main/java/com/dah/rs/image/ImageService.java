package com.dah.rs.image;

import com.dah.rs.user.AppUser;

public interface ImageService {

  String generateUploadUrl(AppUser host, String objectKey);

  String getCloudFrontUrl();

  String getS3SamplePrefix();

}

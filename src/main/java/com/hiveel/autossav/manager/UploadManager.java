package com.hiveel.autossav.manager;

import com.hiveel.upload.sdk.service.UploadAudioService;
import com.hiveel.upload.sdk.service.UploadDeleteService;
import com.hiveel.upload.sdk.service.UploadImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UploadManager {

    @Autowired
    private UploadImageService uploadImageService;
    @Autowired
    private UploadDeleteService uploadDeleteService;
    @Autowired
    private UploadAudioService uploadAudioService;

    @Value("${autossav.apikey:hiveel}")
    private String apiKey;
}

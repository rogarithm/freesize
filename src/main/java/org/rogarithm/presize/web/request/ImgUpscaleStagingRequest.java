package org.rogarithm.presize.web.request;

import org.springframework.web.multipart.MultipartFile;

public class ImgUpscaleStagingRequest {
    private String taskId;
    private MultipartFile file;
    private String upscaleRatio;

    public ImgUpscaleStagingRequest() {
    }

    public ImgUpscaleStagingRequest(String taskId, MultipartFile file, String upscaleRatio) {
        this.taskId = taskId;
        this.file = file;
        this.upscaleRatio = upscaleRatio;
    }

    public String getTaskId() {
        return taskId;
    }

    public MultipartFile getFile() {
        return file;
    }

    public String getUpscaleRatio() {
        return upscaleRatio;
    }
}

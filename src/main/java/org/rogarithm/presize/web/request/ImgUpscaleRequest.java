package org.rogarithm.presize.web.request;

import org.springframework.web.multipart.MultipartFile;

public class ImgUpscaleRequest {
    private String taskId;
    private MultipartFile file;
    private String upscaleRatio;

    public ImgUpscaleRequest() {
    }

    public ImgUpscaleRequest(String taskId, MultipartFile file, String upscaleRatio) {
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

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public void setUpscaleRatio(String upscaleRatio) {
        this.upscaleRatio = upscaleRatio;
    }
}

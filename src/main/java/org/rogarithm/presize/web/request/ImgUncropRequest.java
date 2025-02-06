package org.rogarithm.presize.web.request;

import org.springframework.web.multipart.MultipartFile;

public class ImgUncropRequest {
    private String taskId;
    private MultipartFile file;
    private String targetRatio;

    public ImgUncropRequest() {
    }

    public ImgUncropRequest(String taskId, MultipartFile file, String targetRatio) {
        this.taskId = taskId;
        this.file = file;
        this.targetRatio = targetRatio;
    }

    public String getTaskId() {
        return taskId;
    }

    public MultipartFile getFile() {
        return file;
    }

    public String getTargetRatio() {
        return targetRatio;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public void setTargetRatio(String targetRatio) {
        this.targetRatio = targetRatio;
    }
}

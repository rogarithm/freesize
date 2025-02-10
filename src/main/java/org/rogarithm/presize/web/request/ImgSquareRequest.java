package org.rogarithm.presize.web.request;

import org.springframework.web.multipart.MultipartFile;

public class ImgSquareRequest {
    private String taskId;
    private MultipartFile file;
    private String targetRes;

    public ImgSquareRequest() {
    }

    public ImgSquareRequest(String taskId, MultipartFile file, String targetRes) {
        this.taskId = taskId;
        this.file = file;
        this.targetRes = targetRes;
    }

    public String getTaskId() {
        return taskId;
    }

    public MultipartFile getFile() {
        return file;
    }

    public String getTargetRes() {
        return targetRes;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public void setTargetRes(String targetRes) {
        this.targetRes = targetRes;
    }
}

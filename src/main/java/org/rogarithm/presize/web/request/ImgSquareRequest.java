package org.rogarithm.presize.web.request;

import org.rogarithm.presize.config.ErrorCode;
import org.rogarithm.presize.exception.AiModelRequestMakingFailException;
import org.springframework.web.multipart.MultipartFile;

public class ImgSquareRequest {
    private String taskId;
    private byte[] fileBytes;
    private String originalFileName;
    private String targetRes;

    public ImgSquareRequest() {
    }

    public ImgSquareRequest(String taskId, MultipartFile file, String targetRes) {
        try {
            byte[] fileBytes = file.getBytes();
            this.fileBytes = fileBytes;
        } catch (Exception e) {
            throw new AiModelRequestMakingFailException(ErrorCode.SERVER_FAULT);
        }

        this.taskId = taskId;
        this.originalFileName = file.getOriginalFilename();
        this.targetRes = targetRes;
    }

    public String getTaskId() {
        return taskId;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public String getTargetRes() {
        return targetRes;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public void setTargetRes(String targetRes) {
        this.targetRes = targetRes;
    }
}

package org.rogarithm.presize.web.request;

import org.rogarithm.presize.config.ErrorCode;
import org.rogarithm.presize.exception.AiModelRequestMakingFailException;
import org.springframework.web.multipart.MultipartFile;

public class ImgUpscaleRequest {
    private String taskId;
    private byte[] fileBytes;
    private String upscaleRatio;

    public ImgUpscaleRequest() {
    }

    public ImgUpscaleRequest(String taskId, MultipartFile file, String upscaleRatio) {
        try {
            byte[] fileBytes = file.getBytes();
            this.fileBytes = fileBytes;
        } catch (Exception e) {
            throw new AiModelRequestMakingFailException(ErrorCode.SERVER_FAULT);
        }

        this.taskId = taskId;
        this.upscaleRatio = upscaleRatio;
    }

    public String getTaskId() {
        return taskId;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public String getUpscaleRatio() {
        return upscaleRatio;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }

    public void setUpscaleRatio(String upscaleRatio) {
        this.upscaleRatio = upscaleRatio;
    }
}

package org.rogarithm.presize.web.request;

import org.springframework.web.multipart.MultipartFile;

public class ImgUpscaleRequest {
    private MultipartFile file;
    private String upscaleRatio;

    public ImgUpscaleRequest() {
    }

    public ImgUpscaleRequest(MultipartFile file, String upscaleRatio) {
        this.file = file;
        this.upscaleRatio = upscaleRatio;
    }

    public MultipartFile getFile() {
        return file;
    }

    public String getUpscaleRatio() {
        return upscaleRatio;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public void setUpscaleRatio(String upscaleRatio) {
        this.upscaleRatio = upscaleRatio;
    }
}

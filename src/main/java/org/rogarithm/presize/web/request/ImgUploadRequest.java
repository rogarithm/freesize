package org.rogarithm.presize.web.request;

import org.springframework.web.multipart.MultipartFile;

public class ImgUploadRequest {
    private MultipartFile file;
    private String height;
    private String width;

    public ImgUploadRequest() {
    }

    public ImgUploadRequest(MultipartFile file, String height, String width) {
        this.file = file;
        this.height = height;
        this.width = width;
    }

    public MultipartFile getFile() {
        return file;
    }

    public String getHeight() {
        return height;
    }

    public String getWidth() {
        return width;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setWidth(String width) {
        this.width = width;
    }
}

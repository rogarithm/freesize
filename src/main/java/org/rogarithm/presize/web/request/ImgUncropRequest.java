package org.rogarithm.presize.web.request;

import org.springframework.web.multipart.MultipartFile;

public class ImgUncropRequest {
    private MultipartFile file;

    public ImgUncropRequest() {
    }

    public ImgUncropRequest(MultipartFile file) {
        this.file = file;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}

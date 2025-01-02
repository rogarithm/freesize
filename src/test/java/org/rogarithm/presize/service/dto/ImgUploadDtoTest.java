package org.rogarithm.presize.service.dto;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

class ImgUploadDtoTest {

    @Test
    public void check_whether_jpeg_content_is_valid() throws IOException, URISyntaxException {
        URL res = getClass().getClassLoader().getResource("encode_result2");
        String base64Image = new String(Files.readAllBytes(Paths.get(res.toURI())));

        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            // Check JPEG magic bytes
            if (imageBytes[0] == (byte) 0xFF && imageBytes[1] == (byte) 0xD8 && imageBytes[2] == (byte) 0xFF) {
                System.out.println("Valid JPEG image.");
            } else {
                System.out.println("Invalid image.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid Base64 string.");
        }
    }


}
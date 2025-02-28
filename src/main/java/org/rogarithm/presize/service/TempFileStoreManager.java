package org.rogarithm.presize.service;

import org.rogarithm.presize.config.ErrorCode;
import org.rogarithm.presize.exception.StoreTempFileFailException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class TempFileStoreManager {
    public void store(byte[] fileBytes, String originalFilename) throws IOException {

        if (Objects.equals(originalFilename.split("\\.")[1], "webp")) {
            return;
        }

        Path dirPath = Path.of("/tmp/imgs");
        Files.createDirectories(dirPath);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(fileBytes));
        if (image == null) {
            throw new StoreTempFileFailException(ErrorCode.SERVER_FAULT); // "Invalid image file"
        }
        Path imgPath = dirPath.resolve(originalFilename);
        ImageIO.write(image, "png", imgPath.toFile());
    }
}

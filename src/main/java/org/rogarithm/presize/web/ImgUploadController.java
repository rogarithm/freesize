package org.rogarithm.presize.web;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping
public class ImgUploadController {
    private static final Logger log = LoggerFactory.getLogger(ImgUploadController.class);

    @Value("${file.dir}")
    private String fileDir;

    @GetMapping("/upload")
    public String newFile(Model model, ImgUploadRequest request) {
        model.addAttribute("ImgUploadRequest", request);
        return "/upload-img";
    }

    @PostMapping("/upload-file")
    public String saveFile(@RequestBody ImgUploadRequest request) throws IOException {

        service.uploadImg(ImgUploadDto.from(request));
        MultipartFile file = request.getFile();
        log.info("multipartFile={}", file);

        if (!file.isEmpty()) {
            String fullPath = fileDir + File.separator + file.getOriginalFilename();
            log.info("파일 저장 fullPath={}", fullPath);
            file.transferTo(new File(fullPath));
        }

        return "/upload-form";
    }
}

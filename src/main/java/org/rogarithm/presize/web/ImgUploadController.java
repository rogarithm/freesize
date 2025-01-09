package org.rogarithm.presize.web;

import org.rogarithm.presize.service.ImgUploadService;
import org.rogarithm.presize.service.dto.ImgUploadDto;
import org.rogarithm.presize.web.request.ImgUploadRequest;
import org.rogarithm.presize.web.response.ImgUploadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping
public class ImgUploadController {
    private static final Logger log = LoggerFactory.getLogger(ImgUploadController.class);

    @Value("${file.dir}")
    private String fileDir;

    private final ImgUploadService service;

    public ImgUploadController(ImgUploadService service) {
        this.service = service;
    }

    @GetMapping("/upload")
    public String newFile(Model model, ImgUploadRequest request) {
        model.addAttribute("ImgUploadRequest", request);
        return "upload-img";
    }

    @GetMapping("/result")
    public String showResult(@ModelAttribute("resizedImg") String resizedImg, Model model) {
        model.addAttribute("resizedImg", resizedImg);
        return "result-img";
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

        return "upload-form";
    }

    @PostMapping("/upload")
    public String uploadImg(@ModelAttribute("ImgUploadRequest") ImgUploadRequest request, RedirectAttributes redirectAttributes) {
        ImgUploadDto from = new ImgUploadDto(request.getFile(), "1240", "1240");
        ImgUploadResponse response = service.uploadImg(from);

        if (response.isSuccess()) {
            redirectAttributes.addFlashAttribute("originalImg", from.getImg());
            redirectAttributes.addFlashAttribute("resizedImg", response.getResizedImg());
            return "redirect:result";
        } else {
            redirectAttributes.addFlashAttribute("error", "Image processing failed: " + response.getMessage());
            return "redirect:upload";
        }
    }
}

package org.rogarithm.presize.web;

import org.rogarithm.presize.service.ImgUploadService;
import org.rogarithm.presize.service.dto.ImgUploadDto;
import org.rogarithm.presize.web.request.ImgUploadRequest;
import org.rogarithm.presize.web.response.ImgUploadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping
public class ImgUploadController {
    private static final Logger log = LoggerFactory.getLogger(ImgUploadController.class);

    private final ImgUploadService service;

    public ImgUploadController(ImgUploadService service) {
        this.service = service;
    }

    @GetMapping("/upload")
    public String newFile(Model model, ImgUploadRequest request) {
        model.addAttribute("ImgUploadRequest", request);
        return "upload-img";
    }

    @GetMapping("/upscale")
    public String showResult(@ModelAttribute("resizedImg") String resizedImg, Model model) {
        model.addAttribute("resizedImg", resizedImg);
        return "upscale-result";
    }

    @PostMapping("/upscale")
    public String uploadImg(@ModelAttribute("ImgUploadRequest") ImgUploadRequest request, RedirectAttributes redirectAttributes) {
        ImgUploadDto from = new ImgUploadDto(request.getFile(), "1240", "1240");
        ImgUploadResponse response = service.uploadImg(from);

        if (response.isSuccess()) {
            redirectAttributes.addFlashAttribute("originalImg", from.getImg());
            redirectAttributes.addFlashAttribute("resizedImg", response.getResizedImg());
            return "redirect:upscale";
        } else {
            redirectAttributes.addFlashAttribute("error", "Image processing failed: " + response.getMessage());
            return "redirect:upload";
        }
    }
}

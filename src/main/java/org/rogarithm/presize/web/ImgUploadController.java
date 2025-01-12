package org.rogarithm.presize.web;

import org.rogarithm.presize.service.ImgUploadService;
import org.rogarithm.presize.service.dto.ImgUncropDto;
import org.rogarithm.presize.service.dto.ImgUploadDto;
import org.rogarithm.presize.web.request.ImgUncropRequest;
import org.rogarithm.presize.web.request.ImgUploadRequest;
import org.rogarithm.presize.web.response.ImgUncropResponse;
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
    public String newFile(Model model, ImgUploadRequest request, ImgUncropRequest uncropRequest) {
        model.addAttribute("ImgUploadRequest", request);
        model.addAttribute("ImgUncropRequest", uncropRequest);
        return "upload-img";
    }

    @GetMapping("/upscale")
    public String showResult(@ModelAttribute("resizedImg") String resizedImg, Model model) {
        model.addAttribute("resizedImg", resizedImg);
        return "upscale-result";
    }

    @GetMapping("/uncrop")
    public String showUncropResult(@ModelAttribute("uncropImg") String uncropImg, Model model) {
        model.addAttribute("uncropImg", uncropImg);
        return "uncrop-result";
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

    @PostMapping("/uncrop")
    public String uncropImg(@ModelAttribute("ImgUncropRequest") ImgUncropRequest request, RedirectAttributes redirectAttributes) {
        ImgUncropDto from = new ImgUncropDto(request.getFile());
        ImgUncropResponse response = service.uncropImg(from);

        if (response.isSuccess()) {
            redirectAttributes.addFlashAttribute("originalImg", from.getImg());
            redirectAttributes.addFlashAttribute("uncropImg", response.getUncropImg());
            return "redirect:uncrop";
        } else {
            redirectAttributes.addFlashAttribute("error", "Image processing failed: " + response.getMessage());
            return "redirect:upload";
        }
    }
}

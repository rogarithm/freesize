package org.rogarithm.presize.web;

import org.rogarithm.presize.service.ExternalApiRequester;
import org.rogarithm.presize.service.ImgPolishService;
import org.rogarithm.presize.service.dto.ImgUncropDto;
import org.rogarithm.presize.service.dto.ImgUpscaleDto;
import org.rogarithm.presize.web.request.ImgUncropRequest;
import org.rogarithm.presize.web.request.ImgUpscaleRequest;
import org.rogarithm.presize.web.response.ImgUncropResponse;
import org.rogarithm.presize.web.response.ImgUpscaleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/test")
public class ImgPolishPageController {
    private static final Logger log = LoggerFactory.getLogger(ImgPolishPageController.class);

    private final ImgPolishService service;
    private final ExternalApiRequester externalApiRequester;

    public ImgPolishPageController(ImgPolishService service, ExternalApiRequester externalApiRequester) {
        this.service = service;
        this.externalApiRequester = externalApiRequester;
    }

    @GetMapping("/upload")
    public String newFile(Model model, ImgUpscaleRequest upscaleRequest, ImgUncropRequest uncropRequest) {
        upscaleRequest.setTaskId("x");
        upscaleRequest.setUpscaleRatio("x2");
        model.addAttribute("ImgUpscaleRequest", upscaleRequest);

        uncropRequest.setTaskId("y");
        uncropRequest.setTargetRatio("1:2");
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
    public String uploadImg(@ModelAttribute("ImgUpscaleRequest") ImgUpscaleRequest request, RedirectAttributes redirectAttributes) {
        ImgUpscaleDto dto = ImgUpscaleDto.from(request);
        ImgUpscaleResponse response = externalApiRequester.upscaleImg(dto);

        if (response.isSuccess()) {
            redirectAttributes.addFlashAttribute("originalImg", dto.getImg());
            redirectAttributes.addFlashAttribute("resizedImg", response.getResizedImg());
            return "redirect:upscale";
        } else {
            redirectAttributes.addFlashAttribute("error", "Image processing failed: " + response.getMessage());
            return "redirect:upload";
        }
    }

    @PostMapping("/uncrop")
    public String uncropImg(@ModelAttribute("ImgUncropRequest") ImgUncropRequest request, RedirectAttributes redirectAttributes) {
        ImgUncropDto dto = ImgUncropDto.from(request);
        ImgUncropResponse response = externalApiRequester.uncropImg(dto);

        if (response.isSuccess()) {
            redirectAttributes.addFlashAttribute("originalImg", dto.getImg());
            redirectAttributes.addFlashAttribute("uncropImg", response.getResizedImg());
            return "redirect:uncrop";
        } else {
            redirectAttributes.addFlashAttribute("error", "Image processing failed: " + response.getMessage());
            return "redirect:upload";
        }
    }
}

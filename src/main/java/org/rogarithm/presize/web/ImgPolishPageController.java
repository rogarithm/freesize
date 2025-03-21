package org.rogarithm.presize.web;

import org.rogarithm.presize.service.ExternalApiRequester;
import org.rogarithm.presize.service.ImgPolishService;
import org.rogarithm.presize.web.request.ImgUncropRequest;
import org.rogarithm.presize.web.request.ImgUpscaleRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

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
}

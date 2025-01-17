package org.rogarithm.presize.web;

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
import org.springframework.web.bind.annotation.*;

@Controller
@ResponseBody
@RequestMapping
public class ImgPolishController {
    private static final Logger log = LoggerFactory.getLogger(ImgPolishController.class);

    private final ImgPolishService service;

    public ImgPolishController(ImgPolishService service) {
        this.service = service;
    }

    @PostMapping("/upscale")
    public ImgUpscaleResponse uploadImg(@ModelAttribute("ImgUpscaleRequest") ImgUpscaleRequest request) {
        ImgUpscaleDto from = ImgUpscaleDto.from(request);
        return service.uploadImg(from);
    }

    @PostMapping("/uncrop")
    public ImgUncropResponse uncropImg(@ModelAttribute("ImgUncropRequest") ImgUncropRequest request) {
        ImgUncropDto from = ImgUncropDto.from(request);
        return service.uncropImg(from);
    }
}

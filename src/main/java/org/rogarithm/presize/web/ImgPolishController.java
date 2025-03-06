package org.rogarithm.presize.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.rogarithm.presize.service.ImgPolishService;
import org.rogarithm.presize.service.ImgUploadService;
import org.rogarithm.presize.web.request.ImgSquareRequest;
import org.rogarithm.presize.web.request.ImgUncropRequest;
import org.rogarithm.presize.web.request.ImgUpscaleRequest;
import org.rogarithm.presize.web.response.HealthCheckResponse;
import org.rogarithm.presize.web.response.PollingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;

@Controller
@ResponseBody
@RequestMapping
public class ImgPolishController {
    private static final Logger log = LoggerFactory.getLogger(ImgPolishController.class);

    private final ImgPolishService polishService;
    private final ImgUploadService uploadService;

    public ImgPolishController(ImgPolishService polishService, ImgUploadService uploadService) {
        this.polishService = polishService;
        this.uploadService = uploadService;
    }

    @PostMapping("/uncrop")
    public PollingResponse uncropImg(
            @RequestParam("taskId") String taskId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("targetRatio") String targetRatio,
            HttpServletRequest httpServletRequest
    ) {
        logRequest(httpServletRequest);

        HealthCheckResponse healthCheckResponse = polishService.healthCheck();

        if (!healthCheckResponse.isSuccess()) {
            return new PollingResponse(500, "ai model has problem", "no url");
        }

        ImgUncropRequest request = new ImgUncropRequest(taskId, file, targetRatio);
        polishService.uncropImgAsync(request);

        return new PollingResponse(200, "wait", uploadService.makeUrl(request.getTaskId()));
    }

    @PostMapping("/health-check")
    public HealthCheckResponse healthCheck() {
        return polishService.healthCheck();
    }

    @PostMapping(value = "/upscale", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PollingResponse upscaleImg(
            @RequestParam("taskId") String taskId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("upscaleRatio") String upscaleRatio,
            HttpServletRequest httpServletRequest
    ) {
        logRequest(httpServletRequest);

        HealthCheckResponse healthCheckResponse = polishService.healthCheck();

        if (!healthCheckResponse.isSuccess()) {
            return new PollingResponse(500, "ai model has problem", "no url");
        }

        ImgUpscaleRequest request = new ImgUpscaleRequest(taskId, file, upscaleRatio);
        polishService.upscaleImgAsync(request);
        return new PollingResponse(200, "wait", uploadService.makeUrl(request.getTaskId()));
    }

    @PostMapping(value = "/square", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PollingResponse squareImg(
            @RequestParam("taskId") String taskId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("targetRes") String targetRes,
            HttpServletRequest httpServletRequest
    ) throws FileUploadException {
        logRequest(httpServletRequest);

        HealthCheckResponse healthCheckResponse = polishService.healthCheck();

        if (!healthCheckResponse.isSuccess()) {
            return new PollingResponse(500, "ai model has problem", "no url");
        }

        ImgSquareRequest request = new ImgSquareRequest(taskId, file, targetRes);
        polishService.squareImgAsync(request);
        return new PollingResponse(200, "wait", uploadService.makeUrl(request.getTaskId()));
    }

    private void logRequest(final HttpServletRequest httpServletRequest) {
        log.info("===== REQUEST HEADERS =====");
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            log.info("{}: {}", header, httpServletRequest.getHeader(header));
        }

        log.info("===== REQUEST PARAMETERS =====");
        Map<String, String[]> paramMap = httpServletRequest.getParameterMap();
        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            log.info("{}: {}", entry.getKey(), Arrays.toString(entry.getValue()));
        }

        log.info("===== REQUEST PARTS (Multipart) =====");
        try {
            Collection<Part> parts = httpServletRequest.getParts();
            for (Part part : parts) {
                log.info("Part Name: {}, Size: {}", part.getName(), part.getSize());
            }
        } catch (Exception e) {
            log.error("Error reading multipart request", e);
        }
    }
}

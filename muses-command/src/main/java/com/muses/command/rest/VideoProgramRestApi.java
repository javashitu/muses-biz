package com.muses.command.rest;

import com.muses.adapter.service.IVideoService;
import com.muses.domain.rest.request.LikeVideoRequest;
import com.muses.domain.rest.request.ListVideoRequest;
import com.muses.domain.rest.request.PlayVideoRequest;
import com.muses.domain.rest.request.PubVideoRequest;
import com.muses.domain.rest.response.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName RestApi
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/6 19:18
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/muses/program/video")
public class VideoProgramRestApi {

    @Autowired
    private IVideoService videoService;

    @PostMapping("/pub")
    public ApiResult pubVideo(@RequestBody @Valid PubVideoRequest request) {
        log.info("pub video program {}", request);
        PubVideoResponse response = videoService.pubVideo(request);
        return ApiResult.success(response);
    }

    @PostMapping("/list")
    public ApiResult listVideo(@RequestBody @Valid ListVideoRequest request) {
        log.info("list video program {}", request);
        ListVideoResponse response = videoService.listVideo(request);
        return ApiResult.success(response);
    }

    @PostMapping("/play")
    public ApiResult playVideo(@RequestBody @Valid PlayVideoRequest request) {
        log.info("play video program {}", request);
        PlayVideoResponse response = videoService.playVideo(request);
        return ApiResult.success(response);
    }

    @PostMapping("/like")
    public ApiResult likeVideo(@RequestBody @Valid LikeVideoRequest request) {
        log.info("pub video program {}", request);
        LikeVideoResponse response = videoService.likeVideo(request);
        return ApiResult.success(response);
    }


    @PostMapping("/recommend")
    public ApiResult recommendVideo(@RequestBody @Valid ListVideoRequest request) {
        log.info("recommend video program {}", request);
        ListVideoResponse response = videoService.recommendVideo(request);
        return ApiResult.success(response);
    }

}

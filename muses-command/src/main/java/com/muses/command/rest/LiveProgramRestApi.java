package com.muses.command.rest;

import com.muses.adapter.service.ILiveService;
import com.muses.domain.rest.request.ListLiveRequest;
import com.muses.domain.rest.request.ListOtherLiveRequest;
import com.muses.domain.rest.request.PubLiveRequest;
import com.muses.domain.rest.request.QueryLiveRequest;
import com.muses.domain.rest.response.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @ClassName LiveProgramRestApi
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/9 14:00
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/muses/program/live")
public class LiveProgramRestApi {

    @Autowired
    private ILiveService liveService;

    @PostMapping("/pub")
    public ApiResult pubLive(@RequestBody @Valid PubLiveRequest request) {
        log.info("pub live program {}", request);
        PubLiveResponse response = liveService.pubLive(request);
        return ApiResult.success(response);
    }


    /**
     * 严格来说这里是查询出加入直播的信息
     */
    @PostMapping("/query")
    public ApiResult queryLive(@RequestBody @Valid QueryLiveRequest request) {
        log.info("query live program {}", request);
        QueryLiveResponse response = liveService.queryLive(request);
        return ApiResult.success(response);
    }

    @PostMapping("/list")
    public ApiResult listLive(@RequestBody @Valid ListLiveRequest request) {
        log.info("list live program {}", request);
        ListLiveResponse response = liveService.listLive(request);
        return ApiResult.success(response);
    }


    @PostMapping("/list/other")
    public ApiResult listLive(@RequestBody @Valid ListOtherLiveRequest request) {
        log.info("list live program {}", request);
        ListOtherLiveResponse response = liveService.listOtherLive(request);
        return ApiResult.success(response);
    }

    @GetMapping("/rtc/query/{roomId}")
    public ApiResult queryLive(@PathVariable String roomId) {
        log.info("query live info {}", roomId);
        Map<String, Object> map = liveService.queryLiveInfo(roomId);
        return ApiResult.success(map);
    }
}

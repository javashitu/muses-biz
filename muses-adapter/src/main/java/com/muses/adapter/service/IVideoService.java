package com.muses.adapter.service;


import com.muses.domain.kafka.message.VideoTranscodeMessage;
import com.muses.domain.rest.request.LikeVideoRequest;
import com.muses.domain.rest.request.ListVideoRequest;
import com.muses.domain.rest.request.PlayVideoRequest;
import com.muses.domain.rest.request.PubVideoRequest;
import com.muses.domain.rest.response.LikeVideoResponse;
import com.muses.domain.rest.response.ListVideoResponse;
import com.muses.domain.rest.response.PlayVideoResponse;
import com.muses.domain.rest.response.PubVideoResponse;

/**
 * @ClassName IVideoService
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/7 9:54
 */
public interface IVideoService {
    PubVideoResponse pubVideo(PubVideoRequest request);

    ListVideoResponse listVideo(ListVideoRequest request);

    PlayVideoResponse playVideo(PlayVideoRequest request);

    LikeVideoResponse likeVideo(LikeVideoRequest request);

    ListVideoResponse recommendVideo(ListVideoRequest request);

    boolean updateTranscodeState(VideoTranscodeMessage message);

}

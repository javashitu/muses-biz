package com.muses.domain.servicce.proto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName Stream
 * @Description: 能表示sub/pub stream对象根据pubSubFlag区分
 * @Author: java使徒
 * @CreateDate: 2024/10/15 18:56
 */
@Data
@NoArgsConstructor
public class Stream {

    private String streamId;

    //发布者id
    private String userId;

    //pub stream还是sub stream, true: pub, false: sub
    private boolean pubFlag;

    private boolean audio;

    private boolean video;

    @Builder
    public Stream(String streamId, String userId, boolean pubFlag, boolean audio, boolean video) {
        this.streamId = streamId;
        this.userId = userId;
        this.pubFlag = pubFlag;
        this.audio = audio;
        this.video = video;
    }
}

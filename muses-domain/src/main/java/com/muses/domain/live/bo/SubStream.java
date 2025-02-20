package com.muses.domain.live.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @ClassName SubStream
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/2 13:47
 */
@Data
@ToString
@Builder
@AllArgsConstructor
public class SubStream {

    private String userId;

    private String subTargetUserId;

    private String streamId;

    private String subTargetStreamId;
}

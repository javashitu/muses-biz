package com.muses.domain.live.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @ClassName RtcStream
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/31 16:42
 */
@Data
@Builder
@AllArgsConstructor
public class PubStream {

    private String pubUserId;

    private String streamId;

    //不用原子工具，节省内存，且没必要
    private boolean audioFlag;

    private boolean videoFlag;

    private boolean shareDeskFlag;

}

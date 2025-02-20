package com.muses.domain.rest.response.info;

import lombok.Data;

import java.util.Map;

/**
 * @ClassName LiveRoomAddress
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/13 11:06
 */
@Data
public class LiveRoomAddress {
    private String userId;

    private String liveRoomId;

    private String addressUrl;

    private Map<String,String> addressOption;
}

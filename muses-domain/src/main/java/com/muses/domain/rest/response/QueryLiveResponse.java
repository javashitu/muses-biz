package com.muses.domain.rest.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @ClassName QueryLiveResponse
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/13 10:39
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryLiveResponse {

    private String rtcUserId;

    private String liveProgramId;

    private String liveRoomId;

    private String roomName;

    private String roomDesc;

    private String cover;

    private String type;

    private int state;

    private Map<String, String> liveAddress;

}

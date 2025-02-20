package com.muses.domain.rest.response.info;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @ClassName LiveProgramInfo
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/11/14 15:28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveProgramInfo {

    private String createUserId;

    private String liveProgramId;

    private String liveRoomId;

    private String roomName;

    private String roomDesc;

    private String cover;

    private String type;

    private int state;

    private Map<String, String> liveAddress;
}

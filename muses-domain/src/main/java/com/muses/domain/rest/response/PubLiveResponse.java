package com.muses.domain.rest.response;

import com.muses.domain.rest.response.info.LiveProgramInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName PubLiveResponse
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/9 16:23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PubLiveResponse {

//    private String liveProgramId;
//
//    private String liveRoomId;
//
//    private String rtcUserId;
//
//    private Map<String, String> liveAddress;

    private LiveProgramInfo liveProgramInfo;

}

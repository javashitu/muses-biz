package com.muses.domain.rest.response;

import com.muses.domain.rest.response.info.LiveProgramInfo;
import lombok.Data;

import java.util.List;

/**
 * @ClassName ListOtherLiveResponse
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/11/19 10:02
 */
@Data
public class ListOtherLiveResponse {

    private List<LiveProgramInfo> liveProgramInfoList;

}

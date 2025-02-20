package com.muses.domain.rest.response;

import com.muses.domain.rest.response.info.LiveProgramInfo;
import lombok.Data;

import java.util.List;

/**
 * @ClassName ListLiveResponse
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/11/14 15:27
 */
@Data
public class ListLiveResponse {

    private List<LiveProgramInfo> liveProgramInfoList;

}

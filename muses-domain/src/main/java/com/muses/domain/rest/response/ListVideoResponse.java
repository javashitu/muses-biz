package com.muses.domain.rest.response;

import com.muses.domain.rest.response.info.VideoProgramInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName ListVideoResponse
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/10 17:47
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListVideoResponse {
    private String userId;

    private List<VideoProgramInfo> videoProgramInfoList;

}

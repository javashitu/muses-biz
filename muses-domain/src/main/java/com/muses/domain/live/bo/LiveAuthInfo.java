package com.muses.domain.live.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName LiveAuthInfo
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/12 14:26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LiveAuthInfo {

    private String userId;

    private String roomId;

}

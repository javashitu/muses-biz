package com.muses.domain.rest.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName PlayVideoResponse
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/11 14:46
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayVideoResponse {

    private String userId;

    private int play;
}

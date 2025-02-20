package com.muses.domain.rest.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName LikeVideoResponse
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/11 14:52
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeVideoResponse {

    private String userId;

    private int likes;
}

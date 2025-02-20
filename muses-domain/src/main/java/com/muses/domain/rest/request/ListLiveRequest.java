package com.muses.domain.rest.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @ClassName ListLiveRequest
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/11/14 15:24
 */
@Data
public class ListLiveRequest {

    @NotBlank(message = "param userId can't be null")
    private String userId;

    private int pageNum;
    /**
     * 其他参数，类似分区，地点，之后再加
     */

}

package com.muses.domain.rest.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @ClassName ListOtherLiveRequest
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/11/19 10:02
 */
@Data
public class ListOtherLiveRequest {

    @NotBlank(message = "param userId can't be null")
    private String userId;

    private int pageNum;
    /**
     * 其他参数，类似分区，地点，之后再加
     */
}

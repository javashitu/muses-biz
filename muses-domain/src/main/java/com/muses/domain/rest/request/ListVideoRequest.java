package com.muses.domain.rest.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @ClassName ListVideoReuqest
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/10 17:41
 */
@Data
public class ListVideoRequest {

    @NotBlank(message = "param userId can't be null")
    private String userId;

    private int pageNum;

}

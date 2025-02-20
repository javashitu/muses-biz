package com.muses.domain.rest.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @ClassName QueryLiveRequest
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/13 10:38
 */
@Data
public class QueryLiveRequest {

    @NotBlank(message = "param userId can't be null")
    private String userId;

    @NotBlank(message = "param liveProgramId can't be null")
    private String liveProgramId;
}

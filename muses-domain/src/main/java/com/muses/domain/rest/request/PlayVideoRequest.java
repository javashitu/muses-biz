package com.muses.domain.rest.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @ClassName PlayVideoRequest
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/11 14:09
 */
@Data
public class PlayVideoRequest {

    @NotBlank(message = "param userId can't be null")
    private String userId;

    @NotBlank(message = "param videoProgramId can't be null")
    private String  videoProgramId;
}

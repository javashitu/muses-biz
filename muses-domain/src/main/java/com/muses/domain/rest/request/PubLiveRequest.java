package com.muses.domain.rest.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @ClassName PubLiveRequest
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/9 16:23
 */
@Data
public class PubLiveRequest {

    @NotBlank(message = "param userId can't be null")
    private String userId;

    private String roomName;

    private String roomDesc;

    private String cover;

    @NotBlank(message="param partition can't be null")
    private String partition;

    private boolean recordLive;

    @NotBlank
    private String type;

}

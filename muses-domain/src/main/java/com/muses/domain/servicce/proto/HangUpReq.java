package com.muses.domain.servicce.proto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.muses.domain.servicce.constants.ProtoConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @ClassName HangupRequest
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/2 10:15
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName(ProtoConstant.PROTO_HANGUP)
public class HangUpReq extends BaseReq {

    private boolean pubStreamFlag;

    private String roomId;

    private String userId;

    private String userName;

    private String streamId;
}

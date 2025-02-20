package com.muses.domain.servicce.proto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.muses.domain.servicce.constants.ProtoConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName PubReq
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/18 6:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName(ProtoConstant.PROTO_PUB)
public class PubReq extends BaseReq {

    private String roomId;

    private String userId;

    private String signalType;

    private Object signalMessage;

    private Stream pubStream;
}

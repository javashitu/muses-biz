package com.muses.domain.servicce.proto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.muses.domain.servicce.constants.ProtoConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName NegoReq
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/24 11:08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName(ProtoConstant.PROTO_NEGO)
public class NegoReq extends BaseReq{
    private String roomId;

    private String userId;

    private String userName;

    //发起协商的流是pubStream还是subStream，true: pubStream,false: subStream
    private boolean negoPubFlag;

    private Stream pubStream;

    private Stream subStream;

    private String signalType;

    private Object signalMessage;

}

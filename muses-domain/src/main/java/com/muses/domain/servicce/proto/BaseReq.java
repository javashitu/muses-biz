package com.muses.domain.servicce.proto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.muses.domain.servicce.constants.ProtoConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @ClassName BaseReq
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/17 10:31
 */
@Data
//@Builder
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "protoType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = EnterReq.class, name = ProtoConstant.PROTO_ENTER),
        @JsonSubTypes.Type(value = LeaveReq.class, name = ProtoConstant.PROTO_LEAVE),
        @JsonSubTypes.Type(value = PubReq.class, name = ProtoConstant.PROTO_PUB),
        @JsonSubTypes.Type(value = SubReq.class, name = ProtoConstant.PROTO_SUB),
        @JsonSubTypes.Type(value = NegoReq.class, name = ProtoConstant.PROTO_NEGO),
})
public abstract class BaseReq {
    protected String protoType;
}

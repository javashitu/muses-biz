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
 * @ClassName CloseReq
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2025/3/21 16:40
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName(ProtoConstant.PROTO_CLOSE)
public class CloseReq extends BaseReq {
    private String roomId;

    private String userId;

    private String userName;

}

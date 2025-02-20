package com.muses.domain.servicce.proto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName BaseRsp
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/17 10:37
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseRsp {
    protected String protoType;
}

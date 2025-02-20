package com.muses.domain.servicce.proto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName BaseEvent
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/18 16:21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEvent{
    protected String protoType;
}

package com.muses.domain.servicce.proto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName SubReq
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/18 6:03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubReq extends BaseReq {

    private String roomId;

    private String userId;

    //发布的sub流
    private Stream subStream;

    //sub流sub的对象
    private Stream pubStream;

    private String signalType;

    private Object signalMessage;

}

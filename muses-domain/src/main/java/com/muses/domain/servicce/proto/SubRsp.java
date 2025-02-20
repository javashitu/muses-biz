package com.muses.domain.servicce.proto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName SubRsp
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/18 6:09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubRsp extends BaseRsp {

    private String roomId;

}

package com.muses.domain.servicce.proto;

import com.muses.domain.servicce.enums.ProtoTypeEnums;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName LeaveRsp
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/17 22:47
 */
@Data
@NoArgsConstructor
public class LeaveRsp extends BaseRsp{

    private String roomId;

    private String leaveUserId;

    @Builder
    public LeaveRsp(String roomId, String leaveUserId) {
        super(ProtoTypeEnums.LEAVE.getRspType());
        this.roomId = roomId;
        this.leaveUserId = leaveUserId;
    }
}

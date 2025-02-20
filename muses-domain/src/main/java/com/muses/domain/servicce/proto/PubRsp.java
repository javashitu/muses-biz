package com.muses.domain.servicce.proto;

import com.muses.domain.servicce.enums.ProtoTypeEnums;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName PubRsp
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/18 6:06
 */
@Data
@NoArgsConstructor
public class PubRsp extends BaseRsp {

    private String roomId;

    private String userId;

    private Stream pubStream;

    @Builder
    public PubRsp(String roomId, String userId, Stream pubStream) {
        super(ProtoTypeEnums.PUB.getRspType());
        this.roomId = roomId;
        this.userId = userId;
        this.pubStream = pubStream;
    }
}

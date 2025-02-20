package com.muses.domain.servicce.proto;

import com.google.common.collect.Maps;
import com.muses.domain.live.bo.Constraints;
import com.muses.domain.live.bo.FlutterRtcMediaConf;
import com.muses.domain.live.bo.LivePeerConfig;
import com.muses.domain.servicce.enums.ProtoTypeEnums;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName EnterRsp
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/17 10:52
 */
@Data
@NoArgsConstructor
public class EnterRsp extends BaseRsp {

    private String roomId;

    private String userId;

    private Set<String> otherUsers;

    //这个属性适用于web
    private LivePeerConfig peerConfig;

    //这个属性适用于web
    private Constraints constraints;

    private FlutterRtcMediaConf flutterRtcMediaConf;

    private Map<String, List<String>> userPubMap = Maps.newHashMap();

    private boolean anchorFlag;

    @Builder
    public EnterRsp(String roomId, String userId, Set<String> otherUsers, LivePeerConfig peerConfig, Constraints constraints, FlutterRtcMediaConf flutterRtcMediaConf, Map<String, List<String>> userPubMap, boolean anchorFlag) {
        super(ProtoTypeEnums.ENTER.getRspType());
        this.roomId = roomId;
        this.userId = userId;
        this.otherUsers = otherUsers;
        this.peerConfig = peerConfig;
        this.constraints = constraints;
        this.flutterRtcMediaConf = flutterRtcMediaConf;
        this.userPubMap = userPubMap;
        this.anchorFlag = anchorFlag;
    }
}

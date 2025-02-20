package com.muses.domain.live.bo;

import lombok.Data;

import java.util.List;

/**
 * @ClassName PeerConfig
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/13 14:31
 */
@Data
public class LivePeerConfig {

    private String iceTransportPolicy;

    private String bundlePolicy;

    private String rtcpMuxPolicy;

    private List<IceServer> iceServers;
}

package com.muses.service.live.config;

import com.muses.domain.live.bo.Constraints;
import com.muses.domain.live.bo.LivePeerConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName LiveConfig
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/10 15:57
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.live")
public class LiveConfig {

    private int anchorExtendTime;

    private int audienceExtendTime;

    private int roomExtendTime;

    //应该是每种直播类型一个的，但是这里只做了一个
    private LivePeerConfig livePeerConfig;

    private Constraints constraints;

}

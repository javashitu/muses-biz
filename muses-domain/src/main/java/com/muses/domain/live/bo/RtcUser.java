package com.muses.domain.live.bo;

import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName RTCUser
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/18 15:36
 */
@Data
@NoArgsConstructor
public class RtcUser {

    private String id;

    private String name;

    private boolean audioPub;

    private boolean videoPub;

    private long timeout;

    //key userid, value streamId
    private Map<String, List<String>> subMap;

    @Builder
    public RtcUser(String id, String name, boolean audioPub, boolean videoPub, long timeout, Map<String, List<String>> subMap) {
        this.id = id;
        this.name = name;
        this.audioPub = audioPub;
        this.videoPub = videoPub;
        this.timeout = timeout;
        this.subMap = subMap;
    }

    public void pubStream(){
        audioPub = true;
        videoPub = true;
    }

    public synchronized void subStream(String userId, String streamId){
        if(subMap == null){
            subMap = new ConcurrentHashMap<>();
        }
        subMap.compute(userId, (key, value) ->{
            if(value == null){
                value = Lists.newArrayList();
            }
            value.add(streamId);
            return value;
        });
    }

    public void extendTimeout(long timeout){
        this.timeout = timeout;
    }
}

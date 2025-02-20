package com.muses.domain.live.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName FlutterRtcMediaConf
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/11/9 16:35
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlutterRtcMediaConf {

    /**
     *       "video": {
     *         'mandatory': {
     *           'minWidth':
     *               '1280', // Provide your own width, height and frame rate here
     *           'minHeight': '720',
     *           'minFrameRate': '30',
     *         },
     *       }
     */
    private Map<String, Object> videoMandatory;

    private boolean audio;

    public FlutterRtcMediaConf(Constraints constraints) {
        convertConstraints(constraints);
    }

    public void convertConstraints(Constraints constraints){
        audio = constraints.isAudio();
        if(videoMandatory == null){
            videoMandatory = new HashMap<>();
        }
        videoMandatory.put("minWidth", constraints.getVideo().get("width"));
        videoMandatory.put("minHeight", constraints.getVideo().get("height"));
        videoMandatory.put("minFrameRate", constraints.getVideo().get("frameRate"));
    }
}

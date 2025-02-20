package com.muses.domain.rest.response.info;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName VideoInfo
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/10 17:55
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoProgramInfo {

    private String id;

    private String title;

    private String description;

    private String videoUrl;

    private String coverUrl;

    private String themes;

    private String userId;

    //短视频/长视频
    private String type;

    //状态机，视频有审核之类的
    private int state;

    private int play;

    private int likes;

    private int commentCount;

    private int share;

}

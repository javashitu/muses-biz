package com.muses.persistence.mysql.entity;

import com.muses.common.util.DateTimeUtils;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName VideoProgramInteract
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/11/4 16:20
 */
@Data
@ToString
@Table(name= "video_program_interact")
@Entity(name = "video_program_interact")
@NoArgsConstructor
public class VideoProgramInteract extends BaseEntity {

    @Id
    private String id;

    private String userId;

    private int play;

    private int finishPlay;

    private int likes;

    private int share;

    /**
     * 评论数量
     */
    private int commentCount;

    /**
     * 弹幕
     */
    private int barrageCommentCount;

    private int onlineUser;

    private int version;

    @Builder
    public VideoProgramInteract(String id, String userId, int play, int finishPlay, int likes, int share, int commentCount, int barrageCommentCount, int onlineUser) {
        this.id = id;
        this.userId = userId;
        this.play = play;
        this.finishPlay = finishPlay;
        this.likes = likes;
        this.share = share;
        this.commentCount = commentCount;
        this.barrageCommentCount = barrageCommentCount;
        this.onlineUser = onlineUser;

        this.createTime = DateTimeUtils.currentTime();
        this.modifyTime = createTime;
        this.delFlag = StringUtils.EMPTY;
    }
}

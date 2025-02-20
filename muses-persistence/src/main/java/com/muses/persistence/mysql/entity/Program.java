package com.muses.persistence.mysql.entity;

import com.muses.common.util.DateTimeUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName Program
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/8 19:15
 */
@Data
@ToString
@Table(name = "program")
@Entity(name = "program")
@NoArgsConstructor
public class Program extends BaseEntity {

    /**
     * 如果复用节目实际类目的id，会有哪些问题？ TODO
     */
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "user_id")
    private String userId;

    /**
     * twitter转载一条视频，自己创建的还是图文，创建类型应该是图文还是视频
     */
    @Column(name = "create_type")
    private String createType;

    @Column(name = "type")
    private String type;

    @Column(name="video_program_id")
    private String videoProgramId;

    @Column(name="live_program_id")
    private String liveProgramId;

    /**
     * 图文类节目，可以携带图片文字
     */
    @Column(name = "text_program_id")
    private String textProgramId;

    /**
     * 投票节目,发起投票
     */
    @Column(name = "vote_program_id")
    private String voteProgramId;

    /**
     * 活动节目，组织活动，由若干成员参与
     */
    @Column(name="activity_program_id")
    private String activityProgramId;

    /**
     * 关联id,分享，联合类节目需要这个字段
     */
    @Column(name= "relevance_id")
    private String relevanceId;

    /**
     *  不同类型的节目状态机都不一样，节目本身的状态机只决定能不能看得到
     */
    @Column(name="state")
    private int state;

    @Column(name= "pub_time")
    private long pubTime;

    @Column(name = "version")
    private int version;

    @Builder
    public Program(String id,String userId, String createType, String type, String videoProgramId, String liveProgramId, String textProgramId, String voteProgramId, String activityProgramId, String relevanceId, int state, long pubTime, int version) {
        this.id = id;
        this.userId = userId;
        this.createType = createType;
        this.type = type;
        this.videoProgramId = videoProgramId;
        this.liveProgramId = liveProgramId;
        this.textProgramId = textProgramId;
        this.voteProgramId = voteProgramId;
        this.activityProgramId = activityProgramId;
        this.relevanceId = relevanceId;
        this.state = state;
        this.pubTime = pubTime;
        this.version = version;

        this.createTime = DateTimeUtils.currentTime();
        this.modifyTime = createTime;
        this.delFlag = StringUtils.EMPTY;
    }
}

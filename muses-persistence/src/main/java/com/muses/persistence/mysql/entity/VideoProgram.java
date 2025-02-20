package com.muses.persistence.mysql.entity;

import com.muses.common.enums.VideoProgramStateEnums;
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
 * @ClassName Program
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/7 10:19
 */
@Data
@ToString
@Table(name= "video_program")
@Entity(name = "video_program")
@NoArgsConstructor
public class VideoProgram extends BaseEntity {

    @Id
    private String id;

    private String title;

    private String description;

    private String videoStoreId;

    private String coverStoreId;

    private String themes;

    private String userId;

    //短视频/长视频
    private String type;

    //状态机，视频有审核之类的
    private int state;

    private boolean transcodeFlag;

    //VideoProgramExtInfo
    private String ext;

    private int version;

    @Builder
    public VideoProgram(String id, String title, String description, String videoStoreId, String coverStoreId, String themes, String userId, String type, String ext, boolean transcodeFlag) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.videoStoreId = videoStoreId;
        this.coverStoreId = coverStoreId;
        this.themes = themes;
        this.userId = userId;
        this.type = type;
        this.ext = ext;
        this.transcodeFlag = transcodeFlag;

        this.state = VideoProgramStateEnums.CREATE.getState();
        this.createTime = DateTimeUtils.currentTime();
        this.modifyTime = createTime;
        this.delFlag = StringUtils.EMPTY;
    }

    public boolean isAvailable(){
        return VideoProgramStateEnums.isAvailable(state);
    }
}

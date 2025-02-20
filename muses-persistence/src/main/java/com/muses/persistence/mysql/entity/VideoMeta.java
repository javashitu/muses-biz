package com.muses.persistence.mysql.entity;

import com.muses.common.util.DateTimeUtils;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName VideoMeta
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/7 11:57
 */
@Data
@Table
@Entity
@NoArgsConstructor
public class VideoMeta extends BaseEntity {

    @Id
    private String id;

    private String fileName;

    private String programId;

    private String fileStoreId;

    //时长，seconds
    private int duration;

    //封装格式mp4, avi
    private String format;

    private String suffix;

    //帧率
    private String frameRate;

    private int height;

    private int width;

    //分辨率
    private String resolution;

    //比特率 kbps
    private int bitRate;

    private int size;

    private String videoCodec;

    private String audioCodec;

    @Builder
    public VideoMeta(String id, String fileName, String programId, String fileStoreId, int duration, String format, String suffix, String frameRate, int height, int width, String resolution, int bitRate, int size, String videoCodec, String audioCodec) {
        this.id = id;
        this.fileName = fileName;
        this.programId = programId;
        this.fileStoreId = fileStoreId;
        this.duration = duration;
        this.format = format;
        this.suffix = suffix;
        this.frameRate = frameRate;
        this.height = height;
        this.width = width;
        this.resolution = resolution;
        this.bitRate = bitRate;
        this.size = size;
        this.videoCodec = videoCodec;
        this.audioCodec = audioCodec;

        this.createTime = DateTimeUtils.currentTime();
        this.modifyTime = createTime;
        this.delFlag = StringUtils.EMPTY;
    }
}

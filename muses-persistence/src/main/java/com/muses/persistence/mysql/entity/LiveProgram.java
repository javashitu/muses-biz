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
 * @ClassName LiveProgram
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/9 16:33
 */
@Data
@ToString
@Table(name = "live_program")
@Entity(name = "live_program")
@NoArgsConstructor
public class LiveProgram extends BaseEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "anchor")
    private String anchor;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "cover")
    private String cover;

    @Column(name= "partition")
    private String partition;

    @Column(name= "record_live")
    private boolean recordLive;

    @Column(name= "type")
    private String type;

    @Column(name="state")
    private int state;

    @Column(name = "begin_time")
    private long beginTime;

    @Column(name = "close_time")
    private long closeTime;

    @Column(name="play_back_store_id")
    private String playBackStoreId;

    @Column(name="live_room_id")
    private String liveRoomId;

    @Column(name = "program_id")
    private String programId;

    @Builder
    public LiveProgram(String id, String anchor, String name, String description, String cover, String partition, boolean recordLive, String type, int state, long beginTime, long closeTime, String playBackStoreId, String liveRoomId, String programId) {
        this.id = id;
        this.anchor = anchor;
        this.name = name;
        this.description = description;
        this.cover = cover;
        this.partition = partition;
        this.recordLive = recordLive;
        this.type = type;
        this.state = state;
        this.beginTime = beginTime;
        this.closeTime = closeTime;
        this.playBackStoreId = playBackStoreId;
        this.liveRoomId = liveRoomId;
        this.programId = programId;

        this.createTime = DateTimeUtils.currentTime();
        this.modifyTime = createTime;
        this.delFlag = StringUtils.EMPTY;
    }
}

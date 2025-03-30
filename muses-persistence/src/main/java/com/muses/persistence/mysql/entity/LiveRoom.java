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
 * @ClassName LiveRoom
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/9 17:14
 *
 * 记录直播间里容易变更的统计数据
 */
@Data
@ToString
@Table(name = "live_room")
@Entity(name = "live_room")
@NoArgsConstructor
public class LiveRoom extends BaseEntity {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "live_program_id")
    private String liveProgramId;

    @Column(name = "online")
    private int online;

    @Column(name = "max_online")
    private int maxOnline;

    @Column(name = "gift_count")
    private int giftCount;

    @Column(name = "version")
    private int version;

    @Builder
    public LiveRoom(String id, String liveProgramId, int online, int maxOnline, int giftCount, int version) {
        this.id = id;
        this.liveProgramId = liveProgramId;
        this.online = online;
        this.maxOnline = maxOnline;
        this.giftCount = giftCount;
        this.version = version;

        this.createTime = DateTimeUtils.currentTime();
        this.modifyTime = createTime;
        this.delFlag = StringUtils.EMPTY;
    }
}

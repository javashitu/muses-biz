package com.muses.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName LiveRoomFormatterEnums
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/10 15:02
 */
@Getter
@AllArgsConstructor
public enum LiveFormatterEnums implements ILiveFormatter {

    CREATE_ROOM_DISTRIBUTE_KEY("create_room_distribute_key", "创建直播间时唯一key,参数userId") {
        @Override
        public String format(String... ids) {
            return "%s:live".formatted(ids[0]);
        }
    },
    UPDATE_ROOM_DISTRIBUTE_KEY("update_room_distribute_key", "更新直播间信息时唯一key,参数live roomId") {
        @Override
        public String format(String... ids) {
            return "%s:room".formatted(ids[0]);
        }
    },
    ;

    private final String key;

    private final String desc;

}

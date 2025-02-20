package com.muses.common.enums;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @ClassName LiveProgramStateEnums
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/9 16:50
 */
@Getter
@AllArgsConstructor
public enum LiveProgramStateEnums {

    CREATE(10, "创建"),
    PAUSE(20, "暂停"),
    AUDIT(25, "审核"),
    SHOW(50, "展示"),
    FORBIDDEN(66, "封禁"),
    TERMINATE(70, "结束");

    private final int state;

    private final String desc;

    public static List<Integer> getActiveState() {
        return Lists.newArrayList(CREATE.getState(), PAUSE.getState(), AUDIT.getState(), SHOW.getState());
    }

    public static boolean isActiveState(int state) {
        return state <= SHOW.getState();
    }
}

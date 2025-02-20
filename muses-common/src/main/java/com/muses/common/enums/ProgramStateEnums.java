package com.muses.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName ProgramStatusEnums
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/8 19:52
 */
@Getter
@AllArgsConstructor
public enum ProgramStateEnums {

    CREATE(10, "创建"),
    CONCEAL(30, "隐藏"),
    SHOW(50, "展示"),
    DELETE(70, "删除");


    private final int state;

    private final String desc;

    public static boolean canShow(int state){
        return state == SHOW.getState();
    }
}

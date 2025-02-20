package com.muses.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName VideoProgramStatusEnums
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/7 10:24
 */
@Getter
@AllArgsConstructor
public enum VideoProgramStateEnums {

    CREATE(10, "创建"),
    TRANSCODING(15, "转码"),
    AUDIT(20, "审核"),
    CONCEAL(30, "隐藏"),
    SHOW(50, "展示"),
    DELETE(70, "删除"),
    //不能自己解除,
    FREEZE(81, "冻结");

    private final int state;

    private final String desc;

    public static boolean isAvailable(int state){
        if(state <= SHOW.getState()){
            return true;
        }
        return false;
    }

}

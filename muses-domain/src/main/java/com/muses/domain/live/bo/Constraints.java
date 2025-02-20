package com.muses.domain.live.bo;

import lombok.Data;

import java.util.Map;

/**
 * @ClassName Constraints
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/13 15:15
 */
@Data
public class Constraints {

    private Map<String, Object> video;

    private boolean audio;

}

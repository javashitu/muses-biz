package com.muses.domain.servicce.enums;

import com.muses.common.enums.ServerErrorCodeEnums;
import com.muses.common.exception.ServerException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName SignalType
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/19 19:32
 */
@Getter
@AllArgsConstructor
public enum SignalTypeEnums {

    OFFER("offer"),
    ANSWER("answer"),
    ICE("ice");

    private String rtcSignalType;

    private static final Map<String, SignalTypeEnums> map = new HashMap<>();

    static{
        map.put(OFFER.getRtcSignalType(), OFFER);
        map.put(ANSWER.getRtcSignalType(), ANSWER);
        map.put(ICE.getRtcSignalType(), ICE);
    }

    public static SignalTypeEnums wrap(String signalType) {
        if(StringUtils.isBlank(signalType)){
            throw ServerException.builder().serverErrorCodeEnums(ServerErrorCodeEnums.SIGNAL_TYPE_WRONG).build();
        }
        SignalTypeEnums signalTypeEnums = map.get(signalType);
        if(signalTypeEnums == null){
            throw new UnsupportedOperationException("unknown rtc signal, maybe rtc version wrong");
        }
        return signalTypeEnums;
    }
}

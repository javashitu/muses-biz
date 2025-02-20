package com.muses.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.muses.common.enums.ServerErrorCodeEnums;
import com.muses.common.exception.ServerException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName JsonFormatter
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/8 17:05
 */
@Data
@Slf4j
public class JsonFormatter {

    private ObjectMapper mapper = new ObjectMapper();
    {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }

    public String object2Json(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (Exception e) {
            log.error("serialize object to json failure ", e);
            throw ServerException.builder().cause(e).serverErrorCodeEnums(ServerErrorCodeEnums.SERVER_ERROR).build();
        }
    }

    public <T> T json2Object(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("deserialize json to object error the json is {} will deserialize as object {}", json, clazz, e);
            throw ServerException.builder().cause(e).serverErrorCodeEnums(ServerErrorCodeEnums.SERVER_ERROR).build();
        }
    }
}

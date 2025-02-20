package com.muses.common.exception;

import com.muses.common.enums.ServerErrorCodeEnums;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName ServerException
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/7 14:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerException extends RuntimeException {

    private ServerErrorCodeEnums serverErrorCodeEnums;

    private String message;

    @Builder
    public ServerException(Throwable cause, ServerErrorCodeEnums serverErrorCodeEnums, String message) {
        super(StringUtils.isNotBlank(message) ? message : serverErrorCodeEnums.getMessage(), cause);
        this.serverErrorCodeEnums = serverErrorCodeEnums;
        this.message = StringUtils.isNotBlank(message) ? message : serverErrorCodeEnums.getMessage();
    }

}

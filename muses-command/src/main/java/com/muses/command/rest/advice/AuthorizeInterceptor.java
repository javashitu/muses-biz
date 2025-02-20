package com.muses.command.rest.advice;

import com.muses.common.enums.ServerErrorCodeEnums;
import com.muses.common.util.JsonFormatter;
import com.muses.domain.rest.response.ApiResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;

/**
 * @ClassName AuthorizeInterceptor
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/9 14:46
 */
@Component
public class AuthorizeInterceptor implements HandlerInterceptor {

    private static final String USER_KEY = "userId";

    private static final String TOKEN = "authorization";

    @Autowired
    private JsonFormatter jsonFormatter;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        String userId = request.getHeader(USER_KEY);
        String token = request.getHeader(TOKEN);
        if (canAccess(userId, token)) {
            return true;
        }
        //处理鉴权失败
        ApiResult apiResult = ApiResult.builder().code(ServerErrorCodeEnums.AUTHORIZATION_FAILURE.getCode()).message(ServerErrorCodeEnums.AUTHORIZATION_FAILURE.getMessage()).build();

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        final PrintWriter writer = response.getWriter();
        writer.write(jsonFormatter.object2Json(apiResult));
        return false;
    }

    private boolean canAccess(String userId, String token) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(token)) {
            //TODO 这里是为了测试
            return true;
        }
        //使用JWT校验，并且token中的id要和header中的id一致
        return false;
    }
}

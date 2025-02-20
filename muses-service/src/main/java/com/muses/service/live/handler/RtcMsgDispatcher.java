package com.muses.service.live.handler;

import com.muses.adapter.connection.IMessageDispatcher;
import com.muses.common.util.JsonFormatter;
import com.muses.domain.servicce.enums.ProtoTypeEnums;
import com.muses.domain.servicce.proto.BaseReq;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @ClassName DispatchHandler
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/7 13:12
 */
@Component
public class RtcMsgDispatcher implements RtcHandler<BaseReq>, IMessageDispatcher, InitializingBean, ApplicationContextAware {


    private final ConcurrentMap<String, RtcHandler> HANDLER_MAP = new ConcurrentHashMap<>();

    private ApplicationContext appContext;

    @Autowired
    private JsonFormatter formatter;

    @Override
    public String getType(){
        return ProtoTypeEnums.DISPATCH.getRtcProtoType();
    }

    @Override
    public void handleMsg(BaseReq request) {
        RtcHandler handler = HANDLER_MAP.get(request.getProtoType());
        handler.handleMsg(request);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 将 Spring 容器中所有的 LoginHandler 注册到
        appContext.getBeansOfType(RtcHandler.class)
                .values()
                .forEach(handler -> HANDLER_MAP.put(handler.getType(), handler));
    }

    @Override
    public void dispatchMessage(String message) {
        BaseReq baseReq = formatter.json2Object(message, BaseReq.class);
        handleMsg(baseReq);
    }

    @Override
    public void dispatchReq(BaseReq baseReq){
        handleMsg(baseReq);
    }

}

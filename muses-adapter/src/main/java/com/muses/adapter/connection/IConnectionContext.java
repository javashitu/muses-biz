package com.muses.adapter.connection;

import java.util.Map;

/**
 * @ClassName IConnectionContext
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/11 10:52
 */
public interface IConnectionContext extends IConnectionActionTrigger, IConnectionMsgProcessor {

    Map<String,String> getConnectionUrl(String connectionId, String roomId);

}

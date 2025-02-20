package com.muses.adapter.connection;

import com.muses.common.enums.ConnectionActionEnums;
import com.muses.domain.live.bo.BaseConnection;

/**
 * @ClassName IConnectionActionHandler
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/11 15:34
 */
public interface IConnectionActionDispatcher {

    void dispatchAction(BaseConnection connection, ConnectionActionEnums actionEnum);

}

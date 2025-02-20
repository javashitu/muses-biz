package com.muses.domain.live.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName Connection
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/11 10:28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseConnection {

    protected String connectionId;

    //还是要引入roomId的概念，不必然就需要把roomId的概念转换成类似connectionGroupId的概念，但是本质没变，而且又绕了一圈
    protected String roomId;

    public abstract void sendMsg(Object msg);

    public abstract void close();


}

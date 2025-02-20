package com.muses.common.util;

import lombok.Data;

/**
 * @ClassName IdGenerator
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/8 17:06
 */
@Data
public class IdGenerator {

    private String workerId;

    private IdWorker idWorker;

    public IdGenerator(Long workerId) {
        this.idWorker = new IdWorker(workerId);
    }

    public String nextId(){
        return String.valueOf(idWorker.nextId());
    }

    public String nextUid(){
        return idWorker.nextId() + "U";
    }

    public String nextProgramId(){
        return idWorker.nextId() + "P";
    }

    public String nextVideoProgramId(){
        return idWorker.nextId() + "V";
    }

    public String nextLiveProgramId(){
        return idWorker.nextId() + "L";
    }

    public String nextRoomId(){
        return idWorker.nextId() + "R";
    }
}

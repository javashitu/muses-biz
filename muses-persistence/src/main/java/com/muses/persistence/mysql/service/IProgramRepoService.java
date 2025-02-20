package com.muses.persistence.mysql.service;

import com.muses.persistence.mysql.entity.Program;

import java.util.List;

/**
 * @ClassName IProgramRepoService
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/9 10:29
 */
public interface IProgramRepoService {

    Program findById(String id);

    Program save(Program program);

    List<Program> findByUserId(String userId, int pageNum);

}

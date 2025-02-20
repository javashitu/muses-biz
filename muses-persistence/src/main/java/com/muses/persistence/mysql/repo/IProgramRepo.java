package com.muses.persistence.mysql.repo;

import com.muses.persistence.mysql.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @ClassName ProgramRepo
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/9 10:34
 */
public interface IProgramRepo extends JpaRepository<Program, String>, JpaSpecificationExecutor<Program> {

    List<Program> findByUserId(String userId);

}

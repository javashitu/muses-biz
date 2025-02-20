package com.muses.persistence.mysql.repo;

import com.muses.persistence.mysql.entity.FileStore;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @ClassName FileStoreRepo
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/10 19:42
 */
public interface IFileStoreRepo extends JpaRepository<FileStore, String> {
}

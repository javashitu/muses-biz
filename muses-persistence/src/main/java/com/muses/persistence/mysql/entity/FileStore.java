package com.muses.persistence.mysql.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName FileStore
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/7 11:56
 */
@Data
@Table
@Entity
@NoArgsConstructor
public class FileStore extends BaseEntity{

    @Id
    private String id;

    private String fileName;

    private String type;

    private int size;

    private String userId;

    private String bucketName;

    private String objectName;
}

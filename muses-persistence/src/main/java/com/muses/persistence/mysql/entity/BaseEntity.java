package com.muses.persistence.mysql.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;

/**
 * @ClassName BaseEntity
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/7 10:15
 */
@Data
@MappedSuperclass
public abstract class BaseEntity {

    public long createTime;

    public long modifyTime;

    public String delFlag;

}

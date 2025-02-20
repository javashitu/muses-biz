package com.muses.persistence.mysql.entity.ext;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName VideoExtInfo
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/10 14:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoProgramExt {

    @JsonProperty("vid")
    private String originVideoStoreId;

    @JsonProperty("cid")
    private String originCoverStoreId;
}

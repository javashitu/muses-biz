package com.muses.domain.rest.request.param;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @ClassName VideoFileInfo
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/6 19:22
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class VideoFileInfo {

    @NotBlank(message = "parma videoStoreId can't be null")
    private String videoStoreId;

//    @NotBlank(message = "parma coverStoreId can't be null")
    private String coverStoreId;

}


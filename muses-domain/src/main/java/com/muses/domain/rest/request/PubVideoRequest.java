package com.muses.domain.rest.request;

import com.muses.domain.rest.request.param.VideoFileInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @ClassName PubVideoRequest
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/6 19:21
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PubVideoRequest {

    @NotBlank(message = "parma title can't be null")
    private String title;

    private String description;

    @NotBlank(message = "parma userId can't be null")
    private String userId;

    @NotBlank(message = "parma createType can't be null")
    private String createType;

    @NotBlank(message = "parma programType can't be null")
    private String programType;

    //定时发布时才会有这个参数
    private long pubTime;

    //关联id
    private String relevanceProgramId;

    //话题
    private List<String> themeList;

    @Valid
    @NotNull(message = "parma videoFileInfo can't be null")
    private VideoFileInfo videoFileInfo;

}


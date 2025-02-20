package com.muses.service.mapper;

import com.muses.domain.rest.response.info.VideoProgramInfo;
import com.muses.persistence.mysql.entity.VideoProgram;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @ClassName VideoProgramMapper
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/10 19:12
 */
@Mapper(componentModel = "spring")//交给spring管理
public interface VideoProgramMapper {

    VideoProgramMapper CAR_MAPPING = Mappers.getMapper(VideoProgramMapper.class);

//    @Mapping(target = "type", source = "carType.type")
    VideoProgramInfo videoProgramPoToInfo(VideoProgram videoProgram);
}

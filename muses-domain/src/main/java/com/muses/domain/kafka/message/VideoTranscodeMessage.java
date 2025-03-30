package com.muses.domain.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName VideoTranscodeMessage
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/12/11 17:40
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoTranscodeMessage {
    private String videoProgramId;

    private String fileStoreId;

    private String videoMetaId;

    private String pubUserId;

    private String transcodeVideoFileStoreId;

    private String screenImgFileStoreId;
}

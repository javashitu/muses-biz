package com.muses.domain.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName VideoPubMessage
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/12/4 9:56
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoPubMessage {

    private String videoProgramId;

    private String fileStoreId;

    private String pubUserId;

    private int state;
}

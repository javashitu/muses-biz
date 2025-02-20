package com.muses.adapter.service;

import com.muses.domain.rest.request.ListLiveRequest;
import com.muses.domain.rest.request.ListOtherLiveRequest;
import com.muses.domain.rest.request.PubLiveRequest;
import com.muses.domain.rest.request.QueryLiveRequest;
import com.muses.domain.rest.response.ListLiveResponse;
import com.muses.domain.rest.response.ListOtherLiveResponse;
import com.muses.domain.rest.response.PubLiveResponse;
import com.muses.domain.rest.response.QueryLiveResponse;

import java.util.Map;

/**
 * @ClassName ILiveService
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/9 14:01
 */
public interface ILiveService {

    PubLiveResponse pubLive(PubLiveRequest request);

    void terminateLive(String liveProgramId);

    QueryLiveResponse queryLive(QueryLiveRequest request);

    ListLiveResponse listLive(ListLiveRequest request);

    ListOtherLiveResponse listOtherLive(ListOtherLiveRequest request);

    Map<String, Object> queryLiveInfo(String roomId);

}

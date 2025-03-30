package com.muses.service.live;

import com.muses.adapter.service.ILiveService;
import com.muses.common.enums.*;
import com.muses.common.exception.ServerException;
import com.muses.common.util.DateTimeUtils;
import com.muses.common.util.IdGenerator;
import com.muses.common.util.iface.IDistributeLock;
import com.muses.domain.rest.request.ListLiveRequest;
import com.muses.domain.rest.request.ListOtherLiveRequest;
import com.muses.domain.rest.request.PubLiveRequest;
import com.muses.domain.rest.request.QueryLiveRequest;
import com.muses.domain.rest.response.ListLiveResponse;
import com.muses.domain.rest.response.ListOtherLiveResponse;
import com.muses.domain.rest.response.PubLiveResponse;
import com.muses.domain.rest.response.QueryLiveResponse;
import com.muses.domain.rest.response.info.LiveProgramInfo;
import com.muses.persistence.mysql.entity.LiveProgram;
import com.muses.persistence.mysql.entity.LiveRoom;
import com.muses.persistence.mysql.entity.Program;
import com.muses.persistence.mysql.service.ILiveProgramRepoService;
import com.muses.persistence.mysql.service.ILiveRoomRepoService;
import com.muses.persistence.mysql.service.impl.ProgramRepoService;
import com.muses.service.live.rtc.IRtcRoomService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName LiveServiceImpl
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/9 16:27
 */
@Slf4j
@Component
public class LiveService implements ILiveService {

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private IDistributeLock iDistributeLock;

    @Autowired
    private ILiveProgramRepoService liveProgramRepoService;

    @Autowired
    private ILiveRoomRepoService liveRoomRepoService;

    @Autowired
    private ProgramRepoService programRepoService;

    @Autowired
    private IRtcRoomService rtcRoomService;


    @Autowired
    private LiveDistributeService liveDistributeService;


    @Override
    public PubLiveResponse pubLive(PubLiveRequest request) {
        String lockKey = LiveFormatterEnums.CREATE_ROOM_DISTRIBUTE_KEY.format(request.getUserId());

        if (!iDistributeLock.retryLock(lockKey, 3, 3)) {
            log.info("can't lock for user {} ,ignore pub this live room", lockKey);
            throw ServerException.builder().serverErrorCodeEnums(ServerErrorCodeEnums.CANT_CREATE_LIVE_ROOM).build();
        }

        try {
            LiveProgram activeLiveProgram = findActiveLiveProgram(request.getUserId());
            if (activeLiveProgram != null) {
                log.info("only one live program can pub, so return active live room live program id {} , live room id {} program is {}", activeLiveProgram.getId(), activeLiveProgram.getLiveRoomId(), activeLiveProgram.getProgramId());

                Program program = programRepoService.findById(activeLiveProgram.getProgramId());
                //极端场景，liveProgram插入成功，program插入失败
                if (program == null) {
                    log.info("find live program but can't find program, save program idempotent live program id {}", activeLiveProgram.getProgramId());
                    program = buildProgram(request, activeLiveProgram);
                    programRepoService.save(program);
                }
            }else{
                activeLiveProgram = createLive(request);
            }
            return new PubLiveResponse(convert2LiveProgramInfo(activeLiveProgram, request.getUserId()));
        } finally {
            iDistributeLock.unlock(lockKey);
        }
    }

    private Program buildProgram(PubLiveRequest request, LiveProgram liveProgram) {
        Program program = Program.builder()
                .id(idGenerator.nextProgramId())
                .userId(request.getUserId())
                .createType(ProgramCreateTypeEnums.SELF.getType())
                .type(ProgramTypeEnums.LIVE.getType())
                .liveProgramId(liveProgram.getId())
                .state(ProgramStateEnums.CREATE.getState())
                .videoProgramId(StringUtils.EMPTY)
                .textProgramId(StringUtils.EMPTY)
                .voteProgramId(StringUtils.EMPTY)
                .activityProgramId(StringUtils.EMPTY)
                .relevanceId(StringUtils.EMPTY)
                .build();
        program.setPubTime(program.getCreateTime());
        return program;
    }

    private LiveProgram buildLiveProgram(PubLiveRequest request, LiveRoom liveRoom) {
        LiveProgramTypeEnums programTypeEnums = LiveProgramTypeEnums.wrap(request.getType());

        LiveProgram liveProgram = LiveProgram.builder()
                .id(idGenerator.nextLiveProgramId())
                .anchor(request.getUserId())
                .name(request.getRoomName())
                .description(request.getRoomDesc())
                .cover(request.getCover())
                .partition(request.getPartition())
                .recordLive(request.isRecordLive())
                .type(programTypeEnums.getType())
                .state(LiveProgramStateEnums.CREATE.getState())
                .liveRoomId(liveRoom.getId())
                .playBackStoreId(StringUtils.EMPTY)
                .build();
        liveProgram.setBeginTime(liveProgram.getCreateTime());

        return liveProgram;
    }

    private LiveRoom buildLiveRoom() {
        return LiveRoom.builder().id(idGenerator.nextRoomId()).build();
    }

    private LiveProgram findActiveLiveProgram(String userId) {
        List<LiveProgram> liveProgramList = liveProgramRepoService.findByUserIdAndState(userId, LiveProgramStateEnums.getActiveState());
        if (CollectionUtils.isEmpty(liveProgramList)) {
            return null;
        }
        return liveProgramList.get(0);
    }

    private LiveProgram createLive(PubLiveRequest request) {
        log.info("create a new live program and rtc room ");
        LiveRoom liveRoom = buildLiveRoom();

        LiveProgram liveProgram = buildLiveProgram(request, liveRoom);
        liveRoom.setLiveProgramId(liveProgram.getId());

        Program program = buildProgram(request, liveProgram);
        liveProgram.setProgramId(program.getLiveProgramId());

        rtcRoomService.createRtcRoom(liveRoom.getId(), liveProgram.getAnchor());

        liveRoomRepoService.save(liveRoom);
        liveProgramRepoService.save(liveProgram);
        programRepoService.save(program);
        return liveProgram;
    }

    @Override
    public void terminateLive(String liveRoomId) {
        log.info("terminate live for room {}", liveRoomId);
        LiveProgram liveProgram = liveProgramRepoService.findByLiveRoomId(liveRoomId);
        if (liveProgram == null) {
            log.error("can't find liveProgram by live room id, maybe data loss, live room id {}", liveRoomId);
            return;
        }
        String lockKey = LiveFormatterEnums.UPDATE_ROOM_DISTRIBUTE_KEY.format(liveProgram.getLiveRoomId());

        if (!iDistributeLock.retryLock(lockKey, 3, 3)) {
            log.info("can't lock for live room {} ,ignore terminate room", lockKey);
            throw ServerException.builder().serverErrorCodeEnums(ServerErrorCodeEnums.CANT_CLOSE_LIVE_ROOM).build();
        }
        try {
            closeRtcRoom(liveProgram.getLiveRoomId());
            terminateProgram(liveProgram);
        } finally {
            iDistributeLock.unlock(lockKey);
        }
    }

    public void terminateLiveProgram(String liveProgramId){
        log.info("terminate live program {}", liveProgramId);
        LiveProgram liveProgram = liveProgramRepoService.findByLiveRoomId(liveProgramId);
        if (liveProgram == null) {
            log.error("can't find liveProgram by live room id, live program id {}", liveProgramId);
            return;
        }
        terminateProgram(liveProgram);
    }

    @Override
    public QueryLiveResponse queryLive(QueryLiveRequest request) {
        LiveProgram liveProgram = liveProgramRepoService.findById(request.getLiveProgramId());
        if (liveProgram == null) {
            log.error("no live program for {} ,the query user {}", request.getLiveProgramId(), request.getUserId());
            throw ServerException.builder().serverErrorCodeEnums(ServerErrorCodeEnums.CANT_FIND_LIVE_PROGRAM).build();
        }
        QueryLiveResponse response = QueryLiveResponse.builder()
                .rtcUserId(request.getUserId())
                .liveProgramId(liveProgram.getId())
                .liveRoomId(liveProgram.getLiveRoomId())
                .roomName(liveProgram.getName())
                .roomDesc(liveProgram.getDescription())
                .cover(liveProgram.getCover())
                .type(liveProgram.getType())
                .state(liveProgram.getState())
                .build();
        if (!LiveProgramStateEnums.isActiveState(liveProgram.getState())) {
            log.info("live program has terminate , live program id {} ", request.getLiveProgramId());
            return response;
        }
        response.setLiveAddress(rtcRoomService.getRoomAddress(liveProgram.getLiveRoomId(), request.getUserId()));
        return response;
    }

    @Override
    public ListLiveResponse listLive(ListLiveRequest request) {
        long curDate = DateTimeUtils.getTodayMilliSecond();
        log.info("query param curDate is {}", curDate);
        //TODO 分布式环境下，可能因为延迟导致房间已经被关闭，但是DB里的记录还是活跃的
        List<LiveProgram> liveProgramList = liveProgramRepoService.findByBeginTimeAndState(request.getPageNum(), curDate, LiveProgramStateEnums.getActiveState());
        ListLiveResponse response = new ListLiveResponse();
        if (CollectionUtils.isEmpty(liveProgramList)) {
            log.error("no live program in show in date {} ,", curDate);
            return response;
        }
        List<LiveProgramInfo> liveProgramInfoList = convert2LiveProgramInfoList(liveProgramList, request.getUserId());
        response.setLiveProgramInfoList(liveProgramInfoList);
        return response;
    }

    @Override
    public ListOtherLiveResponse listOtherLive(ListOtherLiveRequest request) {
        long curDate = DateTimeUtils.getTodayMilliSecond();
        log.info("query other live param curDate is {}", curDate);
        List<LiveProgram> liveProgramList = liveProgramRepoService.findByBeginTimeAndStateAndAnchor(request.getPageNum(), curDate, LiveProgramStateEnums.getActiveState(), request.getUserId());
        ListOtherLiveResponse response = new ListOtherLiveResponse();
        if (CollectionUtils.isEmpty(liveProgramList)) {
            log.error("no other live program in show in date {} ,", curDate);
            return response;
        }
        List<LiveProgramInfo> liveProgramInfoList = convert2LiveProgramInfoList(liveProgramList, request.getUserId());
        response.setLiveProgramInfoList(liveProgramInfoList);
        return response;
    }

    private List<LiveProgramInfo> convert2LiveProgramInfoList(List<LiveProgram> liveProgramList, String userId) {
        List<LiveProgramInfo> liveProgramInfoList = new ArrayList<>();
        for (LiveProgram liveProgram : liveProgramList) {
            if(!liveDistributeService.isLiveActive(liveProgram.getLiveRoomId())){
                log.info("live room has been close,won't add");
                continue;
            }
            LiveProgramInfo liveProgramInfo = convert2LiveProgramInfo(liveProgram, userId);
            liveProgramInfoList.add(liveProgramInfo);
        }
        return liveProgramInfoList;
    }

    private LiveProgramInfo convert2LiveProgramInfo(LiveProgram liveProgram, String userId) {
        LiveProgramInfo liveProgramInfo = LiveProgramInfo.builder()
                .createUserId(liveProgram.getAnchor())
                .liveProgramId(liveProgram.getId())
                .liveRoomId(liveProgram.getLiveRoomId())
                .roomName(liveProgram.getName())
                .roomDesc(liveProgram.getDescription())
                .cover(liveProgram.getCover())
                .type(liveProgram.getType())
                .state(liveProgram.getState())
                .build();
        if (!LiveProgramStateEnums.isActiveState(liveProgram.getState())) {
            log.info("live program has terminate , live program id {} ", liveProgramInfo.getLiveProgramId());
            return liveProgramInfo;
        }
        liveProgramInfo.setLiveAddress(rtcRoomService.getRoomAddress(liveProgram.getLiveRoomId(), userId));
        return liveProgramInfo;
    }

    public Map<String, Object> queryLiveInfo(String roomId) {
        return rtcRoomService.getRoomInfo(roomId);
    }

    private void closeRtcRoom(String roomId) {
        //TODO 这里需要判断是不是在本服务器上，不是的话需要转发到对应的目标服务器上，现在就只有一台先不做
        rtcRoomService.closeRtcRoom(roomId);
    }

    private void terminateProgram(LiveProgram liveProgram) {
        if (!LiveProgramStateEnums.isActiveState(liveProgram.getState())) {
            log.info("live program has terminate, ignore terminate ");
            return;
        }
        liveProgram.setState(LiveProgramStateEnums.TERMINATE.getState());
        long curTime = DateTimeUtils.currentTime();
        liveProgram.setCloseTime(curTime);
        liveProgram.setModifyTime(curTime);

        liveProgramRepoService.save(liveProgram);
    }
}

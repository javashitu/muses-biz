package com.muses.service.video.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.muses.adapter.service.IVideoService;
import com.muses.common.enums.ProgramCreateTypeEnums;
import com.muses.common.enums.ProgramStateEnums;
import com.muses.common.enums.ProgramTypeEnums;
import com.muses.common.enums.ServerErrorCodeEnums;
import com.muses.common.exception.ServerException;
import com.muses.common.util.DateTimeUtils;
import com.muses.common.util.IdGenerator;
import com.muses.common.util.JsonFormatter;
import com.muses.domain.kafka.message.VideoPubMessage;
import com.muses.domain.kafka.message.VideoTranscodeMessage;
import com.muses.domain.rest.request.LikeVideoRequest;
import com.muses.domain.rest.request.ListVideoRequest;
import com.muses.domain.rest.request.PlayVideoRequest;
import com.muses.domain.rest.request.PubVideoRequest;
import com.muses.domain.rest.response.LikeVideoResponse;
import com.muses.domain.rest.response.ListVideoResponse;
import com.muses.domain.rest.response.PlayVideoResponse;
import com.muses.domain.rest.response.PubVideoResponse;
import com.muses.domain.rest.response.info.VideoProgramInfo;
import com.muses.persistence.kafka.produce.IVideoPubProducer;
import com.muses.persistence.mysql.entity.Program;
import com.muses.persistence.mysql.entity.VideoMeta;
import com.muses.persistence.mysql.entity.VideoProgram;
import com.muses.persistence.mysql.entity.VideoProgramInteract;
import com.muses.persistence.mysql.entity.ext.VideoProgramExt;
import com.muses.persistence.mysql.service.IProgramRepoService;
import com.muses.persistence.mysql.service.IVideoInteractRepoService;
import com.muses.persistence.mysql.service.IVideoMetaRepoService;
import com.muses.persistence.mysql.service.IVideoProgramRepoService;
import com.muses.service.mapper.VideoProgramMapper;
import com.muses.service.proxy.VideoFileProxy;
import com.muses.service.video.file.stroe.FileStoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName VideoServiceImpl
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/7 9:55
 */
@Slf4j
@Component
public class VideoService implements IVideoService {

    @Autowired
    private IProgramRepoService programRepoService;

    @Autowired
    private IVideoProgramRepoService videoProgramRepoService;

    @Autowired
    private IVideoInteractRepoService videoInteractRepoService;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VideoProgramMapper videoProgramMapper;

    @Autowired
    private FileStoreService fileStoreService;

    @Autowired
    private JsonFormatter jsonFormatter;

    @Autowired
    private VideoFileProxy videoFileProxy;

    @Autowired
    private IVideoPubProducer videoPubProducer;

    @Autowired
    private IVideoMetaRepoService videoMetaRepoService;

    @Override
    public PubVideoResponse pubVideo(PubVideoRequest request) {
        log.info("begin execute pub video service");
        VideoProgram videoProgram = buildVideoProgram(request);
        Program program = buildProgram(request, videoProgram);
        VideoProgramInteract videoProgramInteract = buildVideoProgramInteract(program.getVideoProgramId(), program.getUserId());

        videoInteractRepoService.save(videoProgramInteract);
        programRepoService.save(program);
        videoProgramRepoService.save(videoProgram);
        notifyVideoPub(videoProgram);

        return new PubVideoResponse(videoProgram.getId());
    }

    private void notifyVideoPub(VideoProgram videoProgram) {
        VideoPubMessage videoPubMessage = VideoPubMessage.builder()
                .pubUserId(videoProgram.getUserId())
                .videoProgramId(videoProgram.getId())
                .fileStoreId(videoProgram.getVideoStoreId())
                .state(videoProgram.getState())
                .build();
        videoPubProducer.sendMessage(videoPubMessage.getPubUserId(), jsonFormatter.object2Json(videoPubMessage));
    }

    @Override
    public ListVideoResponse listVideo(ListVideoRequest request) {
        log.info("list video program for user {}", request.getUserId());
        ListVideoResponse response = new ListVideoResponse();
        response.setUserId(response.getUserId());
        List<VideoProgram> videoProgramList = videoProgramRepoService.findByUserId(request.getUserId(), request.getPageNum());
        List<VideoProgramInfo> videoProgramInfoList = genVideoProgramInfo(videoProgramList);
        response.setVideoProgramInfoList(videoProgramInfoList);
        return response;
    }

    private List<VideoProgramInfo> genVideoProgramInfo(List<VideoProgram> videoProgramList) {
        List<String> videoProgramIdList = videoProgramList.stream().map(VideoProgram::getId).toList();
        List<VideoProgramInteract> videoProgramInteractList = videoInteractRepoService.findByIdList(videoProgramIdList);

        return genVideoProgramInfoList(videoProgramList, videoProgramInteractList);
    }


    @Override
    public ListVideoResponse recommendVideo(ListVideoRequest request) {
        log.debug("recommend video program for user {} will list all videoProgram", request.getUserId());
        ListVideoResponse response = new ListVideoResponse();
        response.setUserId(response.getUserId());
        List<VideoProgram> videoProgramList = videoProgramRepoService.findAll(request.getPageNum());
        List<VideoProgramInfo> videoProgramInfoList = genVideoProgramInfo(videoProgramList);
        response.setVideoProgramInfoList(videoProgramInfoList);
        return response;
    }


    @Override
    public PlayVideoResponse playVideo(PlayVideoRequest request) {
        VideoProgramInteract videoProgramInteract = videoInteractRepoService.findById(request.getVideoProgramId());
        if (videoProgramInteract == null) {
            throw ServerException.builder().serverErrorCodeEnums(ServerErrorCodeEnums.CANT_FIND_PROGRAM).build();
        }
        videoInteractRepoService.updatePlayByIdAndVersion(videoProgramInteract.getPlay() + 1, videoProgramInteract.getVersion() + 1, videoProgramInteract.getId(), videoProgramInteract.getVersion());
        PlayVideoResponse response = new PlayVideoResponse();
        response.setUserId(request.getUserId());
        response.setPlay(videoProgramInteract.getPlay());
        return response;
    }

    @Override
    public LikeVideoResponse likeVideo(LikeVideoRequest request) {
        VideoProgramInteract videoProgramInteract = videoInteractRepoService.findById(request.getVideoProgramId());
        if (videoProgramInteract == null) {
            throw ServerException.builder().serverErrorCodeEnums(ServerErrorCodeEnums.CANT_FIND_PROGRAM).build();
        }
        videoInteractRepoService.updateLikesByIdAndVersion(videoProgramInteract.getLikes() + 1, videoProgramInteract.getVersion() + 1, videoProgramInteract.getId(), videoProgramInteract.getVersion());
        LikeVideoResponse response = new LikeVideoResponse();
        response.setUserId(request.getUserId());
        response.setLikes(videoProgramInteract.getLikes() + 1);
        return response;
    }

    public boolean updateTranscodeState(VideoTranscodeMessage message) {
        log.info("begin update videoProgram's transcode state");
        VideoProgram videoProgram = videoProgramRepoService.findById(message.getVideoProgramId());
        if (videoProgram == null || !videoProgram.isAvailable()) {
            log.info("the videoProgram state {} ,", videoProgram == null ? 0 : videoProgram.getState());
            log.info("can't notify video transcode finished, because the video program not available videoProgramId {} ,", message.getVideoProgramId());
            return true;
        }
        videoProgram.setTranscodeFlag(true);
        log.info("video has transcode finished, update transcode flag");
        videoProgramRepoService.save(videoProgram);
        return true;
    }

    private List<VideoProgramInfo> genVideoProgramInfoList(List<VideoProgram> programList, List<VideoProgramInteract> videoProgramInteractList) {
        Map<String, String> videoStoreIdMap = Maps.newHashMap();
        Map<String, String> coverStoreIdMap = Maps.newHashMap();
        Map<String, VideoProgramInfo> videoProgramMap = Maps.newHashMap();
        Map<String, VideoProgramInteract> map = videoProgramInteractList.stream().collect(Collectors.toMap(VideoProgramInteract::getId, program -> program));

        programList.forEach(program -> {
            VideoProgramInfo videoProgramInfo = videoProgramMapper.videoProgramPoToInfo(program);
            VideoProgramInteract videoProgramInteract = map.get(program.getId());
            videoProgramInfo.setPlay(videoProgramInteract.getPlay());
            videoProgramInfo.setLikes(videoProgramInteract.getLikes());
            videoProgramInfo.setShare(videoProgramInteract.getShare());
            videoProgramInfo.setCommentCount(videoProgramInteract.getCommentCount());

            videoProgramMap.put(program.getId(), videoProgramInfo);
            videoStoreIdMap.put(chooseVideo(program), program.getId());
            coverStoreIdMap.put(program.getCoverStoreId(), program.getId());
        });

        Map<String, String> videoStoreUrlMap = fileStoreService.getBatchFileVisitUrl(videoStoreIdMap.keySet(), true);
        videoStoreUrlMap.forEach((key, value) -> {
            String videoProgramId = videoStoreIdMap.get(key);
            videoProgramMap.get(videoProgramId).setVideoUrl(value);
        });

        Map<String, String> coverStoreUrlMap = fileStoreService.getBatchFileVisitUrl(coverStoreIdMap.keySet(), false);
        coverStoreUrlMap.forEach((key, value) -> {
            String videoProgramId = coverStoreIdMap.get(key);
            videoProgramMap.get(videoProgramId).setCoverUrl(value);
        });
        return Lists.newArrayList(videoProgramMap.values());
    }

    //严格来说选择视频文件时需要结合用户的属性决定每个用户能看什么样的视频，但是这里没有用户，就不做这个功能了
    private String chooseVideo(VideoProgram videoProgram) {
        if (!videoProgram.isTranscodeFlag()) {
            return videoProgram.getVideoStoreId();
        }
        List<VideoMeta> videoMetaList = videoMetaRepoService.findByVideoProgramId(videoProgram.getId());
        //取最低码率的
        videoMetaList.sort(Comparator.comparing(VideoMeta::getBitRate));
        VideoMeta videoMeta = videoMetaList.get(0);
        return videoMeta.getFileStoreId();
    }

    private VideoProgramInteract buildVideoProgramInteract(String videoProgramId, String userId) {
        return VideoProgramInteract.builder().id(videoProgramId).userId(userId).build();
    }

    private VideoProgram buildVideoProgram(PubVideoRequest request) {
        VideoProgramExt videoProgramExt = VideoProgramExt.builder().originVideoStoreId(request.getVideoFileInfo().getVideoStoreId()).originCoverStoreId(request.getVideoFileInfo().getCoverStoreId()).build();

        String strExt = jsonFormatter.object2Json(videoProgramExt);

        return VideoProgram.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .videoStoreId(request.getVideoFileInfo().getVideoStoreId())
                .coverStoreId(request.getVideoFileInfo().getVideoStoreId())
                .themes(request.getThemeList() == null ? null : Arrays.toString(request.getThemeList().toArray()))
                .type(ProgramTypeEnums.SHORT_VIDEO.getType())
                .transcodeFlag(false)
                .userId(request.getUserId())
                .ext(strExt)
                .id(idGenerator.nextVideoProgramId())
                .build();
    }

    private Program buildProgram(PubVideoRequest request, VideoProgram videoProgram) {
        ProgramCreateTypeEnums programCreateTypeEnums = ProgramCreateTypeEnums.wrap(request.getCreateType());
        ProgramTypeEnums programTypeEnum = ProgramTypeEnums.wrap(request.getProgramType());
        shareProgramCheck(request, programCreateTypeEnums);

        return Program.builder().id(idGenerator.nextProgramId()).userId(request.getUserId()).createType(programCreateTypeEnums.getType()).type(programTypeEnum.getType()).videoProgramId(videoProgram.getId()).relevanceId(programCreateTypeEnums == ProgramCreateTypeEnums.SHARE ? request.getRelevanceProgramId() : StringUtils.EMPTY).state(ProgramStateEnums.CREATE.getState()).pubTime(request.getPubTime() == 0 ? DateTimeUtils.currentTime() : request.getPubTime()).textProgramId(StringUtils.EMPTY).liveProgramId(StringUtils.EMPTY).voteProgramId(StringUtils.EMPTY).activityProgramId(StringUtils.EMPTY).build();
    }

    private void shareProgramCheck(PubVideoRequest request, ProgramCreateTypeEnums programCreateTypeEnums) {
        if (programCreateTypeEnums == ProgramCreateTypeEnums.SHARE) {
            if (StringUtils.isBlank(request.getRelevanceProgramId())) {
                throw ServerException.builder().serverErrorCodeEnums(ServerErrorCodeEnums.CANT_CREATE_PROGRAM).build();
            }
            Program program = programRepoService.findById(request.getRelevanceProgramId());
            if (!ProgramStateEnums.canShow(program.getState())) {
                throw ServerException.builder().serverErrorCodeEnums(ServerErrorCodeEnums.CANT_SHARE_PROGRAM).build();
            }
        }
    }

}

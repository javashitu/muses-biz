package com.muses.domain.live.bo;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.muses.common.util.DateTimeUtils;
import com.muses.common.util.MapComputeUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName Room
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/9/17 10:16
 * <p>
 * 一定不要在一个操作流的public方法里调用另一个操作流的public方法，否则可能因为pub/sub关系的改变发生异常
 * pub流和sub流的操作要分开，可以根据pub流的引用关系找到sub流再操作sub流，但是不能反过来！！
 */
//@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class RtcRoom {

    @Getter
    private String roomId;

    @Getter
    private String name;

    @Getter
    private String creatorId;

    @Getter
    private long expireTime;

    @Builder
    public RtcRoom(String roomId, String name, String creatorId, long expireTime) {
        this.roomId = roomId;
        this.name = name;
        this.creatorId = creatorId;
        this.expireTime = expireTime;
    }

    /**
     * 房间关闭标记，关闭后房间不能再新增任何流和人，可以挂断，退出，因为关闭房间是需要根据流的关系找到发布订阅关系，并且根据这个关系让各个方关闭对应的资源
     */
    private boolean closeFlag = false;

    private Lock lock = new ReentrantLock();

    private Map<String, RtcUser> userMap = new ConcurrentHashMap<>(8);

    //每个人都要知道其他人有没有pub，但不是每个人都要知道别人的sub关系
    //基于人-流的模型下每个人没办法发布多个流, 如果是流-流模型，这里的map又没办法快速定位到每个人发布的流
    //还是流-流模型更合理且易扩展，
    //key pubStreamId: value pubStream
    private Map<String, PubStream> pubStreamMap = new ConcurrentHashMap<>(8);

    //key pubUserId: value pubStream
    private Map<String, List<PubStream>> pubUserMap = new ConcurrentHashMap<>(8);

    //key subStreamId: value SubStream
    private Map<String, SubStream> subStreamMap = new ConcurrentHashMap<>(8);

    //key subUserId, value subStream
    private Map<String, List<SubStream>> subUserMap = new ConcurrentHashMap<>(8);

    /**
     * key pubStreamId: value subStream
     * 这里一个问题，一个pubStream能不能被同一个人多次sub,从模型上看，是可以的
     * 那么实际上可以吗？我觉得一般不会产生这种情况，但是设计时还是按照这种情况来设计。
     */
    private Map<String, List<SubStream>> pubSubRelationMap = new ConcurrentHashMap<>(8);

    public Set<String> getAllUserId() {
        lock.lock();
        try {
            return userMap.keySet();
        } finally {
            lock.unlock();
        }
    }

    public boolean hasAliveUser() {
        lock.lock();
        try {
            return MapUtils.isNotEmpty(userMap);
        } finally {
            lock.unlock();
        }
    }


    /**
     * 获取参数userId订阅的userId
     * key sub的userId
     * value 用来sub key 的userId的subStream
     */
    public Map<String, List<SubStream>> getMySubbedUser(String userId) {
        lock.lock();
        try {
            List<SubStream> subStreamList = subUserMap.get(userId);
            if (CollectionUtils.isEmpty(subStreamList)) {
                return Maps.newHashMap();
            }
            Map<String, List<SubStream>> curSubUserMap = Maps.newHashMap();
            subStreamList.forEach(subStream -> MapComputeUtils.computeMapList(curSubUserMap, subStream, SubStream::getSubTargetUserId));
            return curSubUserMap;
        } finally {
            lock.unlock();
        }

    }

    /**
     * key pubUserId
     * value pubStreamIdList
     */
    public Map<String, List<PubStream>> getAlUserPubStream() {
        lock.lock();
        try {
            return pubUserMap;
        } finally {
            lock.unlock();
        }
    }

    /**
     * key pubStreamId
     * value pubStream
     */
    public Map<String, PubStream> getAllPubStream() {
        lock.lock();
        try {
            return pubStreamMap;
        } finally {
            lock.unlock();
        }
    }

    public Set<String> getOtherUserIds(String userId) {
        lock.lock();
        try {
            Set<String> allUsers = Sets.newHashSet(userMap.keySet());
            allUsers.remove(userId);
            return allUsers;
        } finally {
            lock.unlock();
        }
    }


    public RtcUser getUser(String userId) {
        lock.lock();
        try {
            return userMap.get(userId);
        } finally {
            lock.unlock();
        }
    }

    public boolean isAnchor(String userId) {
        lock.lock();
        try {
            return pubUserMap.containsKey(userId) || StringUtils.equals(userId, creatorId);
        } finally {
            lock.unlock();
        }
    }

    /**
     * pub不是高并发操作
     */
    public boolean pubStream(PubStream pubStream) {
        lock.lock();
        try {
            if (closeFlag) {
                log.info("room hs been close, can't pub stream, pubStreamId {}", pubStream.getStreamId());
                return false;
            }
            log.info("pub stream is creating, do pub pubStreamId -> {} in room {}", pubStream.getStreamId(), roomId);
            pubStreamMap.put(pubStream.getStreamId(), pubStream);
            MapComputeUtils.computeMapList(pubUserMap, pubStream, PubStream::getPubUserId);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean doPubSubStream(SubStream subStream) {
        lock.lock();
        try {
            if (closeFlag) {
                log.info("room hs been close, can't sub stream subStreamId {}", subStream.getStreamId());
                return false;
            }
            log.info("pub the subStream, relation creating at subStream {} -> pubStream {} in room {}", subStream.getStreamId(), subStream.getSubTargetStreamId(), roomId);
            subStreamMap.put(subStream.getStreamId(), subStream);
            MapComputeUtils.computeMapList(subUserMap, subStream, SubStream::getUserId);
            MapComputeUtils.computeMapList(pubSubRelationMap, subStream, subStream::getSubTargetStreamId);
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * key userId 订阅了本pubStream的userId
     * value subStreamId 订阅本pubStream的subStreamId
     * 挂断pub流时会同时挂断sub本pubStream的subStream
     */
    public Map<String, List<SubStream>> hangupPubStream(String pubStreamId) {
        lock.lock();
        try {
//            if (closeFlag) {
//                log.info("room hs been close, can't hangUp this pubStream pubStreamId {}", pubStreamId);
//                return Collections.emptyMap();
//            }
            PubStream pubStream = pubStreamMap.remove(pubStreamId);
            if (pubStream == null) {
                log.info("can't find pubStream of {} ,so can't hangup, maybe stream already hangup", pubStreamId);
                return Collections.emptyMap();
            }
            log.info("clean pubStream of user {} , try remove pubStream {} which saved in pubUserMap  ", pubStream.getPubUserId(), pubStream.getStreamId());
            MapComputeUtils.computeDelMapList(pubUserMap, pubStream, PubStream::getPubUserId, (pubStream1, pubStream2) -> StringUtils.equals(pubStream1.getStreamId(), pubStream2.getStreamId()));
            return cleanPubSubRelation(pubStream);
        } finally {
            lock.unlock();
        }
    }

    /**
     * key sub本pubStream的userId
     * value sub本pubStream的subStream
     */
    private Map<String, List<SubStream>> cleanPubSubRelation(PubStream pubStream) {
        log.info("clean the pub sub relation, will remove subStream which subbed pubStream  {}", pubStream.getStreamId());
        String pubStreamId = pubStream.getStreamId();
        List<SubStream> subStreamList = pubSubRelationMap.remove(pubStreamId);
        if (CollectionUtils.isEmpty(subStreamList)) {
            log.info("no subStream which subbed pubStream {}", pubStreamId);
            return Collections.emptyMap();
        }
        log.info("will close those subStream ,because they subbed pubStream {} , the will close subStream is {}", pubStream.getStreamId(), subStreamList);
        Map<String, List<SubStream>> curSubUserMap = Maps.newHashMap();
        subStreamList.forEach(subStream -> {
            log.info("will remove subStream which in subStreamMap, subStreamId {}", subStream.getStreamId());
            MapComputeUtils.computeMapList(curSubUserMap, subStream, subStream::getUserId);
            SubStream subStreamTemp = subStreamMap.remove(subStream.getStreamId());
            if (subStreamTemp == null) {
                log.error("can't find sub stream by subStreamId {} in subStreamMap, something wrong ", subStream.getStreamId());
                return;
            }
            cleanUserSubStream(subStream);
        });
        return curSubUserMap;
    }

    public Pair<SubStream, PubStream> hangupSubStream(String subStreamId) {
        lock.lock();
        try {
//            if (closeFlag) {
//                log.info("room hs been close, can't hangUp this subStream subStreamId {}", subStreamId);
//                return null;
//            }
            SubStream subStream = subStreamMap.remove(subStreamId);
            if (subStream == null) {
                log.warn("can't find sub stream by subStreamId {} ,maybe subStream has destroy ", subStreamId);
                return null;
            }
            cleanUserSubStream(subStream);
            cleanPubSubRelation(subStream);
            PubStream pubStream = pubStreamMap.get(subStream.getSubTargetStreamId());

            return ImmutablePair.of(subStream, pubStream);
        } finally {
            lock.unlock();
        }
    }

    private void cleanUserSubStream(SubStream subStream) {
        log.info("clean subStream in subUserMap the will been clean subStreamId {}", subStream.getStreamId());
        MapComputeUtils.computeDelMapList(subUserMap, subStream, SubStream::getUserId, (subStream1, subStream2) -> StringUtils.equals(subStream1.getStreamId(), subStream2.getStreamId()));
    }

    private void cleanPubSubRelation(SubStream subStream) {
        log.info("clean the pubSubRelation which save in pubSubRelationMap try remove subStream {} it's subbed the pubStream {}", subStream.getStreamId(), subStream.getSubTargetStreamId());
        MapComputeUtils.computeDelMapList(pubSubRelationMap, subStream, SubStream::getSubTargetStreamId, (subStream1, subStream2) -> StringUtils.equals(subStream1.getStreamId(), subStream2.getStreamId()));
    }

    public boolean enterUser(RtcUser user) {
        lock.lock();
        try {
            if (closeFlag) {
                log.info("room hs been close, can't enter room, enter userId {}", user.getId());
                return false;
            }
            userMap.put(user.getId(), user);
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * key subSuerId,sub了本人流的userId
     * value subStreamId, sub了本人流的subStreamIdList
     */
    public Map<String, List<SubStream>> leaveAndGetSubStream(String userId) {
        lock.lock();
        try {
//            if (closeFlag) {
//                log.info("room has close, can't do leave room");
//                return Collections.emptyMap();
//            }
            RtcUser rtcUser = userMap.remove(userId);
            if (rtcUser == null) {
                log.info("no user of {} in room {} ", userId, roomId);
                return Collections.emptyMap();
            }
            List<PubStream> pubStreamIdList = pubUserMap.get(userId);
            Map<String, List<SubStream>> curSubUserMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(pubStreamIdList)) {
                log.info("will close my pubStream and some subStream which sub my pubStream");
                List<String> streamIdList = pubStreamIdList.stream().map(PubStream::getStreamId).toList();
                for (String pubStreamId : streamIdList) {
                    log.info("begin hangup pubStream of {}", pubStreamId);
                    Map<String, List<SubStream>> map = hangupPubStream(pubStreamId);
                    //之所以会有这一步是因为一个人可能pub多路流，所以要遍历合并
                    map.forEach((subUserId, subStreamList) -> curSubUserMap.compute(subUserId, (k, v) -> {
                        if (v == null) {
                            v = Lists.newArrayList();
                        }
                        v.addAll(subStreamList);
                        return v;
                    }));
                }
            }

            List<SubStream> subStreamList = subUserMap.remove(userId);
            if (CollectionUtils.isNotEmpty(subStreamList)) {
                log.info("close those subStream which i used to sub other the subStreamList {}", subStreamList);
                List<String> mySubStreamIdList = subStreamList.stream().map(SubStream::getStreamId).toList();
                mySubStreamIdList.forEach(this::hangupSubStream);
            }

            return curSubUserMap;
        } finally {
            lock.unlock();
        }
    }


    /**
     * 关闭操作尽量先执行closeMark再执行closeClean，因为需要在关闭后再根据房间内的用户情况通知用户房间已经被关闭，所以在关闭时不要改变userMap的状态
     */
    public boolean closeMarkRoom() {
        lock.lock();
        try {
            log.info("will mark as close, roomId {}", this.roomId);
            closeFlag = true;
            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean closeCleanRoom() {
        lock.lock();
        try {
            if (!closeFlag) {
                log.info("room hasn't close, can't clean room roomId {}", this.roomId);
                return false;
            }
            subStreamMap.keySet().forEach((subStreamId) -> {
                log.info("will close subStream by hangup, the subStreamId {}", subStreamId);
                hangupSubStream(subStreamId);
            });
            pubStreamMap.keySet().forEach((pubStreamId) -> {
                log.info("when close pubStream by hangup the pubStreamId {}", pubStreamId);
                hangupPubStream(pubStreamId);
            });
            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean forceCloseRoom() {
        lock.lock();
        try {
            closeFlag = true;
            //一定要先挂斷sub流，因为pub流挂断的时候也会挂断对应的sub流，而挂断sub流则不会影响pub流，这样pub/sub关系才能完整
            subStreamMap.forEach((subStreamId, subStream) -> {
                log.info("when close room, subStream still alive, will hangup the subStream {}", subStreamId);
                hangupSubStream(subStreamId);
            });
            pubStreamMap.forEach((pubStreamId, pubStream) -> {
                log.info("when close room, pubStream still alive, will hangup the pubStream {}", pubStreamId);
                hangupPubStream(pubStreamId);
            });
            return true;
        } finally {
            lock.unlock();
        }

    }

    /**
     * key 本userId创建的subStreamId
     * value 本userId sub的pubStream
     */
    public Map<String, PubStream> getSubPubRelation(String userId) {
        lock.lock();
        try {
            List<SubStream> subStreamList = subUserMap.get(userId);
            Map<String, PubStream> subPubMap = Maps.newHashMap();
            if (CollectionUtils.isEmpty(subStreamList)) {
                return subPubMap;
            }
            subStreamList.forEach(subStream -> subPubMap.put(subStream.getStreamId(), pubStreamMap.get(subStream.getSubTargetStreamId())));
            return subPubMap;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 作为判断room是否存活的基础能力，这里判断不存活就肯定不存活只存在基础的判断，包括过期时间和关闭开关
     * 业务上决定怎么样能够过期(比如谁能更新超时时间)，什么情况下能关闭，通过context来控制。
     * 如果业务上认为过期了，但是在room认为没过期，导致更新超时时间存在gap，还是以业务上为准，
     * 这说明更新超时时间这个动作也必须以业务上的操的竞争条件作为准
     */
    public boolean isRoomActive() {
        lock.lock();
        try {
            return expireTime > DateTimeUtils.currentTime() && !closeFlag;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 存在和跟新过期时间时的竞争条件，但是可以接受
     */
    public boolean isRoomExpireInTime(long curTime) {
        lock.lock();
        try {
            return expireTime < curTime;
        } finally {
            lock.unlock();
        }
    }

    /**
     * key sub了本人pubStream的userId
     * value sub了本人pubStream的subStream;
     */
    public Map<String, List<SubStream>> getSubUserStream(String userId) {
        lock.lock();
        try {
            List<PubStream> pubStreamList = pubUserMap.get(userId);
            if (CollectionUtils.isEmpty(pubStreamList)) {
                log.info("user {} no pub any stream ", userId);
                return Collections.emptyMap();
            }

            Map<String, List<SubStream>> subUserStreamMap = Maps.newHashMap();

            pubStreamList.forEach(pubStream -> {
                List<SubStream> subStreamList = pubSubRelationMap.get(pubStream.getStreamId());
                if (CollectionUtils.isEmpty(subStreamList)) {
                    return;
                }
                subStreamList.forEach(subStream -> {
                    MapComputeUtils.computeMapList(subUserStreamMap, subStream, subStream::getUserId);
                });
            });
            return subUserStreamMap;
        } finally {
            lock.unlock();
        }
    }

    public List<RtcUser> getExpireUser() {
        lock.lock();
        try {
            return userMap.entrySet().parallelStream().filter(entry -> entry.getValue().getTimeout() <= DateTimeUtils.currentTime()).map(Map.Entry::getValue).toList();
        } finally {
            lock.unlock();
        }
    }

    public void extendRoomTimeout(long expireTime) {
        lock.lock();
        try {
            if (closeFlag) {
                log.info("room has close, can't extend room timeout");
                return;
            }
            this.expireTime = Math.max(expireTime, this.expireTime);
        } finally {
            lock.unlock();
        }
    }

}

package com.muses.persistence.mysql.service.impl;

import com.muses.persistence.mysql.entity.LiveProgram;
import com.muses.persistence.mysql.repo.ILiveProgramRepo;
import com.muses.persistence.mysql.service.ILiveProgramRepoService;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName LiveProgramService
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/10 11:40
 */
@Slf4j
@Component
@Transactional(value = "transactionManagerMysql")
public class LiveProgramRepoService implements ILiveProgramRepoService {

    @Autowired
    private ILiveProgramRepo liveProgramRepo;

    public LiveProgram findById(String liveProgramId) {
        return liveProgramRepo.findById(liveProgramId).orElse(null);
    }

    public LiveProgram findByLiveRoomId(String liveRoomId) {
        return liveProgramRepo.findByLiveRoomId(liveRoomId);
    }


    public LiveProgram save(LiveProgram liveProgram) {
        return liveProgramRepo.save(liveProgram);
    }

    @Override
    public List<LiveProgram> findByUserIdAndState(String userId, List<Integer> stateList) {
        return liveProgramRepo.findByAnchorAndStateIn(userId, stateList);
    }

    public List<LiveProgram> findByBeginTimeAndState(int pageNum, long beginTime, List<Integer> state) {
        Sort.Direction direction = Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageNum, 20, direction, "beginTime");
        Specification<LiveProgram> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesAndList = new ArrayList<>();
            predicatesAndList.add(criteriaBuilder.greaterThan(root.get("beginTime"), beginTime));
            predicatesAndList.add(root.get("state").in(state));

            return criteriaBuilder.and(predicatesAndList.toArray(new Predicate[0]));
        };
        return liveProgramRepo.findAll(specification, pageable).getContent();
    }

    public List<LiveProgram> findByBeginTimeAndStateAndAnchor(int pageNum,long beginTime, List<Integer> state, String userId){
        Sort.Direction direction = Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageNum, 20, direction, "beginTime");
        Specification<LiveProgram> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesAndList = new ArrayList<>();
            predicatesAndList.add(criteriaBuilder.greaterThan(root.get("beginTime"), beginTime));
            predicatesAndList.add(root.get("state").in(state));
            predicatesAndList.add(criteriaBuilder.notEqual(root.get("anchor"), userId));
            return criteriaBuilder.and(predicatesAndList.toArray(new Predicate[0]));
        };
        return liveProgramRepo.findAll(specification, pageable).getContent();
    }
}

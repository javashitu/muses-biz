package com.muses.persistence.mysql.service.impl;

import com.muses.persistence.mysql.entity.VideoProgram;
import com.muses.persistence.mysql.repo.IVideoProgramRepo;
import com.muses.persistence.mysql.service.IVideoProgramRepoService;
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
 * @ClassName VideoProgramRepoService
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/9 10:54
 */
@Slf4j
@Component
@Transactional(value = "transactionManagerMysql")
public class VideoProgramRepoService implements IVideoProgramRepoService {

    @Autowired
    private IVideoProgramRepo videoProgramRepo;

    @Override
    public VideoProgram findById(String id) {
        return videoProgramRepo.findById(id).orElse(null);
    }

    @Override
    public List<VideoProgram> findByIdList(List<String> ids) {
        return videoProgramRepo.findAllById(ids);
    }

    @Override
    public VideoProgram save(VideoProgram videoProgram) {
        return videoProgramRepo.save(videoProgram);
    }

    @Override
    public List<VideoProgram> findByUserId(String userId, int pageNum) {
        Sort.Direction direction = Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(pageNum, 20, direction, "id");
        Specification<VideoProgram> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesAndList = new ArrayList<>();
            predicatesAndList.add(criteriaBuilder.equal(root.get("userId").as(String.class), userId));
            predicatesAndList.add(criteriaBuilder.isNull(root.get("delFlag").as(String.class)));
            return criteriaBuilder.and(predicatesAndList.toArray(new Predicate[0]));
        };
        return videoProgramRepo.findAll(specification, pageable).getContent();
    }

    @Override
    public List<VideoProgram> findAll(int pageNum) {
        Sort.Direction direction = Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(pageNum, 20, direction, "id");
        Specification<VideoProgram> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesAndList = new ArrayList<>();
            predicatesAndList.add(criteriaBuilder.notEqual(root.get("delFlag").as(String.class), root.get("id").as(String.class)));
            return criteriaBuilder.and(predicatesAndList.toArray(new Predicate[0]));
        };
        return videoProgramRepo.findAll(specification, pageable).getContent();
    }

}

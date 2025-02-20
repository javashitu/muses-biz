package com.muses.persistence.mysql.service.impl;

import com.muses.persistence.mysql.entity.Program;
import com.muses.persistence.mysql.repo.IProgramRepo;
import com.muses.persistence.mysql.service.IProgramRepoService;
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
 * @ClassName ProgramRepoService
 * @Description:
 * @Author: java使徒
 * @CreateDate: 2024/10/9 10:30
 */
@Slf4j
@Component
@Transactional(value = "transactionManagerMysql")
public class ProgramRepoService implements IProgramRepoService {

    @Autowired
    private IProgramRepo programRepo;

    @Override
    public Program findById(String id) {
        return programRepo.findById(id).orElse(null);
    }

    public Program save(Program program) {
        return programRepo.save(program);
    }

    public List<Program> findByUserId(String userId, int pageNum) {
        Sort.Direction direction = Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(pageNum, 20, direction, "id");
        Specification<Program> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicatesAndList = new ArrayList<>();
            predicatesAndList.add(criteriaBuilder.equal(root.get("userId").as(String.class), userId));
            return criteriaBuilder.and(predicatesAndList.toArray(new Predicate[0]));
        };
        return programRepo.findAll(specification, pageable).getContent();
    }
}

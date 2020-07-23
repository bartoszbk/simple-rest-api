package com.bartoszkrol.simplerestapi.repository;

import com.bartoszkrol.simplerestapi.domain.RequestCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequestCountRepository extends JpaRepository<RequestCount, String> {
    Optional<RequestCount> getByLogin(String login);
}

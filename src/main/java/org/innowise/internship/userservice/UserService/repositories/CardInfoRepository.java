package org.innowise.internship.userservice.UserService.repositories;

import org.innowise.internship.userservice.UserService.entities.CardInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardInfoRepository extends JpaRepository<CardInfo, Long> {

    @Query(value = "SELECT * FROM card_info WHERE id IN :ids", nativeQuery = true)
    List<CardInfo> findAllByIdIn(@Param("ids") List<Long> ids);
}

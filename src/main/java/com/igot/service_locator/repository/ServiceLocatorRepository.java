package com.igot.service_locator.repository;

import com.igot.service_locator.entity.ServiceLocatorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceLocatorRepository extends JpaRepository<ServiceLocatorEntity, String> {


    @Query("select s from ServiceLocatorEntity s where s.isActive = :isActive")
    Page<Object> fetchAll(@Param("isActive") Boolean isActive, Pageable pageable);

    Optional<ServiceLocatorEntity> findByServiceCodeAndIsActiveTrue(String serviceCode);

    Optional<ServiceLocatorEntity> findByIdAndIsActive(@Param("id") String id,@Param("isActive") boolean isActive);
}
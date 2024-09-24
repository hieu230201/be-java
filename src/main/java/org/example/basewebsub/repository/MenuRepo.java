package org.example.basewebsub.repository;

import org.example.basewebsub.entity.MenuEntity;
import org.example.basewebsub.response.MenuResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepo extends JpaRepository<MenuEntity,Long> {

    @Query("""
    SELECT NEW org.example.basewebsub.response.MenuResponse
    (m.id, m.name, m.path, m.parentId, m.deleteFlag)
    FROM MenuEntity m
    WHERE m.parentId = :parentId AND m.deleteFlag = false
    """)
    List<MenuResponse> findByParentIDAndDeleteFlagIsFalse(Integer parentId);
}

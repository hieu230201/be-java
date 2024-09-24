package org.example.basewebsub.repository;

import org.example.basewebsub.entity.UsersRolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsersRolesRepo extends JpaRepository<UsersRolesEntity, Long> {
    List<UsersRolesEntity> findByUserId(Long id);
}

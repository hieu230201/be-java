package org.example.basewebsub.repository;

import org.example.basewebsub.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepo extends JpaRepository<UsersEntity, Long> {
    UsersEntity findByUsernameAndDeleteFlagIsFalse(String username);

    UsersEntity findByEmailAndDeleteFlagIsFalse(String email);

}

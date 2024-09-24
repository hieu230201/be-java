package org.example.basewebsub.service;

import org.example.basewebsub.entity.UsersEntity;

public interface UsersService {
    UsersEntity createOrUpdateUser(UsersEntity user);

    UsersEntity findUserByEmail(String email);

    UsersEntity findUserByUsername(String username);

    UsersEntity deleteUser(Long id);

}

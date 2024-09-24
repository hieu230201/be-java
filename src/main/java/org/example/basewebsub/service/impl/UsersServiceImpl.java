package org.example.basewebsub.service.impl;

import org.example.basewebsub.entity.UsersEntity;
import org.example.basewebsub.repository.UsersRepo;
import org.example.basewebsub.service.UsersService;
import org.example.basewebsub.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsersServiceImpl implements UsersService {

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UsersEntity createOrUpdateUser(UsersEntity user) {
        if (user.getId() == null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            UsersEntity oldUser = usersRepo.findById(user.getId()).get();
            copyUnchangedFields(user, oldUser);
        }
        return usersRepo.save(user);
    }

    @Override
    public UsersEntity findUserByEmail(String email) {
        if (StringUtil.isNullOrEmpty(email)) {
            return null;
        }
        return usersRepo.findByEmailAndDeleteFlagIsFalse(email);
    }

    @Override
    public UsersEntity findUserByUsername(String username) {
        if (StringUtil.isNullOrEmpty(username)) {
            return null;
        }
        return usersRepo.findByUsernameAndDeleteFlagIsFalse(username);
    }

    @Override
    public UsersEntity deleteUser(Long id) {
        if (id == null) {
            return null;
        }
        UsersEntity user = usersRepo.findById(id).orElse(null);
        if (user != null) {
            usersRepo.delete(user);
        }
        return user;
    }

    private void copyUnchangedFields(UsersEntity newUser, UsersEntity oldUser) {
        newUser.setPassword(oldUser.getPassword());
        newUser.setCreatedDate(oldUser.getCreatedDate());
        newUser.setCreatedBy(oldUser.getCreatedBy());
        newUser.setDeleteFlag(oldUser.getDeleteFlag());
    }
}

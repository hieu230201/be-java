package org.example.basewebsub.service.impl;

import org.example.basewebsub.entity.UsersEntity;
import org.example.basewebsub.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsersRepo usersRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsersEntity daoUser = usersRepo.findByUsernameAndDeleteFlagIsFalse(username);
        if (daoUser == null) {
            throw new UsernameNotFoundException("User not founddd");
        }
        return new org.springframework.security.core.userdetails.User(daoUser.getUsername(), daoUser.getPassword(),
                new ArrayList<>());
    }
}

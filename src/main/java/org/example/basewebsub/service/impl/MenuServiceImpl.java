package org.example.basewebsub.service.impl;

import org.example.basewebsub.entity.MenuEntity;
import org.example.basewebsub.entity.UsersEntity;
import org.example.basewebsub.repository.MenuRepo;
import org.example.basewebsub.response.MenuResponse;
import org.example.basewebsub.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    private MenuRepo menuRepo;

    @Override
    public List<MenuEntity> getAllMenu() {
        return menuRepo.findAll();
    }

    @Override
    public MenuEntity createOrUpdateMenu(MenuEntity menuEntity) {
        return menuRepo.save(menuEntity);
    }

    @Override
    public List<MenuResponse> getMenuByParentId(Integer parentId) {
        return menuRepo.findByParentIDAndDeleteFlagIsFalse(parentId);
    }

    @Override
    public Optional<MenuEntity> findMenuById(Long id) {
        return menuRepo.findById(id);
    }

    @Override
    public MenuEntity deleteMenuById(Long id) {
        MenuEntity menuEntity = menuRepo.findById(id).orElse(null);
        if (menuEntity != null) {
            menuRepo.delete(menuEntity);
        }
        return menuEntity;
    }

}

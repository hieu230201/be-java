package org.example.basewebsub.service;

import org.example.basewebsub.entity.MenuEntity;
import org.example.basewebsub.response.MenuResponse;

import java.util.List;
import java.util.Optional;

public interface MenuService {

    List<MenuEntity> getAllMenu();

    MenuEntity createOrUpdateMenu(MenuEntity menuEntity);

    List<MenuResponse> getMenuByParentId(Integer parentId);

    Optional<MenuEntity> findMenuById(Long id);

    MenuEntity deleteMenuById(Long id);
}

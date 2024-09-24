package org.example.basewebsub.controller.menu;

import org.example.basewebsub.entity.MenuEntity;
import org.example.basewebsub.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/list-menu")
    public ResponseEntity<?> getAllMenu(){
        return ResponseEntity.ok().body(menuService.getAllMenu());
    }

    @GetMapping("/find-by-parentId")
    public ResponseEntity<?> getMenuByParentID(@RequestParam("parentId") String parentId){
        return ResponseEntity.ok().body(menuService.getMenuByParentId(Integer.valueOf(parentId)));
    }

    @PostMapping("/create-menu")
    public ResponseEntity<?> addMenu(@RequestBody MenuEntity menu){
        Integer parentId = menu.getParentId();
        Optional<MenuEntity> optionMenu = menuService.findMenuById(Long.parseLong(String.valueOf(parentId)));
        if(optionMenu.isEmpty()){
            menu.setParentId(null);
        }
        return ResponseEntity.ok().body(menuService.createOrUpdateMenu(menu));
    }

    @PutMapping("/update-menu")
    public ResponseEntity<?> updateMenu(@RequestBody MenuEntity menu){
        Integer parentId = menu.getParentId();
        Optional<MenuEntity> optionMenu = menuService.findMenuById(Long.parseLong(String.valueOf(parentId)));
        if(optionMenu.isEmpty()){
            menu.setParentId(null);
        }
        return ResponseEntity.ok().body(menuService.createOrUpdateMenu(menu));
    }

    @DeleteMapping("/delete-menu")
    public ResponseEntity<?> deleteUser(@RequestParam("id") String id) {
        return ResponseEntity.ok().body(menuService.deleteMenuById(Long.parseLong(id)));
    }
}

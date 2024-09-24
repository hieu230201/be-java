package org.example.basewebsub.controller.auth;

import org.example.basewebsub.controller.bases.BaseController;
import org.example.basewebsub.entity.UsersEntity;
import org.example.basewebsub.request.UserChangeRequest;
import org.example.basewebsub.response.ResponseData;
import org.example.basewebsub.service.UsersService;
import org.example.basewebsub.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class UserController extends BaseController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/createOrUpdateUser")
    public ResponseEntity<?> createUser(@RequestBody UsersEntity user) {
        String username = getUsername();
        if (user.getId() == null) {
            user.setCreatedBy("ADMIN");
        } else {
            user.setModifiedBy(StringUtil.isNotNullAndEmpty(username) ? username : "");
        }
        return ResponseEntity.ok().body(usersService.createOrUpdateUser(user));
    }

    @PostMapping("/delete-user")
    public ResponseEntity<?> deleteUser(@RequestParam("id") String id) {
        return ResponseEntity.ok().body(usersService.deleteUser(Long.parseLong(id)));
    }

    @GetMapping("/find-user-by-email")
    public ResponseEntity<?> findByEmail(@RequestParam("email") String email) {
        return ResponseEntity.ok().body(usersService.findUserByEmail(email));
    }

    @GetMapping("/find-user-by-username")
    public ResponseEntity<?> findByUserName(@RequestParam("username") String username) {
        return ResponseEntity.ok().body(usersService.findUserByUsername(username));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody UserChangeRequest request){
        String username = request.getUsername();
        UsersEntity user =  usersService.findUserByUsername(username);
        if (user != null) {
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                String encodedPassword = passwordEncoder.encode(request.getRepassword());
                user.setPassword(encodedPassword);
                usersService.createOrUpdateUser(user);
                return ResponseEntity.ok().body("Password changed successfully");
            } else {
                return ResponseEntity.badRequest().body("Current password is incorrect");
            }
        }
        return ResponseEntity.badRequest().body("User not found");
    }

}

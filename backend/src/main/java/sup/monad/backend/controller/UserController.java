package sup.monad.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import sup.monad.backend.pojo.User;
import sup.monad.backend.pojo.UserInfo;
import sup.monad.backend.service.UserService;
import sup.monad.backend.service.IUserService.ResetPasswordRequest;
import sup.monad.backend.exception.SignInException;
import sup.monad.backend.exception.UnauthorizedException;
import sup.monad.backend.exception.ResetPasswordException;

@RestController
@RequestMapping(value = "/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @JsonSerialize
    @JsonDeserialize
    @Data
    public static class SignUpBody {
        public User user;
        public UserInfo userInfo;
    }
    @PostMapping("/signUp")
    public ResponseEntity<Object> signUp(@RequestBody SignUpBody body) {
        User user = body.user;
        UserInfo userInfo = body.userInfo;
        return new ResponseEntity<>(userService.signUp(user, userInfo, "user"), HttpStatus.CREATED);
    }

    @PostMapping("/signIn")
    public ResponseEntity<Object> signIn(@RequestBody User user) throws SignInException {
        return new ResponseEntity<>(userService.signIn(user), HttpStatus.OK);
    }

    @PutMapping("/resetPassword")
    public ResponseEntity<Object> resetPassword(@RequestBody ResetPasswordRequest body) throws ResetPasswordException {
        userService.resetPassword(body);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @GetMapping("/auth")
    public ResponseEntity<Object> auth(@RequestHeader(value = "Authorization") String token)
            throws UnauthorizedException {
        return new ResponseEntity<>(userService.auth(token), HttpStatus.OK);
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @PostMapping("/registerInfo")
    public UserInfo registerUserInfo(@RequestBody UserInfo userInfo) {
        return userService.saveUserInfo(userInfo);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.findUserById(id);
    }

    @GetMapping("/info/{id}")
    public UserInfo getUserInfoById(@PathVariable Long id) {
        return userService.findUserInfoById(id);
    }
}
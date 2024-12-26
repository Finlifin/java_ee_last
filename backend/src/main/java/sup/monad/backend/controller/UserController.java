package sup.monad.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sup.monad.backend.pojo.User;
import sup.monad.backend.service.UserService;
import sup.monad.backend.service.IUserService.ResetPasswordRequest;
import sup.monad.backend.exception.SignInException;
import sup.monad.backend.exception.SignUpException;
import sup.monad.backend.exception.UnauthorizedException;
import sup.monad.backend.exception.ResetPasswordException;

@RestController
@RequestMapping(value = "/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signUp")
    public ResponseEntity<Object> signUp(@RequestBody User user) throws SignUpException {
        return new ResponseEntity<>(userService.signUp(user), HttpStatus.CREATED);
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
}
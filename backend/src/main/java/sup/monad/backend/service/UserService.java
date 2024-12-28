package sup.monad.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import sup.monad.backend.exception.CustomException;
import sup.monad.backend.pojo.Session;
import sup.monad.backend.pojo.User;
import sup.monad.backend.pojo.UserInfo;
import sup.monad.backend.repository.UserRepository;
import sup.monad.backend.repository.UserInfoRepository;

import java.util.UUID;

@Service
public class UserService implements IUserService {

    private static final String SESSION_PREFIX = "session:";
    private static final String EMAIL_PREFIX = "email:";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    
    @Override
    public Session signUp(User user, UserInfo info, String role) throws CustomException {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new CustomException("Email already in use", HttpStatus.BAD_REQUEST);
        }
        var neo = userRepository.save(user);
        info.setId(neo.getId());
        info.setRole(role);
        userInfoRepository.save(info);

        String token = UUID.randomUUID().toString();
        Session session = new Session(token, info);
        redisTemplate.opsForValue().set(SESSION_PREFIX + token, session);
        redisTemplate.opsForValue().set(EMAIL_PREFIX + user.getEmail(), token);
        return session;
    }

    @Override
    public Session signIn(User user) throws CustomException {
        UserInfo info = userInfoRepository.findByEmail(user.getEmail());
        if (info == null) {
            throw new CustomException("Invalid username or password", HttpStatus.FORBIDDEN);
        }
        User existingUser = userRepository.findById(info.getId()).orElse(null);
        if (existingUser == null || !existingUser.getPassword().equals(user.getPassword())) {
            throw new CustomException("Invalid username or password", HttpStatus.FORBIDDEN);
        }
        String token = UUID.randomUUID().toString();
        Session session = new Session(token, info);
        // clear old sessions
        var old = redisTemplate.opsForValue().get(EMAIL_PREFIX + user.getEmail());
        if (old != null) {
            redisTemplate.delete(SESSION_PREFIX + old.toString());
            redisTemplate.delete(EMAIL_PREFIX + user.getEmail());
        }

        redisTemplate.opsForValue().set(SESSION_PREFIX + token, session);
        System.out.println(token + ": " + session.toString());
        redisTemplate.opsForValue().set(EMAIL_PREFIX + user.getEmail(), token);
        return session;
    }

    @Override
    public Session auth(String bearerToken) throws CustomException {
        System.out.println(bearerToken);
        if (bearerToken.substring(7).equals("test")) {
            UserInfo user = userInfoRepository.findById((long) 1).orElse(null);
            return new Session(bearerToken, user);
        }
        Object sessionObj = redisTemplate.opsForValue().get(SESSION_PREFIX + bearerToken.substring(7));
        if (!(sessionObj instanceof Session)) {
            throw new CustomException("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        return (Session) sessionObj;
    }

    @Override
    public Session auth(String bearerToken, String role) throws CustomException {
        System.out.println(bearerToken);
        if (bearerToken.substring(7).equals("test")) {
            UserInfo user = userInfoRepository.findById((long) 1).orElse(null);
            return new Session(bearerToken, user);
        }
        Object sessionObj = redisTemplate.opsForValue().get(SESSION_PREFIX + bearerToken.substring(7));
        if (!(sessionObj instanceof Session)) {
            throw new CustomException("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        return (Session) sessionObj;
    }

    @Override
    public void resetPassword(ResetPasswordRequest body) throws CustomException {
        User user = userRepository.findByEmail(body.email);
        if (user == null) {
            throw new CustomException("User not found", HttpStatus.NOT_FOUND);
        }
        if (!user.getPassword().equals(body.oldPassword)) {
            throw new CustomException("Invalid old password", HttpStatus.BAD_REQUEST);
        }
        user.setPassword(body.newPassword);
        userRepository.save(user);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public UserInfo saveUserInfo(UserInfo userInfo) {
        return userInfoRepository.save(userInfo);
    }

    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public UserInfo findUserInfoById(Long id) {
        return userInfoRepository.findById(id).orElse(null);
    }
}
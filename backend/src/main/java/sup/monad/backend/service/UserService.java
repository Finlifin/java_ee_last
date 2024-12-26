package sup.monad.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import sup.monad.backend.exception.CustomException;
import sup.monad.backend.pojo.Session;
import sup.monad.backend.pojo.User;
import sup.monad.backend.repository.UserRepository;

import java.util.UUID;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Session signUp(User user) throws CustomException {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new CustomException("Email already in use", HttpStatus.BAD_REQUEST);
        }
        userRepository.save(user);
        String token = UUID.randomUUID().toString();
        Session session = new Session(token, user);
        redisTemplate.opsForValue().set(token, session);
        redisTemplate.opsForValue().set(user.getEmail(), token);
        return session;
    }

    @Override
    public Session signIn(User user) throws CustomException {
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser == null || !existingUser.getPassword().equals(user.getPassword())) {
            throw new CustomException("Invalid username or password", HttpStatus.UNAUTHORIZED);
        }
        String token = UUID.randomUUID().toString();
        Session session = new Session(token, existingUser);
        // clear old sessions
        var old = redisTemplate.opsForValue().get(user.getEmail());
        if (old != null) {
            redisTemplate.delete(old.toString());
            redisTemplate.delete(user.getEmail());
        }

        redisTemplate.opsForValue().set(token, session);
        System.out.println(token + ": " + session.toString());
        redisTemplate.opsForValue().set(user.getEmail(), token);
        return session;
    }

    @Override
    public Session auth(String bearerToken) throws CustomException {
        System.out.println(bearerToken);
        // TODO: add 'test' and sample user token for testing
        Object sessionObj = redisTemplate.opsForValue().get(bearerToken.substring(7));
        if (!(sessionObj instanceof Session)) {
            throw new CustomException("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        return (Session) sessionObj;
    }

    @Override
    public Session auth(String bearerToken, String role) throws CustomException {
        System.out.println(bearerToken);
        // TODO: add 'test' and sample user token for testing
        Object sessionObj = redisTemplate.opsForValue().get(bearerToken.substring(7));
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
}
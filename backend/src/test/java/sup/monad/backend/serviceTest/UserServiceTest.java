package sup.monad.backend.serviceTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import sup.monad.backend.exception.CustomException;
import sup.monad.backend.pojo.Session;
import sup.monad.backend.pojo.User;
import sup.monad.backend.pojo.UserInfo;
import sup.monad.backend.repository.UserRepository;
import sup.monad.backend.repository.UserInfoRepository;
import sup.monad.backend.service.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserInfoRepository userInfoRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private UserService userService;

    // 提取构建用户对象的方法
    private User buildUser(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }

    // 提取构建用户信息对象的方法
    private UserInfo buildUserInfo(String username, String email, String role) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(username);
        userInfo.setEmail(email);
        userInfo.setRole(role);
        return userInfo;
    }

    @Test
    void shouldSignUpSuccessfully() {
        // 模拟userRepository.findByEmail方法返回null，表示邮箱未被使用
        when(userRepository.findByEmail(any(String.class))).thenReturn(null);

        // 模拟userRepository.save方法返回保存后的用户对象
        User userToSave = buildUser("test@example.com", "password");
        User savedUser = buildUser("test@example.com", "password");
        savedUser.setId(1L);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // 模拟userInfoRepository.save方法正常执行
        UserInfo userInfo = buildUserInfo("testuser", "test@example.com", "user");
        when(userInfoRepository.save(any(UserInfo.class))).thenReturn(userInfo);

        try {
            Session session = userService.signUp(userToSave, userInfo, "user");
            assertNotNull(session);
            assertEquals("test@example.com", session.userInfo.getEmail());
            assertEquals("user", session.userInfo.getRole());
            Mockito.verify(redisTemplate).opsForValue().set("session:" + session.token, session);
            Mockito.verify(redisTemplate).opsForValue().set("email:test@example.com", session.token);
        } catch (CustomException e) {
            fail("不应该抛出异常");
        }
    }

    @Test
    void shouldThrowExceptionWhenSignUpWithExistingEmail() {
        // 模拟userRepository.findByEmail方法返回一个已存在的用户，表示邮箱已被使用
        when(userRepository.findByEmail(any(String.class))).thenReturn(buildUser("test@example.com", "password"));

        User userToSave = buildUser("test@example.com", "password");
        UserInfo userInfo = buildUserInfo("testuser", "test@example.com", "user");

        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.signUp(userToSave, userInfo, "user");
        });
        assertEquals("Email already in use", exception.getMessage());
    }

    @Test
    void shouldSignInSuccessfully() {
        // 模拟userInfoRepository.findByEmail方法返回用户信息
        UserInfo userInfo = buildUserInfo("testuser", "test@example.com", "user");
        userInfo.setId(1L);
        when(userInfoRepository.findByEmail(any(String.class))).thenReturn(userInfo);

        // 模拟userRepository.findById方法返回用户对象，且密码匹配
        User user = buildUser("test@example.com", "password");
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        try {
            User signInUser = buildUser("test@example.com", "password");
            Session session = userService.signIn(signInUser);
            assertNotNull(session);
            assertEquals("test@example.com", session.userInfo.getEmail());
            assertEquals("user", session.userInfo.getRole());
            Mockito.verify(redisTemplate).opsForValue().set("session:" + session.token, session);
            Mockito.verify(redisTemplate).opsForValue().set("email:test@example.com", session.token);
        } catch (CustomException e) {
            fail("不应该抛出异常");
        }
    }

    @Test
    void shouldThrowExceptionWhenSignInWithInvalidUsernameOrPassword() {
        // 模拟userInfoRepository.findByEmail方法返回用户信息，但userRepository.findById方法返回null，表示用户不存在
        UserInfo userInfo = buildUserInfo("testuser", "test@example.com", "user");
        userInfo.setId(1L);
        when(userInfoRepository.findByEmail(any(String.class))).thenReturn(userInfo);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        User signInUser = buildUser("test@example.com", "password");

        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.signIn(signInUser);
        });
        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenSignInWithWrongPassword() {
        // 模拟userInfoRepository.findByEmail方法返回用户信息，userRepository.findById方法返回用户对象，但密码不匹配
        UserInfo userInfo = buildUserInfo("testuser", "test@example.com", "user");
        userInfo.setId(1L);
        when(userInfoRepository.findByEmail(any(String.class))).thenReturn(userInfo);

        User user = buildUser("test@example.com", "wrongpassword");
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User signInUser = buildUser("test@example.com", "password");

        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.signIn(signInUser);
        });
        assertEquals("Invalid username or password", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Bearer validtoken", "Bearer test"})
    void shouldAuthenticateSuccessfully(String bearerToken) {
        if (bearerToken.equals("Bearer validtoken")) {
            // 测试正常情况，从Redis中获取到有效的Session
            Session session = new Session("validtoken", new UserInfo());
            when(redisTemplate.opsForValue().get("session:validtoken")).thenReturn(session);
        } else {
            // 测试特殊的 "test" token情况
            UserInfo user = buildUserInfo("", "", "");
            user.setId(1L);
            when(userInfoRepository.findById(1L)).thenReturn(Optional.of(user));
        }

        try {
            Session result = userService.auth(bearerToken);
            assertNotNull(result);
            if (bearerToken.equals("Bearer validtoken")) {
                assertEquals("validtoken", result.token);
            } else {
                assertEquals("test", result.token);
            }
        } catch (CustomException e) {
            fail("不应该抛出异常");
        }
    }

    @Test
    void shouldThrowExceptionWhenAuthenticateWithInvalidToken() {
        // 测试无效的token，从Redis中获取不到有效的Session
        String bearerToken = "Bearer invalidtoken";
        when(redisTemplate.opsForValue().get("session:invalidtoken")).thenReturn(null);

        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.auth(bearerToken);
        });
        assertEquals("Unauthorized", exception.getMessage());
    }

    @Test
    void shouldResetPasswordSuccessfully() {
        // 模拟userRepository.findByEmail方法返回用户对象
        User user = buildUser("test@example.com", "oldpassword");
        user.setId(1L);
        when(userRepository.findByEmail(any(String.class))).thenReturn(user);

        // 模拟更新密码成功
        try {
            UserService.ResetPasswordRequest request = new UserService.ResetPasswordRequest();
            request.email = "test@example.com";
            request.oldPassword = "oldpassword";
            request.newPassword = "newpassword";
            userService.resetPassword(request);
            assertEquals("newpassword", user.getPassword());
            Mockito.verify(userRepository).save(user);
        } catch (CustomException e) {
            fail("不应该抛出异常");
        }
    }

    @Test
    void shouldThrowExceptionWhenResetPasswordWithUserNotFound() {
        // 模拟userRepository.findByEmail方法返回null，表示用户不存在
        when(userRepository.findByEmail(any(String.class))).thenReturn(null);

        UserService.ResetPasswordRequest request = new UserService.ResetPasswordRequest();
        request.email = "test@example.com";
        request.oldPassword = "oldpassword";
        request.newPassword = "newpassword";

        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.resetPassword(request);
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenResetPasswordWithInvalidOldPassword() {
        // 模拟userRepository.findByEmail方法返回用户对象，但旧密码不匹配
        User user = buildUser("test@example.com", "oldpassword");
        user.setId(1L);
        when(userRepository.findByEmail(any(String.class))).thenReturn(user);

        UserService.ResetPasswordRequest request = new UserService.ResetPasswordRequest();
        request.email = "test@example.com";
        request.oldPassword = "wrongoldpassword";
        request.newPassword = "newpassword";

        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.resetPassword(request);
        });
        assertEquals("Invalid old password", exception.getMessage());
    }

    @Test
    void shouldSaveUserSuccessfully() {
        // 模拟userRepository.save方法返回保存后的用户对象
        User userToSave = buildUser("test@example.com", "password");
        User savedUser = buildUser("test@example.com", "password");
        savedUser.setId(1L);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.saveUser(userToSave);
        assertEquals(1L, result.getId());
    }

    @Test
    void shouldSaveUserInfoSuccessfully() {
        // 模拟userInfoRepository.save方法返回保存后的用户信息对象
        UserInfo userInfoToSave = buildUserInfo("testuser", "test@example.com", "user");
        UserInfo savedUserInfo = buildUserInfo("testuser", "test@example.com", "user");
        savedUserInfo.setId(1L);
        when(userInfoRepository.save(any(UserInfo.class))).thenReturn(savedUserInfo);

        UserInfo result = userService.saveUserInfo(userInfoToSave);
        assertEquals(1L, result.getId());
    }

    @Test
    void shouldFindUserByIdSuccessfully() {
        // 模拟userRepository.findById方法返回用户对象
        User user = buildUser("test@example.com", "password");
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findUserById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void shouldReturnNullWhenFindUserByIdWithUserNotFound() {
        // 模拟userRepository.findById方法返回空Optional，表示用户不存在
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        User result = userService.findUserById(1L);
        assertNull(result);
    }

    @Test
    void shouldFindUserInfoByIdSuccessfully() {
        // 模拟userInfoRepository.findById方法返回用户信息对象
        UserInfo userInfo = buildUserInfo("testuser", "test@example.com", "user");
        userInfo.setId(1L);
        when(userInfoRepository.findById(1L)).thenReturn(Optional.of(userInfo));

        UserInfo result = userService.findUserInfoById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void shouldReturnNullWhenFindUserInfoByIdWithUserInfoNotFound() {
        // 模拟userInfoRepository.findById方法返回空Optional，表示用户信息不存在
        when(userInfoRepository.findById(1L)).thenReturn(Optional.empty());

        UserInfo result = userService.findUserInfoById(1L);
        assertNull(result);
    }
}
package sup.monad.backend.service;

import lombok.Getter;
import lombok.Setter;
import sup.monad.backend.exception.CustomException;
import sup.monad.backend.pojo.Session;
import sup.monad.backend.pojo.User;

public interface IUserService {
    Session signUp(User user) throws CustomException;

    Session signIn(User user) throws CustomException;

    Session auth(String token) throws CustomException;
    Session auth(String token, String role) throws CustomException;

    void resetPassword(ResetPasswordRequest body) throws CustomException;

    @Getter
    @Setter
    public class ResetPasswordRequest {
        public String email;
        public String oldPassword;
        public String newPassword;
    }
}
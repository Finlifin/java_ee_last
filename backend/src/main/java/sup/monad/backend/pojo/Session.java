// all the user sessions will be stored in redis

package sup.monad.backend.pojo;

import java.io.Serializable;

import org.springframework.data.redis.core.RedisHash;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

@Data
@RedisHash
@JsonSerialize
@JsonDeserialize
public class Session implements Serializable {
    public String token;
    public UserInfo userInfo;

    public Session(String token, UserInfo userInfo) {
        this.token = token;
        this.userInfo = userInfo;
    }
}

// all the user sessions will be stored in redis

package sup.monad.backend.pojo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class Session {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void setSession(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public Object getSession(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}

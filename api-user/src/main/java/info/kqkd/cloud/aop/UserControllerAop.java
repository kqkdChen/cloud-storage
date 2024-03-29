package info.kqkd.cloud.aop;

import cn.hutool.core.util.IdUtil;
import info.kqkd.cloud.pojo.User;
import info.kqkd.cloud.utils.RedisUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
@Aspect
public class UserControllerAop {


    @Autowired
    private RedisUtil redisUtil;


    @AfterReturning(returning="map", pointcut="execution(* info.kqkd.cloud.service.impl.UserServiceImpl.*(..)))")
    public Object AfterExec(JoinPoint joinPoint, Map<String, Object> map){
        boolean result = (boolean)map.get("result");
        if (result) {
            // 缓存token和当前用户
            redisUtil.setDataBase(1);
            User currentUser = (User) map.get("user");
            String userAccount = currentUser.getUserAccount();
            // 判断用户是否已经登录，已经登录则清除之前的登录信息
            Set<String> keys = redisUtil.keys(userAccount + "*");
            if (keys != null && keys.size() > 0) {
                keys.forEach(k -> redisUtil.del(k));
            }
            String token = userAccount + "_" + IdUtil.simpleUUID();
            map.put("access_token", token);
            redisUtil.set(token, currentUser, 1800);
        }
        return map;
    }

}

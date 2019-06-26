package info.kqkd.cloud.config;

import cn.hutool.core.util.StrUtil;
import info.kqkd.cloud.pojo.User;
import info.kqkd.cloud.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginInterceptor implements HandlerInterceptor {

//    private RedisTemplate redisTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException, ServletException {
        String access_token = request.getHeader("Authorization");
        // 令牌为空没有登录
        if (StrUtil.isEmpty(access_token)) {
            response.setStatus(401);
            return false;
        }

//        RedisUtil redisUtil = new RedisUtil();
//        redisUtil.setRedisTemplate(redisTemplate);
        redisUtil.setDataBase(1);
        User user  = (User) redisUtil.get(access_token);
        // 当前用户为空登录过期
        if (null == user) {
            response.setStatus(401);
            return false;
        }
        // 更新活跃时间
        redisUtil.expire(access_token, 1800);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

}

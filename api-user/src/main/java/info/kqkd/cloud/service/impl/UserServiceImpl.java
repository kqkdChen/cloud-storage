package info.kqkd.cloud.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import info.kqkd.cloud.pojo.User;
import info.kqkd.cloud.dao.UserMapper;
import info.kqkd.cloud.pojo.UserMulti;
import info.kqkd.cloud.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import info.kqkd.cloud.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 可圈可丶
 * @since 2019-06-12
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Map<String, Object> login(HttpSession session, User user, String verifyCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("result", false);
        String Code = (String) session.getAttribute("verifyCode");
        String randomStr = (String) session.getAttribute("randomStr");
        session.removeAttribute("randomStr");
        session.removeAttribute("verifyCode");
        if (Objects.equals(verifyCode, Code)) {
            if (user == null) {
                map.put("msg", "用户名或者密码错误");
                return map;
            }
            User cur = userMapper.getCurrentUser(user.getUserAccount());
            if (cur != null) {
                String sign = DigestUtil.md5Hex(cur.getUserPassword() + DigestUtil.md5Hex(randomStr));
                if (user.getUserPassword().equals(sign)) {
                    cur.setUserPassword("");  //清除密码
                    map.put("result", true);
                    map.put("user", cur);
                    return map;
                }
                map.put("msg", "非法登录");
                return map;
            }
        }
        map.put("msg", "验证码错误");
        return map;
    }

    @Override
    public User getCurrentUser(String userAccount) {
       return userMapper.getCurrentUser(userAccount);
    }
}

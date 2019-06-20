package info.kqkd.cloud.service;

import info.kqkd.cloud.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;
import info.kqkd.cloud.pojo.UserMulti;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 可圈可丶
 * @since 2019-06-12
 */
public interface IUserService extends IService<User> {


    Map<String, Object> login(HttpSession session, User user, String verifyCode);

    User getCurrentUser(String userAccount);


}

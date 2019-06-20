package info.kqkd.cloud.dao;

import info.kqkd.cloud.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import info.kqkd.cloud.pojo.UserMulti;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 可圈可丶
 * @since 2019-06-17
 */
public interface UserMapper extends BaseMapper<User> {

    User getCurrentUser(String userAccount);

}

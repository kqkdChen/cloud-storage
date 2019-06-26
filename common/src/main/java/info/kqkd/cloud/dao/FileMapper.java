package info.kqkd.cloud.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import info.kqkd.cloud.pojo.File;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import info.kqkd.cloud.pojo.User;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 可圈可丶
 * @since 2019-06-17
 */
public interface FileMapper extends BaseMapper<File> {



    @Select("SELECT f.id, file_name, type, is_complete, is_dir, file_sha1, file_size, file_addr, sm_url, last_modified_date, " +
            "create_date, update_date, `status` FROM t_file f LEFT JOIN t_user_file uf ON f.id = uf.file_id " +
            "WHERE uf.user_id = #{userId} ")
    IPage<File> fileList(Page<File> page, String userId);


}

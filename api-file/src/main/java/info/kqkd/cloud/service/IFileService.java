package info.kqkd.cloud.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import info.kqkd.cloud.pojo.File;
import com.baomidou.mybatisplus.extension.service.IService;
import info.kqkd.cloud.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 可圈可丶
 * @since 2019-06-12
 */
public interface IFileService extends IService<File> {


    /**
     * 获得文件列表，数据库和FastDFS双向查询
     * @return
     */
    IPage<File> fileList(Integer curr, Integer limit, String userId);



}

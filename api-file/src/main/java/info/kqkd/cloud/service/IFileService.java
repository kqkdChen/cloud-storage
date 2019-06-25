package info.kqkd.cloud.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import info.kqkd.cloud.pojo.File;
import com.baomidou.mybatisplus.extension.service.IService;
import org.csource.common.MyException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
     */
    IPage<File> fileList(Integer curr, Integer limit, String userId);


    /**
     * 文件上传
     */
    void fileUpload(String lastModifiedDate, String fileName, Long fileSize, MultipartFile blob) throws IOException, MyException;

    /**
     * 获取文件已上传的大小
     */
    long getUploadSize(String lastModifiedDate, long fileSize) throws IOException, MyException;


}

package info.kqkd.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import info.kqkd.cloud.dao.FileMapper;
import info.kqkd.cloud.dao.UserMapper;
import info.kqkd.cloud.pojo.File;
import info.kqkd.cloud.pojo.User;
import info.kqkd.cloud.service.IFileService;
import info.kqkd.cloud.utils.FastDFSUtil;
import org.csource.common.MyException;
import org.csource.fastdfs.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 可圈可丶
 * @since 2019-06-12
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements IFileService {

    @Autowired
    FileMapper fileMapper;

    @Override
    public IPage<File> fileList(Integer curr, Integer limit, String userId) {
        IPage<File> fileIPage = fileMapper.fileList(new Page<>(curr, limit), userId);
        List<File> files = fileIPage.getRecords();
        Iterator<File> iterator = files.iterator();
        while (iterator.hasNext()) {
            File file = iterator.next();
            try {
                FileInfo fileInfo = new FastDFSUtil().query(file.getFileAddr());
                if (fileInfo == null) {
                    iterator.remove();
                }
            } catch (IOException | MyException e) {
                e.printStackTrace();
            }

        }
        fileIPage.setRecords(files);
        return fileIPage;
    }
}

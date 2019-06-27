package info.kqkd.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import info.kqkd.cloud.dao.FileMapper;
import info.kqkd.cloud.dao.UserFileMapper;
import info.kqkd.cloud.dao.UserMapper;
import info.kqkd.cloud.pojo.File;
import info.kqkd.cloud.pojo.User;
import info.kqkd.cloud.pojo.UserFile;
import info.kqkd.cloud.service.IFileService;
import info.kqkd.cloud.utils.FastDFSUtil;
import info.kqkd.cloud.utils.RedisUtil;
import org.csource.common.MyException;
import org.csource.fastdfs.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    private FileMapper fileMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserFileMapper userFileMapper;

    @Autowired
    private RedisUtil redisUtil;

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

    @Override
    @Transactional
    public long fileUpload(File file, String userId, MultipartFile blob) throws IOException, MyException {
        String fileName = file.getFileName();
        long lastModifiedDate = file.getLastModifiedDate();
        long fileSize = file.getFileSize();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
        String redisFileKey = "file:%d:%d";
        // 当前上传文件的 redis key 设计 file:fileName:lastModifiedDate:fileSize
        String redisFileKeyStr = String.format(redisFileKey, lastModifiedDate, fileSize);
        FastDFSUtil fastDFSUtil = new FastDFSUtil();

        redisUtil.setDataBase(2);
        if (!redisUtil.hasKey(redisFileKeyStr)) {
            String fileId = fastDFSUtil.upload(blob.getSize(), blob.getInputStream(), fileName, extension);
            long currFileSize = fastDFSUtil.query(fileId).getFileSize();
            Map<String, Object> map = new HashMap<>();
            map.put("fileId", fileId);
            map.put("uploadSize", currFileSize);
            System.out.println(redisUtil.getDataBaseIndex());
            redisUtil.hmset(redisFileKeyStr, map);
            redisUtil.exec();
            return currFileSize;
        } else {
            // 有这个文件表示已经已经上传过了
            Map<Object, Object> fileInfo = redisUtil.hmget(redisFileKeyStr);
            String fileId = (String) fileInfo.get("fileId");
            fastDFSUtil.append(fileId, blob.getInputStream());
            FileInfo info = fastDFSUtil.query(fileId);
            long currFileSize = info.getFileSize();
            System.out.println(redisUtil.getDataBaseIndex());
            redisUtil.hset(redisFileKeyStr, "uploadSize", info.getFileSize());
            if (fileSize == currFileSize) {
                System.out.println(redisUtil.getDataBaseIndex());
                redisUtil.del(redisFileKeyStr);
                redisUtil.exec();
                file.setFileAddr(fileId);
                file.setIsComplete(true);
                file.setType(extension);
                fileMapper.insert(file);
                UserFile userFile = new UserFile();
                User user = userMapper.selectById(userId);
                userFile.setUserId(user.getId());
                userFile.setFileId(file.getId());
                userFileMapper.insert(userFile);
                return currFileSize;
            }
            redisUtil.exec();
            return currFileSize;
        }
    }

    @Override
    public long getUploadSize(Long lastModifiedDate, Long fileSize) throws IOException, MyException {
        String redisFileKeyStr = String.format("file:%d:%d", lastModifiedDate, fileSize);
        // 去数据库找，如果有则触发秒传
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("last_modified_date", lastModifiedDate);
        queryWrapper.eq("file_size", fileSize);
        File file = fileMapper.selectOne(queryWrapper);
        if (file != null) {
            return file.getFileSize();
        }
        // 去redis里面找，判断是否有文件缓存
        redisUtil.setDataBase(2);
        Map<Object, Object> fileMap = redisUtil.hmget(redisFileKeyStr);
        if (!fileMap.isEmpty()) {
            return (Integer)fileMap.get("uploadSize");
        }
        return 0;
    }

    @Override
    public boolean saveUploadSize(File currFile, String userId, String uploadSize) {
        return false;
    }

}

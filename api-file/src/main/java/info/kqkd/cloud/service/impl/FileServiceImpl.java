package info.kqkd.cloud.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import info.kqkd.cloud.dao.FileMapper;
import info.kqkd.cloud.pojo.File;
import info.kqkd.cloud.service.IFileService;
import info.kqkd.cloud.utils.FastDFSUtil;
import info.kqkd.cloud.utils.RedisUtil;
import org.csource.common.MyException;
import org.csource.fastdfs.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private RedisUtil redisUtil;

    @Autowired
    private RedisTemplate redisTemplate;

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
    public void fileUpload(String lastModifiedDate, String fileName, Long fileSize, MultipartFile blob) throws IOException, MyException {
        String extension = fileName.substring(fileName.lastIndexOf("."));
        String redisFileKey = "file:%s:%d";
        // 当前上传文件的 redis key 设计 file:fileName:lastModifiedDate:fileSize
        String redisFileKeyStr = String.format(redisFileKey, lastModifiedDate, fileSize);
        redisUtil.getSpecificDB(2, redisTemplate);
        FastDFSUtil fastDFSUtil = new FastDFSUtil();
        if (!redisUtil.hasKey(redisFileKeyStr)) {
            String fileId = fastDFSUtil.upload(blob.getSize(), blob.getInputStream(), fileName, extension.split("\\.")[1]);
            long currFileSize = fastDFSUtil.query(fileId).getFileSize();
            Map<String, Object> map = new HashMap<>();
            map.put("fileId", fileId);
            map.put("uploadSize", currFileSize);
            System.out.println(fileId);
            System.out.println(fileName);
            redisUtil.hmset(redisFileKeyStr, map);
        } else {
            // 有这个文件表示已经已经上传过了
            Map<Object, Object> fileInfo = redisUtil.hmget(redisFileKeyStr);
            String fileId = (String) fileInfo.get("fileId");
            fastDFSUtil.append(fileId, blob.getInputStream());
            FileInfo info = fastDFSUtil.query(fileId);
            long currFileSize = info.getFileSize();
            if (fileSize == currFileSize) {
                redisUtil.del(redisFileKeyStr);
                return;
            }
            redisUtil.hset(redisFileKeyStr, "uploadSize", info.getFileSize());
        }
    }

    @Override
    public long getUploadSize(String lastModifiedDate, long fileSize) throws IOException, MyException {
        redisUtil.getSpecificDB(2, redisTemplate);
        String redisFileKey = "file:%s:%d";
        // 当前上传文件的 redis key 设计 file:lastModifiedDate:fileSize
        String redisFileKeyStr = String.format(redisFileKey, lastModifiedDate, fileSize);
        Map<Object, Object> fileMap = redisUtil.hmget(redisFileKeyStr);
        // 先去redis里面找，判断是否有文件缓存
        if (fileMap.isEmpty()) {
            return 0;
        }
        FileInfo fileInfo = new FastDFSUtil().query((String) fileMap.get("fileId"));
        return fileInfo.getFileSize();
    }

}

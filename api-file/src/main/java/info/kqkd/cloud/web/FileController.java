package info.kqkd.cloud.web;


import info.kqkd.cloud.pojo.File;
import info.kqkd.cloud.pojo.User;
import info.kqkd.cloud.service.IFileService;
import info.kqkd.cloud.utils.FastDFSUtil;
import info.kqkd.cloud.utils.RedisUtil;
import org.csource.common.MyException;
import org.csource.fastdfs.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 可圈可丶
 * @since 2019-06-12
 */
@RestController
public class FileController {

    @Autowired
    private IFileService fileService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisUtil redisUtil;



    @GetMapping("/{id}")
    public File getFileByUser(@PathVariable("id") Integer id) {
        return fileService.getById(id);
    }

    @GetMapping("/getUploadSize")
    public long getUploadSize( String fileName, String lastModifiedDate) throws IOException, MyException {
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.setRedisTemplate(redisTemplate);
        redisUtil.setDataBase(2);
        String redisFileKey = "file:%s:%s";
        String redisFileKeyStr = String.format(redisFileKey, fileName, lastModifiedDate);
        Map<Object, Object> fileMap = redisUtil.hmget(redisFileKeyStr);
        if (fileMap.isEmpty()) {
            System.out.println("文件没有被上传过");
            return 0;
        }
        FileInfo fileInfo = new FastDFSUtil().query((String) fileMap.get("fileId"));
        System.out.println("文件续传");
        return fileInfo.getFileSize();
    }


    @PostMapping("/upload")
    public void upload(String fileName, String lastModifiedDate, @RequestParam("file") MultipartFile file) throws IOException, MyException {
        // 获取拓展名
        String extension = fileName.substring(fileName.lastIndexOf("."));
        String redisFileKey = "file:%s:%s";
        String redisFileKeyStr = String.format(redisFileKey, fileName, lastModifiedDate);
        System.out.println(redisFileKeyStr);
        redisUtil.setRedisTemplate(redisTemplate);
        redisUtil.setDataBase(2);
        FastDFSUtil fastDFSUtil = new FastDFSUtil();
        if (!redisUtil.hasKey(redisFileKeyStr)) {
            String fileId = fastDFSUtil.upload(file.getSize(), file.getInputStream(), fileName, extension.split("\\.")[1]);
            long fileSize = fastDFSUtil.query(fileId).getFileSize();
            System.out.println(fileId);
            Map<String, Object> map = new HashMap<>();
            map.put("fileId", fileId);
            map.put("uploadSize", fileSize);
            redisUtil.hmset(redisFileKeyStr, map);
        } else {
            // 有这个文件表示已经已经上传过了
            Map<Object, Object> fileInfo = redisUtil.hmget(redisFileKeyStr);
            String fileId = (String) fileInfo.get("fileId");
            fastDFSUtil.append(fileId, file.getInputStream());
            FileInfo info = fastDFSUtil.query(fileId);
            System.out.println("追加后的大小为" + info.getFileSize());
            redisUtil.hset(redisFileKeyStr, "uploadSize", info.getFileSize());

        }
    }

    @GetMapping("/download")
    public void download(HttpServletResponse response, String fileId, String realFileName) throws IOException, MyException {
        System.out.println(fileId + realFileName);
        FastDFSUtil.download(response, fileId, realFileName);
    }


    /**
     * 查询文件列表
     * @param curr
     * @param limit
     * @return
     */
    @GetMapping("/list")
    public Map<String, Object> fileList(HttpServletRequest request, @RequestParam(value = "curr", defaultValue = "0") Integer curr,
                                        @RequestParam(value = "limit", defaultValue = "50") Integer limit) {
        String authorization = request.getHeader("Authorization");
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.setRedisTemplate(redisTemplate);
        redisUtil.setDataBase(1);
        User user = (User) redisUtil.get(authorization);
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("files", fileService.fileList(curr, limit, user.getId()));
        return map;
    }


    @DeleteMapping("/delete")
    public Map<String, Object> delete(@RequestParam("ids") String[] ids) {
        fileService.removeByIds(Arrays.asList(ids));
        return null;
    }



}

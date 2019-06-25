package info.kqkd.cloud.web;


import info.kqkd.cloud.pojo.File;
import info.kqkd.cloud.pojo.User;
import info.kqkd.cloud.service.IFileService;
import info.kqkd.cloud.utils.FastDFSUtil;
import info.kqkd.cloud.utils.RedisUtil;
import org.csource.common.MyException;
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
import java.util.UUID;

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
    public Integer getUploadSize(String fileSHA1) {
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.setRedisTemplate(redisTemplate);
        redisUtil.setDataBase(2);
        Integer uploadSize = (Integer) redisUtil.get(fileSHA1);
        return uploadSize;
    }


    @PostMapping("/upload")
    public void upload(String fileName, @RequestParam("file") MultipartFile file) throws IOException, MyException {
        // 获取拓展名
        String extension = fileName.substring(fileName.lastIndexOf("."));
        // 重命名文件
        String newFileName = UUID.randomUUID() + extension;
        String redisFileKey = "file:%s:%s";  // 模块名:原来文件命:uuid文件名
        String redisFileKeyStr = String.format(redisFileKey, fileName, newFileName);
        System.out.println(redisFileKeyStr);
        redisUtil.setRedisTemplate(redisTemplate);
        redisUtil.setDataBase(2);
        FastDFSUtil fastDFSUtil = new FastDFSUtil();
        if (!redisUtil.hasKey(fileName)) {
            System.out.println("文件第一次上传");
            String fileId = fastDFSUtil.upload(file.getSize(), file.getInputStream(), fileName, extension.split("\\.")[1]);
            System.out.println(fileId);
            redisUtil.set(redisFileKeyStr, fileId);
        } else {
            // 有这个文件表示已经已经上传过了
            System.out.println("文件追加开始了");
            String fileId = (String) redisUtil.get(fileName);
            new FastDFSUtil().append(fileId, file.getInputStream());
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

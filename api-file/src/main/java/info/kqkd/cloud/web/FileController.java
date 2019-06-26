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
    private RedisUtil redisUtil;

    @GetMapping("/{id}")
    public File getFileByUser(@PathVariable("id") Integer id) {
        return fileService.getById(id);
    }


    /**
     * @param lastModifiedDate 最后修改时间
     * @param fileSize 文件大小
     * @return 返回文件已经上传的大小
     */
    @GetMapping("/getUploadSize")
    public long getUploadSize(Long lastModifiedDate, Long fileSize) throws IOException, MyException {
        return fileService.getUploadSize(lastModifiedDate, fileSize);
    }


    /**
     * 文件上传
     * @param blob 文件对象
     */
    @PostMapping("/upload")
    public void upload(File currFile, String token,
                       @RequestParam("file") MultipartFile blob) throws IOException, MyException {
        fileService.fileUpload(currFile, token, blob);
    }

    /**
     *
     * @param response 响应流
     * @param fileId fastdfs文件id
     * @param realFileName  文件原名称
     */
    @GetMapping("/download")
    public void download(HttpServletResponse response, String fileId, String realFileName) throws IOException, MyException {
        FastDFSUtil.download(response, fileId, realFileName);
    }


    /**
     * 查询文件列表
     * @param curr 当前页
     * @param limit 每页显示数据量
     * @return 数据信息和状态码
     */
    @GetMapping("/list")
    public Map<String, Object> fileList(HttpServletRequest request, @RequestParam(value = "curr", defaultValue = "0") Integer curr,
                                        @RequestParam(value = "limit", defaultValue = "50") Integer limit) {
        String authorization = request.getHeader("Authorization");
        redisUtil.setDataBase(1);
        User user = (User) redisUtil.get(authorization);
        Map<String, Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("files", fileService.fileList(curr, limit, user.getId()));
        return map;
    }


    /**
     *
     * @param ids 要删除的文件id
     * @return
     */
    @DeleteMapping("/delete")
    public Map<String, Object> delete(@RequestParam("ids") String[] ids) {
        fileService.removeByIds(Arrays.asList(ids));
        return null;
    }

}

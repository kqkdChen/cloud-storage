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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.util.Arrays;
import java.util.Date;
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


    @GetMapping("/{id}")
    public File getFileByUser(@PathVariable("id") Integer id) {
        return fileService.getById(id);
    }

    @PostMapping("/upload")
    public Map<String, Object> upload(HttpServletRequest request, @RequestParam("file") MultipartFile file) throws IOException, ServletException {
        String fileName = System.currentTimeMillis()+file.getOriginalFilename();
        System.out.println(fileName);
        Part file1 = request.getPart("file");
//        InputStream inputStream = file1.getInputStream();
       /* BufferedInputStream bis = new BufferedInputStream(inputStream);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream("/media/kqkd/software/Projects/cloud-storage/upload/1560410729735课件.zip", true));
        byte[] bytes = new byte[1024 * 1024 * 2];
        int len;
        while ((len = bis.read(bytes, 0, bytes.length)) != -1) {
            bufferedOutputStream.write(bytes, 0, len);
            bufferedOutputStream.flush();
        }*/
        String realPath = System.getProperty("user.dir") + "/upload" + java.io.File.separator + fileName;
        java.io.File destFile = new java.io.File (realPath);
        destFile.getParentFile().mkdirs();
        Map<String, Object> map = new HashMap<>();
        try {
            file.transferTo(destFile);
            map.put("fileName", fileName);
            map.put("lastModifiedDate", new Date());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
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

package info.kqkd.cloud.utils;

import cn.hutool.core.io.IoUtil;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class FastDFSUtil {


    @Value("${fast-properties}")
    private static String fastProperties = "fastdfs-client.properties";

    static {
        try {
            //加载fastDFS客户端的配置 文件
            ClientGlobal.initByProperties(fastProperties);
        } catch (IOException | MyException e) {
            e.printStackTrace();
        }
    }


    public String upload(String realPath, String fileName, String extension) throws IOException, MyException {
        //创建tracker的客户端
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = tracker.getConnection();
        StorageServer storageServer = null;
        //定义storage的客户端
        StorageClient1 client = new StorageClient1(trackerServer, storageServer);
        //文件元信息
        NameValuePair[] metaList = new NameValuePair[1];
        metaList[0] = new NameValuePair("fileName", fileName);
        //执行上传，将上传成功的存放在web服务器（本机）上的文件上传到 fastDFS
        String fileId = client.upload_appender_file1(realPath, extension, metaList);
        trackerServer.close();
        return fileId;
    }

    public String upload2(String realPath, MultipartFile file,  String fileName, String extension) throws IOException, MyException {
        //创建tracker的客户端
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = tracker.getConnection();
        StorageServer storageServer = null;
        //定义storage的客户端
        StorageClient1 client = new StorageClient1(trackerServer, storageServer);
        //文件元信息
        NameValuePair[] metaList = new NameValuePair[1];
        metaList[0] = new NameValuePair("fileName", fileName);
        //执行上传，将上传成功的存放在web服务器（本机）上的文件上传到 fastDFS
        String fileId = client.upload_appender_file1(null, file.getSize(),
                new UploadFileSender(file.getInputStream()), extension, metaList);
        trackerServer.close();
        return fileId;
    }


    private static class UploadFileSender implements UploadCallback {
        private InputStream inStream;

        public UploadFileSender(InputStream inStream) {
            this.inStream = inStream;
        }

        public int send(OutputStream out) throws IOException {
            if (inStream instanceof FileInputStream) {
                inStream = new BufferedInputStream(inStream);
            }
            byte[] buff = new byte[4096];
            int off;
            while ((off = inStream.read(buff)) != -1) {
                out.write(buff, 0, off);
            }
            out.flush();
            inStream.close();
            return 0;
        }
    }



    /**
     *  FDFS 文件下载类
     * @param response 响应
     * @param fileAddr 文件路径
     * @throws IOException
     * @throws MyException
     */
    public static void download(HttpServletResponse response, String fileAddr, String realFileName) throws IOException, MyException {
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = tracker.getConnection();
        StorageServer storageServer = null;
        StorageClient1 client = new StorageClient1(trackerServer, storageServer);
        response.setContentType("application/force-download");
        response.setHeader("Content-Disposition", "attachment;filename=" + realFileName);
        client.download_file1(fileAddr, new MyDownloadCallBack(response.getOutputStream()));
        trackerServer.close();
    }

    public static FileInfo query(String fileAddr) throws IOException, MyException {
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = tracker.getConnection();
        StorageServer storageServer = null;
        String[] filePath = fileAddr.split(File.separator, 2);
        StorageClient1 client = new StorageClient1(trackerServer, storageServer);
        FileInfo fileInfo = client.query_file_info(filePath[0], filePath[1]);
        trackerServer.close();
        return fileInfo;

    }

    public void append(String appendFileId, String localFilePath) throws IOException, MyException {
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = tracker.getConnection();
        StorageServer storageServer = null;
        StorageClient1 client = new StorageClient1(trackerServer, storageServer);
        int i = client.append_file1(appendFileId, localFilePath);
        System.out.println(i);
//        byte[] buff = new byte[1024 * 1024 * 2];
//        int len;
//        while ((len = inputStream.read(buff, 0, buff.length)) != -1) {
//            int i = client.append_file1(appendFileId, buff, 0, len);
//        }
        trackerServer.close();
    }


    public static void main(String[] args) throws IOException, MyException {
//        System.out.println(upload("/home/kqkd/Desktop/尚硅谷SpringBoot整合篇/课件.zip"));
//        query("group1/M00/00/00/wKgCGF0C30aAcgCqBDiRuhPK9jQ245.pdf");
//        FileInputStream fileInputStream = new FileInputStream("/home/kqkd/Desktop/Java程序性能优化  让你的Java程序更快、更稳定.pdf");
//        append(new FileInputStream("/home/kqkd/Desktop/vip会员视频清单.txt"), "group1/M00/00/00/wKgCI10C7XiEflqlAAAAAAgQJ_A664.zip");

//        System.out.println(upload("vip会员视频清单.txt",));
    }


}

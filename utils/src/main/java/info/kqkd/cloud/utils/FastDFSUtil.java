package info.kqkd.cloud.utils;

import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class FastDFSUtil {


    @Value("${fast-properties}")
    private static String fastProperties = "fastdfs-client.properties";

    static {
        try {
            ClientGlobal.initByProperties(fastProperties);
        } catch (IOException | MyException e) {
            e.printStackTrace();
        }

    }

    /**
     *  FDFS 文件上传类
     * @param fileName
     * @return
     * @throws IOException
     * @throws MyException
     */
    public static String upload(String fileName) throws IOException, MyException {
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = tracker.getConnection();
        StorageServer storageServer = null;
        StorageClient1 client = new StorageClient1(trackerServer, storageServer);
        NameValuePair[] metaList = new NameValuePair[1];
        metaList[0] = new NameValuePair("fileName", fileName);
        String fileId = client.upload_appender_file1(fileName, null, metaList);
        trackerServer.close();
        return fileId;
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

    public static void append(InputStream inputStream, String appendFIleAddr) throws IOException, MyException {
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = tracker.getConnection();
        StorageServer storageServer = null;
        StorageClient1 client = new StorageClient1(trackerServer, storageServer);
        byte[] buff = new byte[1024 * 1024 * 2];
        int len;
        while ((len = inputStream.read(buff, 0, buff.length)) != -1) {
            int i = client.append_file1(appendFIleAddr, buff, 0, len);
            System.out.println(i);
        }
        trackerServer.close();
    }


    public static void main(String[] args) throws IOException, MyException {
//        System.out.println(upload("/home/kqkd/Desktop/尚硅谷SpringBoot整合篇/课件.zip"));
//        query("group1/M00/00/00/wKgCGF0C30aAcgCqBDiRuhPK9jQ245.pdf");
//        FileInputStream fileInputStream = new FileInputStream("/home/kqkd/Desktop/Java程序性能优化  让你的Java程序更快、更稳定.pdf");
//        append(fileInputStream, "group1/M00/00/00/wKgCI10C7XiEflqlAAAAAAgQJ_A664.zip");
        System.out.println(upload("/home/kqkd/Desktop/尚硅谷SpringBoot整合篇/视频/2、尚硅谷-SpringBoot高级-缓存-Spring缓存抽象简介.avi"));
    }


}

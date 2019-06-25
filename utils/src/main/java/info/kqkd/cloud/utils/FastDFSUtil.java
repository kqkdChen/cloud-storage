package info.kqkd.cloud.utils;

import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Value;

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


    public String upload(long fileSize, InputStream inputStream, String fileName, String extension) throws IOException, MyException {
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
        String fileId = client.upload_appender_file1(null, fileSize,
                new UploadFileSender(inputStream), extension, metaList);
        trackerServer.close();
        return fileId;
    }


    private static class UploadFileSender implements UploadCallback {
        private InputStream in;

        public UploadFileSender(InputStream in) {
            this.in = in;
        }

        public int send(OutputStream out) throws IOException {
            try {
                BufferedOutputStream bufferOut = new BufferedOutputStream(out);
                if (in instanceof FileInputStream) {
                    in = new BufferedInputStream(in);
                }
                byte[] buff = new byte[4096];
                int len;
                while ((len = in.read(buff, 0, buff.length)) != -1) {
                    bufferOut.write(buff, 0, len);
                }
                bufferOut.flush();
            } catch (Exception e) {
                throw e;
            } finally {
                in.close();
            }
            return 0;
        }
    }



    /**
     *  FDFS 文件下载类
     * @param response 响应
     * @param fileAddr 文件路径
     */
    public static void download(HttpServletResponse response, String fileAddr, String realFileName) throws IOException, MyException {
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = tracker.getConnection();
        StorageServer storageServer = null;
        StorageClient1 client = new StorageClient1(trackerServer, storageServer);
//        response.setContentType("application/force-download");
        response.setHeader("Content-Disposition", "attachment;filename=" + realFileName);
        client.download_file1(fileAddr, new MyDownloadCallBack(response.getOutputStream()));
        trackerServer.close();
    }

    public FileInfo query(String fileAddr) throws IOException, MyException {
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = tracker.getConnection();
        StorageServer storageServer = null;
        StorageClient1 client = new StorageClient1(trackerServer, storageServer);
        FileInfo fileInfo = client.query_file_info1(fileAddr);
        trackerServer.close();
        return fileInfo;

    }

    /**
     * 文件追加
     * @param appendFileId
     * @param inputStream
     * @throws IOException
     * @throws MyException
     */
    public void append(String appendFileId, InputStream inputStream) throws IOException, MyException {
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = tracker.getConnection();
        StorageServer storageServer = null;
        StorageClient1 client = new StorageClient1(trackerServer, storageServer);
        byte[] buff = new byte[1024 * 1024 * 2];
        int len;
        while ((len = inputStream.read(buff, 0, buff.length)) != -1) {
            client.append_file1(appendFileId, buff, 0, len);
        }
        trackerServer.close();
    }

}

package info.kqkd.cloud;

import cn.hutool.crypto.digest.DigestUtil;
import org.csource.common.MyException;
import org.csource.fastdfs.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Test {

    public static void main(String[] args) throws IOException, MyException {
//        String conf_filename = "fastdfs-client.properties";
//        String local_filename = "/home/kqkd/Desktop/FastDFS入门.md";
//        ClientGlobal.initByProperties(conf_filename);
//        System.out.println("network_timeout=" + ClientGlobal.g_network_timeout + "ms");
//        System.out.println("charset=" + ClientGlobal.g_charset);
//        TrackerClient tracker = new TrackerClient();
//        TrackerServer trackerServer = tracker.getConnection();
//        StorageServer storageServer = null;
//        StorageClient1 client = new StorageClient1(trackerServer, storageServer);
//        byte[] bytes = client.download_file("group1", "M00/00/00/wKgCI10CNz6AE7nrAAAkBTfwv8s7725.md");
//        FileOutputStream outputStream = new FileOutputStream("/home/kqkd/Desktop/haha.md");
//        outputStream.write(bytes);

//        System.out.println(DigestUtil.md5Hex(DigestUtil.md5Hex("m4mv05wi37") + DigestUtil.md5Hex("123")));
//        System.out.println(DigestUtil.md5Hex("123"));
        FileInputStream inputStream = new FileInputStream("/home/kqkd/Desktop/vip会员视频清单.txt");
        System.out.println(DigestUtil.sha1Hex(inputStream));


//        NameValuePair[] metaList = new NameValuePair[1];
//        metaList[0] = new NameValuePair("fileName", local_filename);
//        String fileId = client.upload_file1(local_filename, "md", metaList);
//        System.out.println("upload success. file id is: " + fileId);
    }

}

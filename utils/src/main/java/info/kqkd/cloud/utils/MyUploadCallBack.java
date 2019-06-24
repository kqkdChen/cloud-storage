package info.kqkd.cloud.utils;

import org.csource.fastdfs.DownloadCallback;
import org.csource.fastdfs.UploadCallback;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MyUploadCallBack implements UploadCallback {
    private InputStream inStream;

    public MyUploadCallBack(InputStream inStream) {
        this.inStream = inStream;
    }

    public int send(OutputStream out) throws IOException {
        int readBytes;
        byte[] buff = new byte[1024 * 1024 * 2];
        while((readBytes = inStream.read(buff, 0, buff.length)) != -1) {
            out.write(buff, 0, readBytes);
        }
        return 0;
    }
}



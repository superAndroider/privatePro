package com.ipeercloud.com.httpd;

import com.ipeercloud.com.controler.GsSocketManager;
import com.ipeercloud.com.utils.GsFile;
import com.ipeercloud.com.utils.GsLog;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import fi.iki.elonen.NanoHTTPD;

/**
 * @author 673391138@qq.com
 * @since 17/4/30
 * 主要功能:
 */

public class GsHttpd extends NanoHTTPD {
    private static final int BUF_SIZE = 1024 * 1024;
    private String fileName = "hkc.mp4";
    public static String sRemotePath;
    public GsHttpd(int port) {
        super(8080);
    }

    @Override
    public Response serve(IHTTPSession session) {
//        String range = session.getHeaders().get("range");
//        GsLog.d("请求range " + range);
//        Response response = new Response(NanoHTTPD.Response.Status.PARTIAL_CONTENT, "text/html", getStream());
//        response.addHeader("Content-Length", "" + BUF_SIZE);
//        int start = (time - 1) * BUF_SIZE;
//        response.addHeader("Content-Range", "bytes " + start + "-" + ((time) * BUF_SIZE - 1) + "/" + getSize());
        GsLog.d("url "+session.getUri());
        return new Response(Response.Status.OK,"text/html",new ByteArrayInputStream(getFromJni(sRemotePath)));
    }

    int index = -1;
    static int time = 0;

    private byte[] getFromJni(String remotePath) {
        int size = 1024;
        byte[] buf = new byte[size];
        int[] leng = new int[]{};
        GsLog.d("远端路径 "+remotePath);
        boolean result = GsSocketManager.getInstance().gsReadFileBuffer(remotePath, 0, size, buf, leng);
        if (result) {
            GsLog.d("请求数据流成功  "+buf.length);

            return buf;
        }
        GsLog.d("请求数据失败");
        return null;
    }

    private long getSize() {
        File file = new File(GsFile.getDir(), fileName);
        if (!file.exists()) {
            GsLog.d("服务端文件不存在");
            return 0;
        }
        return file.length();
    }

    private InputStream getStream() {
        File file = new File(GsFile.getDir(), fileName);
        if (!file.exists()) {
            GsLog.d("服务端文件不存在");
            return null;
        }
        try {
            GsLog.d("文件的大小  " + file.length());
            FileInputStream fis = new FileInputStream(file);
            byte[] buf = new byte[1024 * 1024];
            int offset = BUF_SIZE * time;
            if (offset != 0) {
                offset = offset + 1;
            }
            //读掉无效数据
            byte[] gcBuf = new byte[offset];
            fis.read(gcBuf);
            //读有用数据
            fis.read(buf, 0, BUF_SIZE);
            ByteArrayInputStream bis = new ByteArrayInputStream(buf);
            time++;
            return bis;
        } catch (Exception e) {
            GsLog.d("出现了异常");
            e.printStackTrace();
        }
        return null;
    }
}

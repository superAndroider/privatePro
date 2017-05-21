package com.ipeercloud.com.httpd;

import com.ipeercloud.com.controler.GsSocketManager;
import com.ipeercloud.com.utils.GsLog;

import java.io.ByteArrayInputStream;

import fi.iki.elonen.NanoHTTPD;

/**
 * @author 673391138@qq.com
 * @since 17/4/30
 * 主要功能:
 */

public class GsHttpd extends NanoHTTPD {
    public static int fileSize = 0;
    public static int bufSize = 1024 * 1024;
    public static String sRemotePath;

    public GsHttpd(int port) {
        super(8080);
    }
    @Override
    public Response serve(IHTTPSession session) {
        if (fileSize < 5 * 1024 * 1024) {
            bufSize = 1024 * 1024;
        } else {
            bufSize = 1024 * 1024;
        }
        String range = session.getHeaders().get("range");
        GsLog.d("\n ================================ \n 请求range " + range);
        int start = getStart(range);
        byte[] buf = getFromJni(start, sRemotePath);
        Response response = new Response(NanoHTTPD.Response.Status.PARTIAL_CONTENT, "text/html", new ByteArrayInputStream(buf));
        response.addHeader("Content-Length", "" + bufSize);
        GsLog.d(" Content-Range response:  " + "bytes " + start + "-" + (start + bufSize) + "/" + fileSize);
        response.addHeader("Content-Range", "bytes " + start + "-" + (start + bufSize) + "/" + fileSize);
        return response;
    }


    private synchronized byte[] getFromJni(int start, String remotePath) {
        byte[] buf = new byte[bufSize + 2];
        int[] leng = new int[5];
        GsLog.d("远端路径 " + remotePath);
        leng[0] = bufSize;//要读取的字节数要先保存到这里
        int result = GsSocketManager.getInstance().gsReadFileBuffer(remotePath, start, bufSize, buf, leng);
        if (result == 0) {
            GsLog.d("请求数据流成功  " + buf.length);
            return buf;
        }
        GsLog.d("请求数据失败");
        return new byte[]{0};
    }

    private int getStart(String range) {
        int index = range.indexOf("-");
        String num = range.substring(6, index);
        return Integer.parseInt(num);
    }


}

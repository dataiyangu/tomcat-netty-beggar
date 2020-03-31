package com.leesin.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.io.UnsupportedEncodingException;

/**
 * @description:
 * @author: Leesin.Dong
 * @date: Created in 2020/3/31 16:33
 * @version: ${VERSION}
 * @modified By:
 */
public class Response {
    //SocketChannel的封装
    private ChannelHandlerContext chx;

    private HttpRequest req;

    public Response(ChannelHandlerContext chx, HttpRequest req) {
        this.chx = chx;
        this.req = req;
    }

    public void write(String s) throws UnsupportedEncodingException {
        try {
            if (s == null || s.length() == 0) {
                return;
            }
            //设置Http协议及请求头信息
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(
                    //设置http版本为1.1
                    HttpVersion.HTTP_1_1,
                    //设置响应状态码
                    HttpResponseStatus.OK,
                    //将输出值写出，编码为UTF-8
                    Unpooled.wrappedBuffer(s.getBytes("UTF-8")));

            response.headers().set("Content-Type", "text/html");

            chx.write(response);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            chx.flush();
            chx.close();
        }
    }
}

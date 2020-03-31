package com.leesin.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: Leesin.Dong
 * @date: Created in 2020/3/31 16:33
 * @version: ${VERSION}
 * @modified By:
 */
public class Request {
    private ChannelHandlerContext ctx;
    private HttpRequest request;

    public Request(ChannelHandlerContext ctx, HttpRequest request) {
        this.ctx = ctx;
        this.request = request;
    }

    public String getUri() {
        return request.uri();
    }

    public String getMethod() {
        return request.method().name();
    }

    public Map<String, List<String>> getParameters() {
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
        Map<String, List<String>> parameters = queryStringDecoder.parameters();
        return parameters;
    }
}

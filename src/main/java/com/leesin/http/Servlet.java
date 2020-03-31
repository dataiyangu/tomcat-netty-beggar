package com.leesin.http;


/**
 * @description:
 * @author: Leesin.Dong
 * @date: Created in 2020/3/31 16:24
 * @version: ${VERSION}
 * @modified By:
 */
public abstract class Servlet {

    public void service(Request requst, Response response) throws Exception {
        if (("GET").equalsIgnoreCase(requst.getMethod())) {
            doGet(requst,response);
        } else {
            doPost(requst,response);
        }
    }

    public abstract void doGet(Request request , Response response) throws Exception;

    public abstract void doPost(Request request , Response response) throws Exception;
}

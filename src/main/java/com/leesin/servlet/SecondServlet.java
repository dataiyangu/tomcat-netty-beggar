package com.leesin.servlet;

import com.leesin.http.Request;
import com.leesin.http.Response;
import com.leesin.http.Servlet;


/**
 * @description:
 * @author: Leesin.Dong
 * @date: Created in 2020/3/31 16:24
 * @version: ${VERSION}
 * @modified By:
 */
public class SecondServlet extends Servlet {


    @Override
    public void doGet(Request request,Response response) throws Exception {
        this.doPost(request, response);

    }

    @Override
    public void doPost(Request request, Response response) throws Exception {
        response.write("this is second Servlet");
    }

}

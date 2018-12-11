package com.jfinalshop.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.jfinalshop.controller.member.RegisterController;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet(name = "apiPortal")
public class apiPortal extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BufferedReader reader = request.getReader();
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        JSONObject req = JSONObject.parseObject(stringBuilder.toString());
        String account = req.getString("account");
        String password = req.getString("password");

        RegisterController reg = new RegisterController();
        Object o = reg.registerUser(account,password);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=utf-8");
        response.getWriter().write(JSONObject.toJSONString(o));
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}

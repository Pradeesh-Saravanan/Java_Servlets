package com.service;

import java.io.IOException;
import java.net.CookieStore;
import java.net.http.HttpRequest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
//import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * Servlet Filter implementation class Lazylogin
 */
//@WebFilter("/Lazylogin")
public class Lazylogin extends HttpFilter implements Filter {
	private static final long serialVersionUID = 6593125883047910116L;

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;
        httpResponse.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:5500");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        try {
        	Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch(ClassNotFoundException e) {
        	
        }
        Cookie cks[] = httpRequest.getCookies();
        boolean flag = false;
        if(cks!=null) {
        for(Cookie ck:cks) {
        	try {
	        	Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo","root","password");
	        	PreparedStatement preparedStatement = connection.prepareStatement("select * from userLogin where id = ?");
	        	preparedStatement.setString(1, ck.getValue());
	        	ResultSet rSet = preparedStatement.executeQuery();
	        	while(rSet.next()) {
	        		if(rSet.getString("value").equals("true")) {
	        			flag = true;
	        			Map<String,String> map = new HashMap<>();
	                	Gson gson = new Gson();
	                	httpResponse.setStatus(HttpServletResponse.SC_OK);
	                	httpResponse.setContentType("application/json");
	        			map.put("status", "success");
	        			map.put("message", "Authenticated");
	        			map.put("user", rSet.getString("username"));
	        			httpResponse.getWriter().println(gson.toJson(map));
	        			httpResponse.getWriter().flush();
	        			System.out.println("Authenticated at lazy login filter....");
	        		}
	        	}
	        	
        	}
        	catch(SQLException e) {
				
			}
        	
        }
        }	
        else {
        	chain.doFilter(httpRequest, httpResponse);
        	response.getWriter().println("No cookies ");
        	System.out.println("No cookies in lazy login filter...");
        	return;
        }
        if(!flag) {
        	Map<String,String> map = new HashMap<>();
        	Gson gson = new Gson();
        	System.out.println("Authentication failed at lazy login");
        	httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        	httpResponse.setContentType("application/json");
			map.put("status", "failed");
			map.put("message", "Unauthorized Access");
			httpResponse.getWriter().println(gson.toJson(map));
			httpResponse.getWriter().flush();
        	return;
        }
	}


}

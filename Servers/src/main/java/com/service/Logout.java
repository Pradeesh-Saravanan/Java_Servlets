package com.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;


public class Logout extends HttpServlet{
	private static final long serialVersionUID = 7497259291823217852L;
	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:5500");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "GET,POST");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		response.setStatus(HttpServletResponse.SC_OK);
	}
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:5500");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		
		}
		catch(ClassNotFoundException e) {
			
		}
		Cookie[] cks = request.getCookies();
		boolean flag = true;
		if(cks!=null) {
			for(Cookie ck:cks) {
				try {
					Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo","root","password");
					PreparedStatement stmtPreparedStatement = connection.prepareStatement("update userLogin set value = 'false' where id = ?");
					stmtPreparedStatement.setString(1,ck.getValue());
					int rows = stmtPreparedStatement.executeUpdate();
					if(rows>0) {
						Map<String,String> map = new HashMap<>();
	                	Gson gson = new Gson();
	                	response.setStatus(HttpServletResponse.SC_OK);
	                	response.setContentType("application/json");
	        			map.put("status", "success");
	        			map.put("message", "user Logout");
	        			response.getWriter().println(gson.toJson(map));
	        			response.getWriter().flush();
	        			System.out.println("Logout successful...");
	        			break;
					}
					else {
						Map<String,String> map = new HashMap<>();
	                	Gson gson = new Gson();
	                	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	                	response.setContentType("application/json");
	        			map.put("status", "failed");
	        			map.put("message", "Unauthorized Access");
	        			response.getWriter().println(gson.toJson(map));
	        			response.getWriter().flush();
	        			System.out.println("Logout failed!");
					}
				}
				catch(SQLException|IOException  e) {
					
				}
			}
		}
		else {
			Map<String,String> map = new HashMap<>();
        	Gson gson = new Gson();
        	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        	response.setContentType("application/json");
			map.put("status", "failed");
			map.put("message", "Unauthorized Access");
			try {
				response.getWriter().println(gson.toJson(map));
				response.getWriter().flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Logout failed!");
			System.out.println("Cookies not found in logout servlet");
		}
		
	}
}

package com.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mindrot.jbcrypt.*;

import com.google.gson.*;	

public class Registration extends HttpServlet {
	private static final long serialVersionUID = -5926468460905343227L;
	@Override
	public void init() throws ServletException{
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		}
		catch(ClassNotFoundException e) {
			throw new ServletException("Mysql driver not found");
		}
	}
	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException{
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods","POST,OPTIONS");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type, access-control-allow-methods");
		response.setStatus(HttpServletResponse.SC_OK);
	}
	private Connection getConnection() throws SQLException, ClassNotFoundException{
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/demo","root","password");
	}
	@Override
	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		response.setHeader("Access-Control-Allow-Origin","*");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type,Authorization,access-control-allow-methods");
		response.setHeader("Access-Control-Allow-Methods", "POST,OPTIONS");
		if("OPTIONS".equals(request.getMethod()))
		{
			response.setStatus(HttpServletResponse.SC_OK);
			return;
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line ;
		while((line=reader.readLine())!=null) {
			sb.append(line);
		}
		Gson gson = new Gson();
		User user = gson.fromJson(sb.toString(),User.class);
		Map<String,String> map = new HashMap<>();
		try(Connection conn = getConnection()){
			String query = "select username from userLogin where username = ?";
			try(PreparedStatement stmt = conn.prepareStatement(query)){
				stmt.setString(1, user.getUsername());
				ResultSet rs = stmt.executeQuery();
				if(rs.next()) {
					response.setContentType("application/json");
					map.put("status", "failed");
					map.put("message", "Username already exists");
					response.getWriter().println(gson.toJson(map));
					response.getWriter().flush();
				}
				else {
					String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
					query = "insert into userLogin(username,password) values(?,?)";
					try(PreparedStatement insert =conn.prepareStatement(query)){
						insert.setString(1, user.getUsername());
						insert.setString(2, hashed);
						insert.executeUpdate();
						response.setContentType("application/json");
						map.put("status", "success");
						map.put("message","User added to database");
						response.getWriter().println(gson.toJson(map));
						response.getWriter().flush();
					}
					catch(SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		catch(SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	public class User{
		private String username;
		private String password;
		public User(String username,String password) {
			this.username = username.trim();
			this.password = password.trim();
		}
		public String getUsername() {
			return this.username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getPassword() {
			return this.password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
	}
}

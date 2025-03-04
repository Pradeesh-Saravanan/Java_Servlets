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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;

import com.google.gson.Gson;

public class Login extends HttpServlet {
	private static final long serialVersionUID = 9001827459316047966L;

	@Override
	public void init() throws ServletException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			throw new ServletException("MySQL Driver not found", e);
		}
	}

	private Connection getConnection() throws SQLException, ClassNotFoundException {
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/demo", "root", "password");
	}

	public void setCORS(HttpServletRequest request, HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:5500");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "GET,POST");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
	}

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
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:5500");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.getWriter().println("Login servlet is running");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:5500");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		System.out.println("Login servlet is running...");
		if ("OPTIONS".equals(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_OK);
			return;
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line = "";
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		String json = sb.toString();
		Gson gson = new Gson();
		User user = gson.fromJson(json, User.class);

		try (Connection conn = getConnection()) {
			String query = "select password from userLogin where username = ?";
			try (PreparedStatement stmt = conn.prepareStatement(query)) {
				stmt.setString(1, user.getUsername());
				ResultSet rs = stmt.executeQuery();
				Map<String, String> map = new HashMap<>();
				if (rs.next()) {
					if (BCrypt.checkpw(user.getPassword(), rs.getString("password"))) {
						Cookie ck = new Cookie("user", user.getUsername());
						ck.setPath("/");
						ck.setMaxAge(60 * 60 * 24);  
						response.addCookie(ck);
						response.setStatus(HttpServletResponse.SC_OK);
						response.setHeader("Set-Cookie", "user="+user.getUsername()+ ";Max-Age=86400; Path=/; SameSite=None;Secure=true;");
						System.out.println("Cookie set in Login...");
						response.setContentType("application/json");
						map.put("status", "success");
						map.put("message", "login successful");
						response.getWriter().println(gson.toJson(map));
						response.getWriter().flush();
						
					} else {
						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						response.setContentType("application/json");
						map.put("status", "failed");
						map.put("message", "wrong password");
						response.getWriter().println(gson.toJson(map));
						response.getWriter().flush();
						System.out.println(gson.toJson(map));
					}
				} else {
					response.setStatus(HttpServletResponse.SC_NOT_FOUND);
					response.setContentType("application/json");
					map.put("status", "failed");
					map.put("message", "wrong username");
					response.getWriter().println(gson.toJson(map));
					response.getWriter().flush();
					System.out.println(gson.toJson(map));
				}
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static class User {
		private String username;
		private String password;

		public User(String username, String password) {
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

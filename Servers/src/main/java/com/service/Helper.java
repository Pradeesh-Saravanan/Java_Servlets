package com.service;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import com.google.gson.*;

public class Helper extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/demo"; 
    private static final String JDBC_USER = "root"; 
    private static final String JDBC_PASSWORD = "password"; 

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }
    @Override
    protected void service(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException {
    	response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE,SEARCHTITLE,SEARCHCONTENT,OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    	String method = request.getMethod();
    	if("GET".equalsIgnoreCase(method)) {
    		doGet(request,response);
    	}
    	else if("POST".equalsIgnoreCase(method)) {
    		doPost(request,response);
    	}
    	else if("PUT".equalsIgnoreCase(method)) {
    		doPut(request,response);
    	}
    	else if("DELETE".equalsIgnoreCase(method)) {
    		doDelete(request,response);
    	}
    	else if("SEARCHTITLE".equalsIgnoreCase(method)) {
    		searchByTitle(request,response);
    	}
    	else if("SEARCHCONTENT".equalsIgnoreCase(method)) {
    		searchByContent(request,response);
    	}
    }
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK); 
    }
    @Override
    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ServletException("MySQL Driver not found", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.setHeader("Access-Control-Allow-Origin", "*"); 
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        response.setContentType("application/json");
        String url = request.getRequestURI();
        if(url.contains("title")) {
        	searchByTitle(request,response);
        	return;
        }
        if(url.contains("content")) {
        	searchByContent(request,response);
        	return;
        }
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM posts")) {

            List<Post> posts = new ArrayList<>();
            while (rs.next()) {
                String title = rs.getString("title");
                String body = rs.getString("body");
                posts.add(new Post(title, body));
            }

            String json = gson.toJson(posts);
            out.print(json);
            out.flush();

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
            out.print("{\"error\": \"Database error\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.setHeader("Access-Control-Allow-Origin", "*"); 
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String jsonString = sb.toString();
        Gson gson = new Gson();
        Post post = gson.fromJson(jsonString, Post.class);

        try (Connection conn = getConnection()) {
            String query = "INSERT INTO posts (title, body) VALUES (?, ?);";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, post.getTitle());
                stmt.setString(2, post.getBody());
                stmt.executeUpdate();
            }
            response.setStatus(HttpServletResponse.SC_CREATED); 
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = response.getWriter();
            out.print("{\"error\": \"Database error\"}");
            out.flush();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.setHeader("Access-Control-Allow-Origin", "*"); 
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String jsonString = sb.toString();
        Gson gson = new Gson();
        Post post = gson.fromJson(jsonString, Post.class);

        try (Connection conn = getConnection()) {
            String query = "UPDATE posts SET body = ? WHERE title = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, post.getBody());
                stmt.setString(2, post.title);  

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    response.setStatus(HttpServletResponse.SC_OK);  
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);  
                }
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);  
            PrintWriter out = response.getWriter();
            out.print("{\"error\": \"Database error\"}");
            out.flush();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.setHeader("Access-Control-Allow-Origin", "*"); 
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        
        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        response.setHeader(JDBC_URL, JDBC_PASSWORD);
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String jsonString = sb.toString();
        Gson gson = new Gson();
        Post post = gson.fromJson(jsonString, Post.class);
        try (Connection conn = getConnection()) {
            String query = "DELETE FROM posts WHERE title = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, post.title);  
                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT); 
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND); 
                }
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);  
            PrintWriter out = response.getWriter();
            out.print("{\"error\": \"Database error\"}");
            out.flush();
        }
    }
    protected void searchByTitle(HttpServletRequest request, HttpServletResponse response) 
    	    throws ServletException, IOException {
    	    
    	    response.setHeader("Access-Control-Allow-Origin", "*");
    	    response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    	    response.setHeader("Access-Control-Allow-Headers", "Content-Type");

    	    if ("OPTIONS".equals(request.getMethod())) {
    	        response.setStatus(HttpServletResponse.SC_OK);
    	        return;
    	    }

    	    response.setContentType("application/json");
    	    PrintWriter out = response.getWriter();
    	    Gson gson = new Gson();

    	    try {
//    	        BufferedReader reader = request.getReader();
//    	        Map<String, String> map = gson.fromJson(reader, Map.class);
//    	        String key = map.get("key");
    	    	String key = request.getParameter("key");
    	        String query = "SELECT * FROM posts WHERE title LIKE ?";
    	        try (Connection conn = getConnection();
    	             PreparedStatement stmt = conn.prepareStatement(query)) {
    	            
    	            stmt.setString(1, "%" + key + "%"); 
    	            ResultSet rs = stmt.executeQuery();

    	            List<Post> posts = new ArrayList<>();
    	            while (rs.next()) {
    	                posts.add(new Post(rs.getString("title"), rs.getString("body")));
    	            }

    	            out.print(gson.toJson(posts));
    	            out.flush();
    	        }
    	    } catch (SQLException e) {
    	        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    	        out.print("{\"error\": \"Database error\"}");
    	    }
    	}
    protected void searchByContent(HttpServletRequest request, HttpServletResponse response) 
    	    throws ServletException, IOException {
    	    
    	    response.setHeader("Access-Control-Allow-Origin", "*");
    	    response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE,SEARCHBYCONTENT, OPTIONS");
    	    response.setHeader("Access-Control-Allow-Headers", "Content-Type");

    	    if ("OPTIONS".equals(request.getMethod())) {
    	        response.setStatus(HttpServletResponse.SC_OK);
    	        return;
    	    }

    	    response.setContentType("application/json");
    	    PrintWriter out = response.getWriter();
    	    Gson gson = new Gson();

    	    try {
    	        BufferedReader reader = request.getReader();
    	        Map<String, String> map = gson.fromJson(reader, Map.class);
    	        String key = map.get("key");

    	        String query = "SELECT * FROM posts WHERE body LIKE ?";
    	        try (Connection conn = getConnection();
    	             PreparedStatement stmt = conn.prepareStatement(query)) {
    	            
    	            stmt.setString(1, "%" + key + "%"); 
    	            ResultSet rs = stmt.executeQuery();

    	            List<Post> posts = new ArrayList<>();
    	            while (rs.next()) {
    	                posts.add(new Post(rs.getString("title"), rs.getString("body")));
    	            }

    	            out.print(gson.toJson(posts));
    	            out.flush();
    	        }
    	    } catch (SQLException e) {
    	        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    	        out.print("{\"error\": \"Database error\"}");
    	    }
    	}
    public static class Post {
        private String title;
        private String body;

        public Post(String title, String body) {
            this.title = title;
            this.body = body;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }
}

//package com.service;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.List;
//
//
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.sql.SQLException;
//import java.sql.*;
//import java.io.*;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import com.google.gson.Gson;
//public class Helper extends HttpServlet{
//	Connection conn;
//	public static class Post{
//		private String title;
//		private String body;
//		public Post(String title,String body) {
//			this.title =title;
//			this.body = body;
//		}
//		public String getTitle() {
//			return this.title;
//		}
//		public void setTitle(String title) {
//			this.title = title;
//		}
//		public String getBody() {
//			return this.body;
//		}
//		public void setBody(String body) {
//			this.body = body;
//		}
//	}
//	private static Connection getConnection() throws SQLException{
//		return DriverManager.getConnection("jdbc:mysql://localhost:3306/demo","root","password");
//	}
//	@Override
//	public void init() throws ServletException{
//		try {
//			Class.forName("com.mysql.cj.jdbc.Driver");
//			conn = getConnection();
//		}
//		catch(Exception e){
//			e.printStackTrace();
//		}
//	}
//	@Override 
//	protected void doOptions(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{
//		response.setHeader("Access-Control-Allow-Origin", "*");
//		response.setHeader("Allow-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
//		response.setHeader("Access-Control-Allow-Methods","Content-Type");
//		response.setStatus(HttpServletResponse.SC_OK);
//	}
//	@Override
//	public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
//
//		PrintWriter out = response.getWriter();
//		Gson gson = new Gson();
//		try {
//			Connection conn = getConnection();
//			
//			Statement stmt = conn.createStatement();
//			ResultSet rs = stmt.executeQuery("Select * from posts;");
//			List<Post> posts = new ArrayList<>();
//			while(rs.next()) {
//				posts.add(new Post(rs.getString(0),rs.getString(1)));
//			}
//			String json = gson.toJson(posts);
//			out.print(json);
//			out.flush();
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//		}
//	}
//	@Override
//	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
//
//		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
//		StringBuilder sb = new StringBuilder();
//		String line;
//		while((line=reader.readLine())!=null) {
//			sb.append(line);
//		}
//		String jsonString = sb.toString();
//		Gson gson =new Gson();
//		Post post = gson.fromJson(jsonString,Post.class);
//		String query = "insert into posts(title,body) values(?,?); ";
//		try(PreparedStatement stmt = conn.prepareStatement(query)){
//			stmt.setString(1, post.getTitle());
//			stmt.setString(2,post.getBody());
//			stmt.executeUpdate();
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//		}
//	}
//	@Override
//	protected void doPut(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
//		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
//		StringBuilder sb = new StringBuilder();
//		String line ;
//		while((line=reader.readLine())!=null) {
//			sb.append(line);
//		}
//		String json = sb.toString();
//		Gson gson = new Gson();
//		Post post = gson.fromJson(json, Post.class);
//		String query = "update posts set body = ? where title = ?";
//		try(PreparedStatement stmt = conn.prepareStatement(query)){
//			stmt.setString(1, post.title);
//			stmt.setString(2, post.body);
//			int rows = stmt.executeUpdate();
//			if(rows>0) {
//				response.setStatus(HttpServletResponse.SC_OK);
//			}
//			else {
//				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//			}
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//		}
//	}
//	@Override
//	protected void doDelete(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException{
//		BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
//		StringBuilder sb= new StringBuilder();
//		String line;
//		while((line=reader.readLine())!=null) {
//			sb.append(line);
//		}
//		String json = sb.toString();
//		Gson gson = new Gson();
//		Post post = gson.fromJson(json, Post.class);
//		String query = "delete from posts where title = ?";
//		try(PreparedStatement stmt =  conn.prepareStatement(query)){
//			stmt.setString(0, post.title);
//			int rows = stmt.executeUpdate();
//			if(rows>0) {
//				response.setStatus(HttpServletResponse.SC_OK);
//			}
//			else {
//				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//			}
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//		}
//	}
//}
//
//
//
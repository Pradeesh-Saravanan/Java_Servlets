package com.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;


//@WebFilter("/Gatekeeper")
public class Gatekeeper extends HttpFilter implements Filter {
       
    private static final long serialVersionUID = -3257553160062404652L;

	/**
     * @see HttpFilter#HttpFilter()
     */
    public Gatekeeper() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		// place your code here
		System.out.println("Filter running!");
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:5500");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        
        httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setHeader("Expires", "0");

        // Disable ETag and Last-Modified to prevent 304 responses
        httpResponse.setHeader("ETag", ""); 
        httpResponse.setHeader("Last-Modified", "0");
        
        Cookie cks[] = httpRequest.getCookies();
        System.out.println(httpRequest.getContextPath());
        boolean flag = false;
        if(cks!=null) {
        for(Cookie ck:cks) {
        	if(ck.getValue().equals(request.getParameter("user"))) {
        		System.out.println("Authentication in filter is successful!");
        		flag = true;
        		chain.doFilter(request, response);
        	}
        }
        }	
        else {
        	response.getWriter().println("No cookies ");
        	System.out.println("No cookies in filter...");
        	return;
        }
        if(!flag) {
        	Map<String,String> map = new HashMap<>();
        	Gson gson = new Gson();
        	System.out.println("Authentication failed");
        	httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        	httpResponse.setContentType("application/json");
			map.put("status", "failed");
			map.put("message", "wrong password");
			httpResponse.getWriter().println(gson.toJson(map));
			httpResponse.getWriter().flush();
        	return;
        }

	}

	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}

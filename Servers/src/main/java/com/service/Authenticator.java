package com.service;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

public class Authenticator extends HttpFilter implements Filter {
    private static final long serialVersionUID = 1438474301554625151L;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)throws IOException, ServletException {
        System.out.println("Filter running!");
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        
        Cookie[] cks =httpRequest.getCookies();
        System.out.println(request.getParameter("user"));
        if(cks!=null) {
        for(Cookie ck:cks) {
        	if(ck.getValue().equals(request.getParameter("user"))) {
        		System.out.println("Authentication in filter is successful!");
        	}
        }}
        else {
        	System.out.println("No cookies in filter...");
        }
		chain.doFilter(request, response);
    }
}

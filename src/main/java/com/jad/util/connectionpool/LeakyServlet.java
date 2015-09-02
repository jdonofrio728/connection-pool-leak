package com.jad.util.connectionpool;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

@ManagedBean
public class LeakyServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	@Resource(lookup = "claimsDataSource")
	private DataSource ds;
	private List<Connection> connectionList;
	
	@PostConstruct
	public void init(){
		log("Servlet initialized");
		connectionList = new ArrayList<Connection>();
		if(ds == null){
			log("Looking up datasource");
			InitialContext ctx;
			try {
				ctx = new InitialContext();
				ds = (DataSource) ctx.lookup("claimsDataSource");
			} catch (NamingException e) {
				e.printStackTrace();
			}

		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log("Http request received");

		if(req.getParameter("invalidate") != null){
			log("Invalidating session and releasing connections");
			req.getSession().invalidate();
			releaseConnections();
		}
		if(req.getParameter("connection") != null){
			log("Grabbing Connection");
			grabConnection();
		}
		req.getSession(true);
		
		String html="<html><head></head><body><h1>Hello, " + connectionList.size() + "</h1><form><button name=\"connection\">Grab Connection</button><button name=\"invalidate\">Invalidate</button></form></body></html>";
		resp.getOutputStream().write(html.getBytes());
	}

	private void releaseConnections(){
		for(Connection c : connectionList){
			try {
				c.close();
			} catch (SQLException e) {
				log("Failed to close connection");
				e.printStackTrace();
			}
		}
		connectionList = new ArrayList<Connection>();
	}
	private void grabConnection(){
		try {
			connectionList.add(ds.getConnection());
			log("Connection:  " + connectionList.size());
		} catch (SQLException e) {
			log("Failed to grab connection");
			e.printStackTrace();
		}
	}
}

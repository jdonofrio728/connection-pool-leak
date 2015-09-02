package com.jad.util.connectionpool.ejb;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Stateful;
import javax.sql.DataSource;

@Stateful
public class CPBean implements LocalConnectionService{
	@Resource(lookup = "claimsDataSource")
	private DataSource ds;
	private List<Connection> connectionList;
	
	@PostConstruct()
	public void init(){
		log("Bean initialized");
		connectionList = new ArrayList<Connection>();
	}
	
	@PreDestroy
	public void destroy(){
		for(Connection c : connectionList){
			try {
				c.close();
			} catch (SQLException e) {
				log("Failed to close connection");
				e.printStackTrace();
			}
		}
	}
	
	public void grabConnection(){
		log("Grabbing connection");
		try {
			connectionList.add(ds.getConnection());
		} catch (SQLException e) {
			log("Failed to grab connection");
			e.printStackTrace();
		}
	}
	private void log(String msg){
		System.out.println(msg);
	}
}

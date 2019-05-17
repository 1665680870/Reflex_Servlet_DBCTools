package com.Servlet.Tookit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.reflex.Object.InstanceReflexParse;

import DBConnect.DBCTools;

public class ContextSession implements ServletContextListener,HttpSessionListener{
	public InstanceReflexParse reflexParse;
	 public ServletTookit servletTookit;
	 public DBCTools dbcTools;
	public ContextSession() {
		dbcTools = DBCTools.getDbcTools("default", "default.date");
	    reflexParse=InstanceReflexParse.getReflexParse();
	    servletTookit=ServletTookit.geServletTookit(reflexParse);
	}
	
	public void contextInit(ServletContextEvent contextEvent){}
	public void contextDie(ServletContextEvent contextEvent){}
	public void sessionInit(HttpSessionEvent sessionEvent){}
	public void sessionDie(HttpSessionEvent sessionEvent){}
	public void setDbcTools(DBCTools dbcTools) {this.dbcTools = dbcTools;}
	
	
	@Override
	public final void contextDestroyed(ServletContextEvent contextEvent) {
		new CD(contextEvent).start();
	}

	@Override
	public final void contextInitialized(ServletContextEvent contextEvent) {
		new CC(contextEvent).start();		
	}

	@Override
	public final void sessionCreated(HttpSessionEvent sessionEvent) {
		sessionInit(sessionEvent);
	}

	@Override
	public final void sessionDestroyed(HttpSessionEvent sessionEvent) {
		sessionDie(sessionEvent);
	}
	
	class CC extends Thread{
		private ServletContextEvent contextEvent;
		public CC(ServletContextEvent contextEvent) {
			this.contextEvent=contextEvent;
		}
		@Override
		public void run() {
			contextInit(contextEvent);
		}
	}
	class CD extends Thread{
		private ServletContextEvent contextEvent;
		public CD(ServletContextEvent contextEvent){
			this.contextEvent=contextEvent;
		}
		@Override
		public void run() {
			contextDie(contextEvent);
		}
	}
}

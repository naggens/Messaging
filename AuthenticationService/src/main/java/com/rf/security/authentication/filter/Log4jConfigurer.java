package com.rf.security.authentication.filter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.PropertyConfigurator;

public class Log4jConfigurer implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent context) {
		// TODO Auto-generated method stub

	}

	@Override
	public void contextInitialized(ServletContextEvent context) {
		load();
	}
	
	private void load(){
		System.out.println("Enetered Context Listener..");
		try {
			InputStream stream = Log4jConfigurer.class.getClassLoader().getResourceAsStream("log4j.properties");
			Properties properties = new Properties();
			properties.load(stream);
			PropertyConfigurator.configure(properties);
		} catch (IOException ex) {
			System.out.println("Could not configurer Log4j");
			ex.printStackTrace();
		}
	}

}

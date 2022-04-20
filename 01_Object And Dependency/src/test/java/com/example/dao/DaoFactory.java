package com.example.dao;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

@Configuration // indicating it is a bean factory
public class DaoFactory {
	@Bean // indicating that a method produces a bean to be managed by the Spring
			// container
	public DataSource dataSource() {
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

		dataSource.setDriverClass(com.mysql.cj.jdbc.Driver.class); // The 'com.mysql.jdbc.Driver.class' is deprecated.
		dataSource.setUrl("jdbc:mysql://localhost:3306/mogumogu");
		dataSource.setUsername("mogumogu");
		dataSource.setPassword("1234");

		return dataSource;
	}

	@Bean
	public UserDao userDao() { // It makes userDao and contains connection information and return it.
		UserDao userDao = new UserDao(); // dao object
		userDao.setDataSource(dataSource());
		return userDao;
	}
}

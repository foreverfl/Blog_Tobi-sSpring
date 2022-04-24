package com.example.dao;

import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.example.entities.User;

public class UserDaoTest {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
		UserDao dao = context.getBean("userDao", UserDao.class);
		dao.deleteAll();

		User user = new User();
		user.setId("mogumogu");
		user.setName("Taro");
		user.setPassword("1234");

		dao.add(user);

		System.out.println(user.getId() + " is successfully registered.");
		System.out.println();

		User user_check = dao.get(user.getId());

		System.out.println(user_check.getName());
		System.out.println(user_check.getPassword());
		System.out.println(user_check.getId() + " is successfully searched.");
	}
}

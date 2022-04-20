package com.example.dao;

import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.example.entities.User;

public class UserDaoTest {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
		// using a bean from a bean factory.
		UserDao dao = context.getBean("userDao", UserDao.class);

		User user = new User();
		user.setId("mogumogu");
		user.setName("타로");
		user.setPassword("1234");

		dao.add(user);

		System.out.println(user.getId() + " 성공적으로 등록되었습니다.");
		System.out.println();

		User user_check = dao.get(user.getId());

		System.out.println(user_check.getName());
		System.out.println(user_check.getPassword());
		System.out.println(user_check.getId() + " 성공적으로 조회되었습니다.");
	}
}

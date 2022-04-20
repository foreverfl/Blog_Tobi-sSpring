package com.example.abstractingService.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.abstractingService.entities.Level;
import com.example.abstractingService.entities.User;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "../test-applicationContext.xml")
public class UserDaoTest {
	@Autowired
	UserDao dao;
	@Autowired
	DataSource dataSource;

	private User user1;
	private User user2;
	private User user3;

	@BeforeEach
	public void setUp() {
		this.user1 = new User("mogumogu1", "1234", "taro1", "mogumogu1@tarotaro90.com", Level.BASIC, 1, 0);
		this.user2 = new User("mogumogu2", "1234", "taro2", "mogumogu2@tarotaro90.com", Level.SILVER, 55, 10);
		this.user3 = new User("mogumogu3", "1234", "taro3", "mogumogu3@tarotaro90.com", Level.GOLD, 100, 40);
	}

	@Test
	public void andAndGet() {
		dao.deleteAll();
		assertEquals(dao.getCount(), 0);

		dao.add(user1);
		dao.add(user2);
		assertEquals(dao.getCount(), 2);

		User userget1 = dao.get(user1.getId());
		checkSameUser(userget1, user1);

		User userget2 = dao.get(user2.getId());
		checkSameUser(userget2, user2);
	}

	private void getUserFailure() throws SQLException {
		dao.deleteAll();
		assertEquals(dao.getCount(), 0);

		dao.get("unknown_id");
	}

	@Test
	public void getUserFailureTest() throws SQLException {
		assertThrows(EmptyResultDataAccessException.class, () -> getUserFailure());
	}

	@Test
	public void count() {
		dao.deleteAll();
		assertEquals(dao.getCount(), 0);

		dao.add(user1);
		assertEquals(dao.getCount(), 1);

		dao.add(user2);
		assertEquals(dao.getCount(), 2);

		dao.add(user3);
		assertEquals(dao.getCount(), 3);
	}

	@Test
	public void getAll() {
		dao.deleteAll();

		List<User> users0 = dao.getAll();
		assertEquals(users0.size(), 0);

		dao.add(user1);
		List<User> users1 = dao.getAll();
		assertEquals(users1.size(), 1);
		checkSameUser(user1, users1.get(0));

		dao.add(user2);
		List<User> users2 = dao.getAll();
		assertEquals(users2.size(), 2);
		checkSameUser(user1, users2.get(0));
		checkSameUser(user2, users2.get(1));

		dao.add(user3);
		List<User> users3 = dao.getAll();
		assertEquals(users3.size(), 3);
		checkSameUser(user1, users3.get(0));
		checkSameUser(user2, users3.get(1));
		checkSameUser(user3, users3.get(2));
	}

	private void checkSameUser(User user1, User user2) {
		assertEquals(user1.getId(), user2.getId());
		assertEquals(user1.getName(), user2.getName());
		assertEquals(user1.getPassword(), user2.getPassword());
		assertEquals(user1.getEmail(), user2.getEmail());
		assertEquals(user1.getLevel(), user2.getLevel());
		assertEquals(user1.getLogin(), user2.getLogin());
		assertEquals(user1.getRecommend(), user2.getRecommend());
	}

	private void duplicateKey() {
		dao.deleteAll();

		dao.add(user1);
		dao.add(user1);
	}

	@Test
	public void duplciateKeyTest() {
		assertThrows(DuplicateKeyException.class, () -> duplicateKey());
	}

	@Test
	public void sqlExceptionTranslate() {
		dao.deleteAll();

		try {
			dao.add(user1);
			dao.add(user1);
		} catch (DuplicateKeyException ex) {
			SQLException sqlEx = (SQLException) ex.getCause();
			SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
			DataAccessException transEx = set.translate(null, null, sqlEx);
			assertTrue(transEx instanceof DuplicateKeyException);
		}
	}

	@Test
	public void update() {
		dao.deleteAll();

		dao.add(user1);
		dao.add(user2);

		user1.setName("taro4");
		user1.setPassword("1234");
		user1.setEmail("mogumogu4@tarotaro90.com");
		user1.setLevel(Level.GOLD);
		user1.setLogin(1000);
		user1.setRecommend(999);

		dao.update(user1);

		User user1update = dao.get(user1.getId());
		checkSameUser(user1, user1update);
		User user2same = dao.get(user2.getId());
		checkSameUser(user2, user2same);
	}

}

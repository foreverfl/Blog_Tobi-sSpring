package com.example.abstractingService.service;

import static com.example.abstractingService.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static com.example.abstractingService.service.UserService.MIN_RECCOMEND_FOR_GOLD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.abstractingService.dao.UserDao;
import com.example.abstractingService.entities.Level;
import com.example.abstractingService.entities.User;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "../test-applicationContext.xml")
public class UserServiceTest {
	@Autowired
	UserService userService;
	@Autowired
	UserDao userDao;
	@Autowired
	MailSender mailSender;
	@Autowired
	PlatformTransactionManager transactionManager;

	List<User> users; // test fixture

	@BeforeEach
	public void setUp() {
		users = Arrays.asList(
				new User("mogumogu1", "taro1", "p1", "mogumogu1@tarotaro90.com", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0),
				new User("mogumogu2", "taro2", "p2", "mogumogu2@tarotaro90.com", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
				new User("mogumogu3", "taro3", "p3", "mogumogu3@tarotaro90.com", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD - 1),
				new User("mogumogu4", "taro4", "p4", "mogumogu4@tarotaro90.com", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD),
				new User("mogumogu5", "taro5", "p5", "mogumogu5@tarotaro90.com", Level.GOLD, 100, Integer.MAX_VALUE));
	}

	@Test
	@DirtiesContext
	public void upgradeLevels() {
		userDao.deleteAll();
		for (User user : users)
			userDao.add(user);

		MockMailSender mockMailSender = new MockMailSender();
		userService.setMailSender(mockMailSender);

		userService.upgradeLevels();

		checkLevelUpgraded(users.get(0), false);
		checkLevelUpgraded(users.get(1), true);
		checkLevelUpgraded(users.get(2), false);
		checkLevelUpgraded(users.get(3), true);
		checkLevelUpgraded(users.get(4), false);

		List<String> request = mockMailSender.getRequests();
		assertEquals(request.size(), 2);
		assertEquals(request.get(0), users.get(1).getEmail());
		assertEquals(request.get(1), users.get(3).getEmail());
	}

	static class MockMailSender implements MailSender {
		private List<String> requests = new ArrayList<String>();

		public List<String> getRequests() {
			return requests;
		}

		public void send(SimpleMailMessage mailMessage) throws MailException {
			requests.add(mailMessage.getTo()[0]);
		}

		public void send(SimpleMailMessage[] mailMessage) throws MailException {
		}
	}

	private void checkLevelUpgraded(User user, boolean upgraded) {
		User userUpdate = userDao.get(user.getId());
		if (upgraded) {
			assertEquals(userUpdate.getLevel(), user.getLevel().nextLevel());
		} else {
			assertEquals(userUpdate.getLevel(), user.getLevel());
		}
	}

	@Test
	public void add() {
		userDao.deleteAll();

		User userWithLevel = users.get(4);
		User userWithoutLevel = users.get(0);
		userWithoutLevel.setLevel(null);

		userService.add(userWithLevel);
		userService.add(userWithoutLevel);

		User userWithLevelRead = userDao.get(userWithLevel.getId());
		User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

		assertEquals(userWithLevelRead.getLevel(), userWithLevel.getLevel());
		assertEquals(userWithoutLevelRead.getLevel(), Level.BASIC);
	}

	@Test
	public void upgradeAllOrNothing() {
		UserService testUserService = new TestUserService(users.get(3).getId());
		testUserService.setUserDao(this.userDao);
		testUserService.setTransactionManager(this.transactionManager);
		testUserService.setMailSender(this.mailSender);

		userDao.deleteAll();
		for (User user : users)
			userDao.add(user);

		try {
			testUserService.upgradeLevels();
			fail("TestUserServiceException expected");
		} catch (TestUserServiceException e) {
		}

		checkLevelUpgraded(users.get(1), false);
	}

	static class TestUserService extends UserService {
		private String id;

		private TestUserService(String id) {
			this.id = id;
		}

		protected void upgradeLevel(User user) {
			if (user.getId().equals(this.id))
				throw new TestUserServiceException();
			super.upgradeLevel(user);
		}
	}

	static class TestUserServiceException extends RuntimeException {
	}

}

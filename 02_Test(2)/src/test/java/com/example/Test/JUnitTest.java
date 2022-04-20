package com.example.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
//Integrates the Spring TestContext Framework into JUnit 5's Jupiter programming model
@ContextConfiguration(locations = "junit.xml")
//Defines class-level metadata that is used to determine how to load and configure an ApplicationContext for integration tests
@DirtiesContext
//Should therefore be closed and removed from the context cache.
public class JUnitTest {
	@Autowired
	ApplicationContext context;

	static Set<JUnitTest> testObjects = new HashSet<JUnitTest>();
	static ApplicationContext contextObject = null;

	@Test
	public void test1() {
		assertThat(!testObjects.contains(this));
		testObjects.add(this);

		assertEquals(contextObject == null || contextObject == this.context, true);
		contextObject = this.context;
	}

	@Test
	public void test2() {
		assertThat(!testObjects.contains(this));
		testObjects.add(this);

		assertTrue(contextObject == null || contextObject == this.context);
		contextObject = this.context;
	}
}

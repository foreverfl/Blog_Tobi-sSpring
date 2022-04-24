package com.example.test;

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
@ContextConfiguration(locations = "../junit.xml")
@DirtiesContext
public class JUnitTest {
	@Autowired
	ApplicationContext context;

	static Set<JUnitTest> testObjects = new HashSet<JUnitTest>();
	static ApplicationContext contextObject = null;

	@Test
	public void test1() {
		assertTrue(!testObjects.contains(this));
		testObjects.add(this);

		assertEquals(contextObject == null, true);
		contextObject = this.context;
	}

	@Test
	public void test2() {
		assertTrue(!testObjects.contains(this));
		testObjects.add(this);

		assertTrue(contextObject == this.context);
		contextObject = this.context;
	}

	@Test
	public void test3() {
		assertTrue(!testObjects.contains(this));
		testObjects.add(this);

		assertTrue(contextObject == this.context);
		contextObject = this.context;
	}

}

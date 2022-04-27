package com.example.IoCContainerAndDI.prototype;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

public class ScopeTest {
	@Test
	public void singletonScope() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(SingletonBean.class, SingletonClientBean.class);

		Set<SingletonBean> bean = new HashSet<SingletonBean>();
		bean.add(ac.getBean(SingletonBean.class));
		bean.add(ac.getBean(SingletonBean.class));
		assertEquals(bean.size(), 1);

		bean.add(ac.getBean(SingletonClientBean.class).bean1);
		bean.add(ac.getBean(SingletonClientBean.class).bean2);
		assertEquals(bean.size(), 1);
	}

	static class SingletonBean {
	}

	static class SingletonClientBean {
		@Autowired
		SingletonBean bean1;
		@Autowired
		SingletonBean bean2;
	}

	@Test
	public void prototypeScope() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class, PrototypeClientBean.class);

		Set<PrototypeBean> bean = new HashSet<PrototypeBean>();
		bean.add(ac.getBean(PrototypeBean.class));
		assertEquals(bean.size(), 1);
		bean.add(ac.getBean(PrototypeBean.class));
		assertEquals(bean.size(), 2);

		bean.add(ac.getBean(PrototypeClientBean.class).bean1);
		assertEquals(bean.size(), 3);
		bean.add(ac.getBean(PrototypeClientBean.class).bean2);
		assertEquals(bean.size(), 4);
	}

	@Component("prototypeBean")
	@Scope("prototype")
	static class PrototypeBean {
	}

	static class PrototypeClientBean {
		@Autowired
		PrototypeBean bean1;
		@Autowired
		PrototypeBean bean2;
	}

}

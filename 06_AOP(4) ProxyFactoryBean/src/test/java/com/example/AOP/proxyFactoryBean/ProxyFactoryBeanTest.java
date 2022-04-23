package com.example.AOP.proxyFactoryBean;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactoryBean;

public class ProxyFactoryBeanTest {
	@Test
	public void proxyFactoryBean() {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());
		pfBean.addAdvice(new UppercaseAdvice());

		Hello proxiedHello = (Hello) pfBean.getObject();

		assertEquals(proxiedHello.sayHello("Toby"), "HELLO TOBY");
		assertEquals(proxiedHello.sayHi("Toby"), "HI TOBY");
		assertEquals(proxiedHello.sayThankYou("Toby"), "THANK YOU TOBY");
	}

	static class UppercaseAdvice implements MethodInterceptor {
		public Object invoke(MethodInvocation invocation) throws Throwable {
			String ret = (String) invocation.proceed();
			return ret.toUpperCase();
		}
	}

	static interface Hello {
		String sayHello(String name);

		String sayHi(String name);

		String sayThankYou(String name);
	}

	static class HelloTarget implements Hello {
		public String sayHello(String name) {
			return "Hello " + name;
		}

		public String sayHi(String name) {
			return "Hi " + name;
		}

		public String sayThankYou(String name) {
			return "Thank You " + name;
		}
	}
}

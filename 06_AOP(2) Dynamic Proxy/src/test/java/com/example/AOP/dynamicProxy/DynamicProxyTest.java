package com.example.AOP.dynamicProxy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.jupiter.api.Test;

public class DynamicProxyTest {
	@Test
	public void simpleProxy() {
		Hello hello = new HelloTarget();
		assertEquals(hello.sayHello("Taro"), "Hello Taro");
		assertEquals(hello.sayHi("Taro"), "Hi Taro");
		assertEquals(hello.sayThankYou("Taro"), "Thank You Taro");

		Hello hello_uppercase = new HelloUppercase(new HelloTarget());
		assertEquals(hello_uppercase.sayHello("Taro"), "HELLO TARO");
		assertEquals(hello_uppercase.sayHi("Taro"), "HI TARO");
		assertEquals(hello_uppercase.sayThankYou("Taro"), "THANK YOU TARO");

		Hello proxiedHello = (Hello) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { Hello.class },
				new UppercaseHandler(new HelloTarget()));

		assertEquals(proxiedHello.sayHello("Taro"), "HELLO TARO");
		assertEquals(proxiedHello.sayHi("Taro"), "HI TARO");
		assertEquals(proxiedHello.sayThankYou("Taro"), "THANK YOU TARO");
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

	static class HelloUppercase implements Hello {
		Hello hello;

		public HelloUppercase(Hello hello) {
			this.hello = hello;
		}

		public String sayHello(String name) {
			return hello.sayHello(name).toUpperCase();
		}

		public String sayHi(String name) {
			return hello.sayHi(name).toUpperCase();
		}

		public String sayThankYou(String name) {
			return hello.sayThankYou(name).toUpperCase();
		}

	}

	static class UppercaseHandler implements InvocationHandler {
		Object target;

		private UppercaseHandler(Object target) {
			this.target = target;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Object ret = method.invoke(target, args);
			if (ret instanceof String && method.getName().startsWith("say")) {
				return ((String) ret).toUpperCase();
			} else {
				return ret;
			}
		}
	}

}

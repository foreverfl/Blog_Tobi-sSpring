package com.example.coreofSpring.unmarshalling;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.jupiter.api.Test;

public class JaxbTest {
//	@Test
//	public void readSqlmap() throws JAXBException, IOException {
//
//		String contextPath = Sqlmap.class.getPackage().getName();
//		JAXBContext context = JAXBContext.newInstance(contextPath);
//		Unmarshaller unmarshaller = context.createUnmarshaller();
//		Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(getClass().getResourceAsStream("sqlmap.xml"));
//
//		List<SqlType> sqlList = sqlmap.getSql();
//
//		assertEquals(sqlList.size(), 3);
//		assertEquals(sqlList.get(0).getKey(), "add");
//		assertEquals(sqlList.get(0).getValue(), "insert");
//		assertEquals(sqlList.get(1).getKey(), "get");
//		assertEquals(sqlList.get(1).getValue(), "select");
//		assertEquals(sqlList.get(2).getKey(), "delete");
//		assertEquals(sqlList.get(2).getValue(), "delete");
//	}
}

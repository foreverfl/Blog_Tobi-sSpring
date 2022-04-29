package com.example.DataAccessTechnology.JDBC;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "../jdbctest-context.xml")
public class JdbcTest {
	@Autowired
	DataSource dataSource;

	SimpleJdbcInsert simplejdbcinsert;
	JdbcTemplate jdbctemplate;
	NamedParameterJdbcTemplate namedparameterjdbctemplate;

	@BeforeAll
	public static void init() throws IllegalStateException, NamingException, SQLException {
		SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
		SimpleDriverDataSource datasource = new SimpleDriverDataSource(new com.mysql.cj.jdbc.Driver(),
				"jdbc:mysql://localhost/mogumogu", "mogumogu", "1234");
		builder.bind("jdbc/DefaultDS", datasource);
		builder.activate();
	}

	@BeforeEach
	public void before() {
		jdbctemplate = new JdbcTemplate(dataSource);
		namedparameterjdbctemplate = new NamedParameterJdbcTemplate(dataSource);
		simplejdbcinsert = new SimpleJdbcInsert(dataSource).withTableName("member");
	}

	@Test
	public void simeJdbcTemplate() {
		ApplicationContext ac = new AnnotationConfigApplicationContext(SimpleDao.class);
		SimpleDao dao = ac.getBean(SimpleDao.class);
		dao.deleteAll();

		// 1, "Spring", 3.5
		// 2, "Book", 10.1
		// 3, "Jdbc" 20.5
		// update()
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", 1);
		map.put("name", "Taro1");
		map.put("point", 3.5);
		dao.insert(map);
		dao.insert(new MapSqlParameterSource().addValue("id", 2).addValue("name", "Taro2").addValue("point", 10.1));
		dao.insert(new Member(3, "Taro3", 20.5));

		// queryForInt()
		assertEquals(dao.rowCount(), 3);
		assertEquals(dao.rowCount(5), 2);
		assertEquals(dao.rowCount(1), 3);

		// queryForObject(Class)
		assertEquals(dao.name(1), "Taro1");
		assertEquals(dao.point(1), 3.5);

		// queryForObject(RowMapper)
		Member member = dao.get(1);
		assertEquals(member.id, 1);
		assertEquals(member.name, "Taro1");
		assertEquals(member.point, 3.5);

		// query(RowMapper)
		assertEquals(dao.find(1).size(), 3);
		assertEquals(dao.find(5).size(), 2);
		assertEquals(dao.find(100).size(), 0);

		// queryForMap
		Map<String, Object> map_01 = dao.getMap(1);
		assertEquals((Integer) map_01.get("id"), 1);
		assertEquals((String) map_01.get("name"), "Taro1");
		assertEquals((Double) map_01.get("point"), 3.5);

		// 1, "Spring2", 3.5
		// 2, "Book2", 10.1
		// 3, "Jdbc" 20.5
		// batchUpdates()
		Map<String, Object>[] paramMaps = new HashMap[2];
		paramMaps[0] = new HashMap<String, Object>();
		paramMaps[0].put("id", 1);
		paramMaps[0].put("name", "Taro11");
		paramMaps[1] = new HashMap<String, Object>();
		paramMaps[1].put("id", 2);
		paramMaps[1].put("name", "Taro22");
		dao.updates(paramMaps);

		assertEquals(dao.name(1), "Taro11");
		assertEquals(dao.name(2), "Taro22");

		// 1, "Spring3", 3.5
		// 2, "Book3", 10.1
		// 3, "Jdbc" 20.5
		dao.NamedParameterJdbcTemplate.batchUpdate("update member set name = :name where id = :id",
				new SqlParameterSource[] { new MapSqlParameterSource().addValue("id", 1).addValue("name", "Taro111"),
						new BeanPropertySqlParameterSource(new Member(2, "Taro222", 0)) });

		assertEquals(dao.name(1), "Taro111");
		assertEquals(dao.name(2), "Taro222");

		((AbstractApplicationContext) ac).close();
	}

	static class SimpleDao {
		JdbcTemplate JdbcTemplate;
		NamedParameterJdbcTemplate NamedParameterJdbcTemplate;

		@Autowired
		public void init(DataSource dataSource) {
			this.JdbcTemplate = new JdbcTemplate(dataSource);
			this.NamedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		}

		public void updates(Map<String, Object>[] maps) {
			this.NamedParameterJdbcTemplate.batchUpdate("update member set name = :name where id = :id", maps);
		}

		public Map<String, Object> getMap(int id) {
			return this.JdbcTemplate.queryForMap("select * from member where id = ?", id);
		}

		public List<Member> find(double point) {
			return this.JdbcTemplate.query("select * from member where point > ?",
					new BeanPropertyRowMapper<Member>(Member.class), point);
		}

		public Member get(int id) {
			return this.JdbcTemplate.queryForObject("select * from member where id = ?",
					new BeanPropertyRowMapper<Member>(Member.class), id);
		}

		public String name(int id) {
			return this.JdbcTemplate.queryForObject("select name from member where id = ?", String.class, id);
		}

		public double point(int id) {
			return this.JdbcTemplate.queryForObject("select point from member where id = ?", Double.class, id);
		}

		public int rowCount() {
			return this.JdbcTemplate.queryForObject("select count(*) from member", Integer.class);
		}

		public int rowCount(double min) {
			return this.NamedParameterJdbcTemplate.queryForObject("select count(*) from member where point > :min",
					new MapSqlParameterSource("min", min), Integer.class);
		}

		public void deleteAll() {
			this.JdbcTemplate.update("delete from member");
		}

		public void insert(Map<String, Object> map) {
			this.NamedParameterJdbcTemplate.update("INSERT INTO MEMBER(ID, NAME, POINT) VALUES(:id, :name, :point)",
					map);
		}

		public void insert(Member member) {
			this.NamedParameterJdbcTemplate.update("INSERT INTO MEMBER(ID, NAME, POINT) VALUES(:id, :name, :point)",
					new BeanPropertySqlParameterSource(member));
		}

		public void insert(SqlParameterSource member) {
			this.NamedParameterJdbcTemplate.update("INSERT INTO MEMBER(ID, NAME, POINT) VALUES(:id, :name, :point)",
					member);
		}

		@Bean
		public DataSource dataSource() {
			try {
				return new SimpleDriverDataSource(new com.mysql.cj.jdbc.Driver(), "jdbc:mysql://localhost/mogumogu",
						"mogumogu", "1234");
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Test
	public void simpleJdbcInsert() {
		jdbctemplate.update("delete from member");

		SimpleJdbcInsert memberInsert = new SimpleJdbcInsert(dataSource).withTableName("member");
		Member member = new Member(1, "Taro1", 3.5);
		memberInsert.execute(new BeanPropertySqlParameterSource(member));
	}

	@Test
	public void simpleJdbcInsertWithGeneratedKey() {
		jdbctemplate.update("delete from register");

		SimpleJdbcInsert registerInsert = new SimpleJdbcInsert(dataSource).withTableName("register")
				.usingGeneratedKeyColumns("id");
		int key = registerInsert.executeAndReturnKey(new MapSqlParameterSource("name", "Spring")).intValue();
		System.out.println(key);
	}

	@Test
	public void simpleJdbcInsertWithSqlParamSource() {
		jdbctemplate.update("delete from member");

		MapSqlParameterSource paramSource = new MapSqlParameterSource().addValue("id", 1).addValue("name", "Taro1")
				.addValue("point", 10.5);
		simplejdbcinsert.execute(paramSource);

		Member member = new Member(2, "Taro2", 3.3);
		simplejdbcinsert.execute(new BeanPropertySqlParameterSource(member));

		JdbcTemplate jdbctemplate = new JdbcTemplate(dataSource);
		List<Map<String, Object>> list = jdbctemplate.queryForList("select * from member order by id");

		assertEquals(list.size(), 2);

		assertEquals((Integer) list.get(0).get("id"), 1);
		assertEquals((String) list.get(0).get("name"), "Taro1");
		assertEquals((Double) list.get(0).get("point"), 10.5);

		assertEquals((Integer) list.get(1).get("id"), 2);
		assertEquals((String) list.get(1).get("name"), "Taro2");
		assertEquals((Double) list.get(1).get("point"), 3.3);
	}

	@Test
	public void simpleJdbcCall() {
		jdbctemplate.update("delete from member");
		jdbctemplate.update("insert into member(id, name, point) values(1, 'Taro1', 0)");

		SimpleJdbcCall simplejdbccall = new SimpleJdbcCall(dataSource).withFunctionName("find_name");
		String result = simplejdbccall.executeFunction(String.class, 1);
		assertEquals(result, "Taro1");
	}
}

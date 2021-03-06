package com.example.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.example.entites.User;

public class UserDao {
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	private JdbcTemplate jdbcTemplate;

	private RowMapper<User> userMapper = new RowMapper<User>() {
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));
			return user;
		}
	};

	public void add(final User user) {
		this.jdbcTemplate.update("insert into users_01(id, name, password) values(?,?,?)", user.getId(), user.getName(),
				user.getPassword());
	}

	public User get(String id) {
		return this.jdbcTemplate.queryForObject("select * from users_01 where id = ?", this.userMapper,
				new Object[] { id });
	}

	public void deleteAll() {
		this.jdbcTemplate.update("delete from users_01");
	}

	public int getCount() {
		return this.jdbcTemplate.queryForObject("select count(*) from users_01", Integer.class);
	}

	public List<User> getAll() {
		return this.jdbcTemplate.query("select * from users_01 order by id", this.userMapper);
	}

}

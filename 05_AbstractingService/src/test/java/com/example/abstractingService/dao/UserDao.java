package com.example.abstractingService.dao;

import java.util.List;

import com.example.abstractingService.entities.User;

public interface UserDao {

	void add(User user);

	User get(String id);

	List<User> getAll();

	void deleteAll();

	int getCount();

	void update(User user);

}
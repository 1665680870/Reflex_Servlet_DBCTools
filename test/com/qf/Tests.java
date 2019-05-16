package com.qf;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.test.entity.User;

import DBConnect.DBCTools;

public class Tests {

	@Test
	public void test() {
		DBCTools dbcTools=DBCTools.getDbcTools("qf", "localhost", "3306", "text", "root", "root");

		try {
			User user = dbcTools.getSingeByName(1, new User());
			List<User> listByName = dbcTools.getListByName(User.class);
			for (User user2 : listByName) {
				System.out.println(user2.getUsername());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

package com.rf.security.authentication.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.rf.security.authentication.domain.UserBO;

public class AuthenticationDAO {
	
	private JdbcTemplate jdbcTemplate;
	private DataSource datasource;
	
	private String insertUserSql;
	private String selectUserSql;
	
	private enum USER {		
		DUMMY_COLUMN, USR_ID, USR_NAME
	}
	
	
	public DataSource getDatasource() {
		return datasource;
	}


	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
		jdbcTemplate = new JdbcTemplate(this.datasource);
	}


	public boolean insert(UserBO userBO){
		InsertUserPrepareStatementSetter params = new InsertUserPrepareStatementSetter(userBO);
		int rows = jdbcTemplate.update(insertUserSql, params);
		return ((rows > 0) ? true:false);
	}
	
	public UserBO getUser(String login){
		UserBO userBO = jdbcTemplate.queryForObject(selectUserSql, new UserRowMapper(), login);
		return userBO;
		
	}
	
	private class UserRowMapper implements RowMapper<UserBO>{

		@Override
		public UserBO mapRow(ResultSet rs, int rowNum) throws SQLException {
			UserBO userBO = new UserBO();
			userBO.setPassword(rs.getString(1));
			userBO.setSalt(rs.getString(2));
			return userBO;
		}
		
	}
	
	private class InsertUserPrepareStatementSetter implements PreparedStatementSetter{
		private UserBO userBO;
		
		InsertUserPrepareStatementSetter(UserBO userBO){
			this.userBO = userBO;
		}
		
		@Override
		public void setValues(PreparedStatement ps) throws SQLException {
			ps.setString(1, userBO.getLogin());
			ps.setString(2, userBO.getPassword());
			ps.setString(3, userBO.getSalt());
		}
		
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}


	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


	public String getInsertUserSql() {
		return insertUserSql;
	}


	public void setInsertUserSql(String insertUserSql) {
		this.insertUserSql = insertUserSql;
	}


	public String getSelectUserSql() {
		return selectUserSql;
	}


	public void setSelectUserSql(String selectUserSql) {
		this.selectUserSql = selectUserSql;
	}
}

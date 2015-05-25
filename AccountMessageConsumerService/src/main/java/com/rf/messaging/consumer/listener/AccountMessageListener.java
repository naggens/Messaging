package com.rf.messaging.consumer.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.rf.messaging.consumer.domain.AccountDO;

public class AccountMessageListener implements MessageListener {
	private static final Log log = LogFactory.getLog(AccountMessageListener.class);
	
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public void onMessage(Message message) {
		// Print message once we receive it
		if (message instanceof ObjectMessage) {
			ObjectMessage msg = (ObjectMessage) message;
			try {
				AccountDO acctMsg = (AccountDO)msg.getObject();
				log.info("Received Account message: " + acctMsg);
				System.out.println("Received Account message: " + acctMsg);
			} catch (JMSException ex) {
				log.error("Exception while receiving message: ", ex);
			}
		}
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new JdbcTemplate(this.dataSource);
	}

}

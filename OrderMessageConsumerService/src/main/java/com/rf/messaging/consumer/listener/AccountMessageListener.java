package com.rf.messaging.consumer.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rf.messaging.consumer.domain.AccountDO;

public class AccountMessageListener implements MessageListener {
	private static final Log log = LogFactory.getLog(AccountMessageListener.class);
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

}

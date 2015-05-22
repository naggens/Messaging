package com.rf.messaging.consumer.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rf.messaging.consumer.domain.OrderDO;

public class OrderMessageListener implements MessageListener {
	private static final Log log = LogFactory
			.getLog(OrderMessageListener.class);

	@Override
	public void onMessage(Message message) {
		// Print message once we receive it
		if (message instanceof ObjectMessage) {
			ObjectMessage msg = (ObjectMessage) message;
			try {
				OrderDO orderMsg = (OrderDO) msg.getObject();
				log.info("Received Account message: " + orderMsg);
				System.out.println("Received Account message: " + orderMsg);
			} catch (JMSException ex) {
				log.error("Exception while receiving message: ", ex);
			}
		}
	}

}

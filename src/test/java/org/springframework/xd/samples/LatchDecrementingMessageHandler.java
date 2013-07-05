package org.springframework.xd.samples;

import java.util.concurrent.CountDownLatch;

import org.springframework.integration.Message;
import org.springframework.integration.MessagingException;
import org.springframework.integration.core.MessageHandler;

public class LatchDecrementingMessageHandler implements MessageHandler {

	private volatile CountDownLatch latch;

	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}

	@Override
	public void handleMessage(Message<?> arg0) throws MessagingException {
		this.latch.countDown();
	}

}

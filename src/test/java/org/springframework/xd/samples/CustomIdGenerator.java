package org.springframework.xd.samples;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.integration.MessageHeaders.IdGenerator;

public class CustomIdGenerator implements IdGenerator {

	private final AtomicLong counter = new AtomicLong();

	@Override
	public UUID generateId() {
		return new UUID(counter.incrementAndGet(), counter.incrementAndGet());
	}

}

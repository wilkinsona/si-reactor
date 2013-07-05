/*
 * Copyright 2002-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.xd.samples;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.Message;
import org.springframework.integration.MessageChannel;
import org.springframework.integration.support.MessageBuilder;

/**
 *
 */
public class PerformanceTests {

	private static final int MESSAGES = 2000000;

	private static final int ITERATIONS = 10;

	@Test
	public void performanceComparison() throws InterruptedException {
		System.out.println("Resending the same message");
		doTest("Direct channel", "org/springframework/xd/samples/PerformanceTestsDirectChannel.xml", true, false);
		doTest("Spring executor channel", "org/springframework/xd/samples/PerformanceTestsSpringExecutorChannel.xml", true, false);
		doTest("Ring buffer channel", "org/springframework/xd/samples/PerformanceTestsRingBufferChannel.xml", true, false);
		doTest("Reactor fixed thread pool executor channel", "org/springframework/xd/samples/PerformanceTestsReactorFixedThreadPoolExecutorChannel.xml", true, false);
		doTest("Reactor ring buffer executor channel", "org/springframework/xd/samples/PerformanceTestsReactorRingBufferExecutorChannel.xml", true, false);

		System.out.println("Sending a new message each time");
		doTest("Direct channel", "org/springframework/xd/samples/PerformanceTestsDirectChannel.xml", false, false);
		doTest("Spring executor channel", "org/springframework/xd/samples/PerformanceTestsSpringExecutorChannel.xml", false, false);
		doTest("Ring buffer channel", "org/springframework/xd/samples/PerformanceTestsRingBufferChannel.xml", false, false);
		doTest("Reactor fixed thread pool executor channel", "org/springframework/xd/samples/PerformanceTestsReactorFixedThreadPoolExecutorChannel.xml", false, false);
		doTest("Reactor ring buffer executor channel", "org/springframework/xd/samples/PerformanceTestsReactorRingBufferExecutorChannel.xml", false, false);

		System.out.println("Sending a new message each time using a custom id generator");
		doTest("Direct channel", "org/springframework/xd/samples/PerformanceTestsDirectChannel.xml", false, true);
		doTest("Spring executor channel", "org/springframework/xd/samples/PerformanceTestsSpringExecutorChannel.xml", false, true);
		doTest("Ring buffer channel", "org/springframework/xd/samples/PerformanceTestsRingBufferChannel.xml", false, true);
		doTest("Reactor fixed thread pool executor channel", "org/springframework/xd/samples/PerformanceTestsReactorFixedThreadPoolExecutorChannel.xml", false, true);
		doTest("Reactor ring buffer executor channel", "org/springframework/xd/samples/PerformanceTestsReactorRingBufferExecutorChannel.xml", false, true);
	}

	private void doTest(String description, String contextLocation, boolean sameMessage, boolean customIdGenerator) throws InterruptedException {
		String[] configLocations;
		if (customIdGenerator) {
			configLocations = new String[] {contextLocation, "org/springframework/xd/samples/PerformanceTestsCustomIdGenerator.xml"};
		} else {
			configLocations = new String[] {contextLocation};
		}

		ConfigurableApplicationContext applicationContext = new ClassPathXmlApplicationContext(configLocations);
		MessageChannel inputChannel = applicationContext.getBean("inputChannel",  MessageChannel.class);
		LatchDecrementingMessageHandler messageHandler = applicationContext.getBean(LatchDecrementingMessageHandler.class);

		long[] durations = new long[ITERATIONS];

		for (int i = 0; i < ITERATIONS; i++) {
			CountDownLatch latch = new CountDownLatch(MESSAGES);
			messageHandler.setLatch(latch);

			long start = System.currentTimeMillis();

			if (sameMessage) {
				Message<String> message = MessageBuilder.withPayload("test").build();

				for (int m = 0; m < MESSAGES; m++) {
					inputChannel.send(message);
				}
			} else {
				for (int m = 0; m < MESSAGES; m++) {
					inputChannel.send(MessageBuilder.withPayload("test").build());
				}
			}

			assertTrue(latch.await(60, TimeUnit.SECONDS));

			durations[i] = System.currentTimeMillis() - start;
		}

		long total = 0;
		for (int i = 0; i < ITERATIONS; i++) {
			total += durations[i];
		}

		double elapsed = total / ITERATIONS;

		System.out.println("\t" + description + " " + (int) ((MESSAGES) / (elapsed / 1000)));

		applicationContext.stop();
		applicationContext.close();
	}



}

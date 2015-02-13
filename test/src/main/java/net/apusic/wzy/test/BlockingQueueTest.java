package net.apusic.wzy.test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BlockingQueueTest {
	private static BlockingQueue<Integer> queue;
	ThreadPoolExecutor pe;
	
	public static void main(String[] args) throws InterruptedException {
		queue = new LinkedBlockingQueue<Integer>();
		queue.offer(1);
		queue.take();
		//queue.take();
		queue.poll(1000,TimeUnit.NANOSECONDS);
	}
}

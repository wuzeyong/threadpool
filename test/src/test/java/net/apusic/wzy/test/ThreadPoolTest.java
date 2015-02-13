package net.apusic.wzy.test;

import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ThreadPoolTest {
	static ThreadPool threadPool;
	static int corePoolSize = 4;
	static int maxPoolSize = 9;
	static int taskNum = 10;

	@Before
	public void setUp() throws Exception {
		threadPool = Executors.newThreadPool(corePoolSize, maxPoolSize);
	}

	@After
	public void tearDown() throws Exception {
		threadPool.close();
	}

	@Test
	public void testExecute() {
	}

	@Test
	public void testCloseNow() {
		List<Task> tasks = Task.createTasks(taskNum);
		for (Task task : tasks) {
			threadPool.execute(task);
		}
		List<Runnable> unCompletedTasks = threadPool.closeNow();
		//测试现在关闭后未完成的任务数
		int expected = taskNum - corePoolSize;
		int actual = unCompletedTasks.size();
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testClose() {
		List<Task> tasks = Task.createTasks(taskNum);
		for (Task task : tasks) {
			threadPool.execute(task);
		}
		threadPool.close();
		// 测试关闭后当前线程数
		int expectedThread = 0;
		int actualThread = threadPool.getPoolSize();
		//System.out.println(""+actualThread);
		Assert.assertEquals(expectedThread, actualThread);
		// 测试关闭后任务队列的数目
		int expectedTask = 0;
		int actualTask = threadPool.getWorkQueue().size();
		//System.out.println(actualTask);
		Assert.assertEquals(expectedTask, actualTask);
		// 测试关闭后完成的任务数
		int expectdCompletedTask = taskNum;
		int actualCompletedTask = threadPool.getCompletedTaskNum();
		Assert.assertEquals(expectdCompletedTask, actualCompletedTask);
	}

	@Test
	public void testSetMaxPoolSize() {
		List<Task> tasks = Task.createTasks(taskNum);
		for (Task task : tasks) {
			threadPool.execute(task);
		}
		int initMax = threadPool.getMaxPoolSize();
		int expectedMax = 2*initMax;
		threadPool.setMaxPoolSize(expectedMax);
		int actualMax = threadPool.getMaxPoolSize();
		Assert.assertEquals(expectedMax, actualMax);
	}

	@Test/*(expected=IllegalArgumentException.class)*/
	public void testSetCorePoolSize() throws InterruptedException {
		List<Task> tasks = Task.createTasks(taskNum);
		for (Task task : tasks) {
			threadPool.execute(task);
		}
		/*
		 //测试新corePoolSize < 旧corePoolSize
		 int expectedCore = 2*corePoolSize;
		threadPool.setCorePoolSize(expectedCore);
		int actualSize = threadPool.getPoolSize();
		System.out.println(actualSize);
		Assert.assertEquals(expectedCore, actualSize);*/
		
		//测试新corePoolSize > 旧corePoolSize
		int expectedCore = corePoolSize / 2;
		threadPool.setCorePoolSize(expectedCore);
		Thread.sleep(5000);
		int actualSize = threadPool.getPoolSize();
		System.out.println(actualSize);
		Assert.assertEquals(expectedCore, actualSize);
		
		//threadPool.setCorePoolSize(-1);
	}
}

package net.apusic.wzy.test;

import java.util.concurrent.BlockingQueue;


public class Executors {
	
	
	public static ThreadPool newThreadPool(int corePoolSize,int maxPoolSize){
		ThreadPool threadPool = new ThreadPool(corePoolSize, maxPoolSize);
		return threadPool;
	}
	
	public static ThreadPool newThreadPool(int corePoolSize,int maxPoolSize,BlockingQueue<Runnable> queue){
		ThreadPool threadPool = new ThreadPool(corePoolSize, maxPoolSize,queue);
		return threadPool;
	}
}

package net.apusic.wzy.test;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Task implements Runnable {
	private static final Logger logger = LoggerFactory
			.getLogger(ThreadPool.class);
	private String name;
	
	public Task(){}
	
	public Task(int name){
		this.name = "Task-"+name;
	}

	public void run() {
		System.out.println("Thread:"+Thread.currentThread().getName()+"执行: " + name + " : start");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		System.out.println("Thread:"+Thread.currentThread().getName()+"执行: " + name + " : end");
	}
	
	public static List<Task> createTasks(int taskNum){
		try {
			if(taskNum <= 0) 
				throw new IllegalArgumentException();
		} catch (IllegalArgumentException e) {
			logger.error("taskNum不能小于或等于0");
		}
		List<Task> tasks = new ArrayList<Task>();
		for(int i = 0 ;i <taskNum; i++){
			tasks.add(new Task(i));
		}
		return tasks;
	}
}

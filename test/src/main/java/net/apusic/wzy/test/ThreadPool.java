package net.apusic.wzy.test;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO:线程池
 * author						time					version			remark
 *--------------------------------------------------------------------------
 *吴泽勇		2014年12月15 13:41			1.0					~~
 */
public class ThreadPool extends ThreadGroup {
	private static final Logger logger = LoggerFactory
			.getLogger(ThreadPool.class);

	/**
	 * 线程池是否关闭
	 * */
	private boolean isClosed = false;

	/**
	 * 工作队列
	 * */
	private BlockingQueue<Runnable> workQueue;
	/**
	 * 线程池ID
	 * */
	private static int threadPoolID;

	/**
	 * 工作线程ID
	 * */
	private int threadID;

	/**
	 * 当前的线程数
	 * */
	private volatile int poolSize = 0;

	/**
	 * 核心线程数
	 * */
	private volatile int corePoolSize;

	/**
	 * 最大线程数
	 * */
	private int maxPoolSize;

	/**
	 * 线程池完成的任务数
	 * */
	private volatile int completedTaskNum;

	private HashSet<Worker> workers = new HashSet<Worker>();

	private static BlockingQueue<Runnable> defaultQueue = new LinkedBlockingQueue<Runnable>();

	/**
	 * @param poolSize
	 *            :线程数
	 */
	public ThreadPool(int corePoolSize, int maxPoolSize,
			BlockingQueue<Runnable> workQueue) {
		super("ThreadPool-" + (threadPoolID++));
		this.corePoolSize = corePoolSize;
		this.maxPoolSize = maxPoolSize;
		this.workQueue = workQueue;
	}

	public ThreadPool(int corePoolSize, int maxPoolSize) {
		this(corePoolSize, maxPoolSize, defaultQueue);
	}

	/**
	 * @param task表示新增的任务
	 * @throws Exception
	 * @see 向工作队列增加新任务，由工作线程去执行
	 * */
	public synchronized void execute(Runnable task) {
		// task出现空指针异常
		if (task == null) {
			logger.info("execute:任务出现空指针异常");
			throw new NullPointerException();
		}
		// 如果当前线程池大小小于核心线程大小，那么尝试创建新的执行线程，进行新任务的执行
		if (poolSize < corePoolSize) {
			if (addWorkerIfUnderCorePoolSize(task)) {
				return;
			}
		}
		// 因为当前线程未加锁，所以这里需要再次判断线程池状态
		if (!isClosed && workQueue.offer(task)) {
			 notify();
			// 如果当前线程已经关闭，并且已经没有活动线程了，需要对新加入队列，但尚未执行的任务进行处理
			// 因为当前线程未加锁，所以这里需要再次判断线程池状态
			if (isClosed || poolSize == 0) {
				ensureQueuedTaskHandled(task);
			}
			return;
		}
		// 如果执行到这里，表示当前线程池可能已经被关闭，或任务缓存队列已经填满 在线程数小于上限的情况下，尝试创建新的线程进行任务执行
		if (!addWorkerIfUnderMaximumPoolSize(task)) {
			reject(task);
		}
	}

	private void reject(Runnable task) {
		try {
			throw new Exception();
		} catch (Exception e) {
			logger.error("线程数超过上限,任务无法执行!");
		}
	}

	private  boolean addWorkerIfUnderMaximumPoolSize(
			Runnable firstTask) {
		if (!isClosed && poolSize < maxPoolSize) {
			Worker w = new Worker(firstTask);
			workers.add(w);
			w.start();
			++poolSize;
			return true;
		}
		return false;
	}

	/**
	 * @see 如果成功移除当前任务，任务被拒绝 如果当前任务无法移除，并且已经没有任务执行线程了，那么创建一个新的线程进行任务执行
	 * @param task
	 * */
	private  void ensureQueuedTaskHandled(Runnable task) {
		boolean reject = false;
		Worker w = null;
		if (isClosed && workQueue.remove(task))
			reject = true;
		else if (!isClosed && poolSize < corePoolSize && !workQueue.isEmpty()) {
			w = new Worker(task);
		}
		if (reject)
			reject(task);
		else if (w != null) {
			w.start();
			workers.add(w);
		}
	}

	/**
	 * @throws InterruptedException
	 * @see 从工作队列中取出任务 
	 * */
	protected synchronized  Runnable getTask() throws InterruptedException {
		Runnable task = null;
		while ((task = workQueue.poll()) == null) {
			if (isClosed)
				return null;
			//超出核心线程时,移除空闲时的工作线程
			if(poolSize > corePoolSize){
				for(Worker w:workers){
					if(Thread.currentThread() == w){
						w.interrupt();
					}
				}
			}
			wait();
		}
		return task;
	}

	/**
	 * @see 立即关闭线程池
	 * */
	public synchronized List<Runnable> closeNow() {
		List<Runnable> unCompletedTasks = null;
		if (!isClosed) {
			logger.info("close : 线程池关闭");
			isClosed = true;
			unCompletedTasks = clearWorkQueue();
			interrupt(); // 中断此线程组中的所有线程
		}
		return unCompletedTasks;
	}

	private List<Runnable> clearWorkQueue() {
		List<Runnable> taskList = new ArrayList<Runnable>();
		workQueue.drainTo(taskList);
		while (!workQueue.isEmpty()) {
			Iterator<Runnable> it = workQueue.iterator();
			try {
				if (it.hasNext()) {
					Runnable r = it.next();
					if (workQueue.remove(r))
						taskList.add(r);
				}
			} catch (ConcurrentModificationException ignore) {
			}
		}
		return taskList;
	}

	/**
	 * @see 等待工作线程把所有的任务执行完
	 * */
	public void close() {
		synchronized (this) {
			isClosed = true;
			notifyAll();
		}
		Thread[] threads = new Thread[activeCount()];
		// enumerate()方法继承自ThreadGroup类，获得线程组中所有活着的工作线程
		int count = enumerate(threads);
		for (int i = 0; i < count; i++) { // 等待所有工作线程 运行结束
			try {
				threads[i].join(); // 等待工作线程 运行结束
			} catch (InterruptedException e) {
				logger.error("join : 线程被打断");
			}
		}
	}

	/**
	 * 当线程数小于核心数,则增加工作线程
	 * */
	private  boolean addWorkerIfUnderCorePoolSize(Runnable firstTask) {
		if (!isClosed && poolSize < corePoolSize) {
			Worker worker = new Worker(firstTask);
			worker.start();
			workers.add(worker);
			++poolSize;
			return true;
		}
		return false;
	}

	/**
	 * 工作线程
	 * */
	private class Worker extends Thread {

		private Runnable firstTask;
		volatile long completedTasks;
		private final ReentrantLock runLock = new ReentrantLock();

		public Worker(Runnable firstTask) {
			// 加入当前ThreadPool的线程组中
			super(ThreadPool.this, "WorkThread-" + (threadID++));
			this.firstTask = firstTask;
		}

		public void run() {
			try {
				Runnable task = firstTask;
				firstTask = null;
				while (task != null || (task = getTask()) != null) {
					runTask(task);
					task = null;
				}
			} catch (InterruptedException e) {

			} finally {
				workerDone(this);
			}
		}

		private  void runTask(Runnable task) {
			final ReentrantLock runLock = this.runLock;
			runLock.lock();
			try {
				task.run();
				++completedTasks;
			} finally {
				runLock.unlock();
			}
		}

		//打断空闲的工作线程
		  void interruptIfIdle() {
			final ReentrantLock runLock = this.runLock;
			if (runLock.tryLock()) {
			try {
					if (this != Thread.currentThread())
						this.interrupt();
			} finally {
				runLock.unlock();
			}
			}
		}
	}

	/**
	 * 工作线程关闭时,计算线程池的总任务数:
	 * */
	synchronized void workerDone(Worker worker) {
		completedTaskNum += worker.completedTasks;
		workers.remove(worker);
		if (--poolSize == 0) {
			handleZeroThread();
		}
	}

	/**
	 * 当前线程数为0,队列不空的处理情况
	 * */
	private void handleZeroThread() {
		if (poolSize == 0) {
			if (!isClosed && !workQueue.isEmpty()) {
				Worker worker = new Worker(null);
				if (worker != null) {
					worker.start();
					workers.add(worker);
				}
			}
			if (isClosed && !workQueue.isEmpty()) {
				// 线程池关闭,但任务队列不为空
			}
		}
	}

	public synchronized void setCorePoolSize(int corePoolSize) {
		if (corePoolSize < 0){
			throw new IllegalArgumentException();
		}
		int extra = this.corePoolSize - corePoolSize;
		this.corePoolSize = corePoolSize;
		if (extra < 0) {
			int n = workQueue.size(); // don't add more threads than tasks
			while (extra++ < 0 && n-- > 0 && poolSize < corePoolSize) {
				Worker worker = new Worker(null);
				if (worker != null) {
					worker.start();
					++poolSize;
				} else
					break;
			}
		} else if (extra > 0 && poolSize > corePoolSize) {
			Iterator<Worker> it = workers.iterator();
			while (it.hasNext() && extra-- > 0 && poolSize > corePoolSize
			 && workQueue.remainingCapacity() == 0 )
				it.next().interruptIfIdle();
		}
	}

	public synchronized void setMaxPoolSize(int maxPoolSize) {
		if (maxPoolSize <= 0 || maxPoolSize < corePoolSize)
			throw new IllegalArgumentException();
		int extra = this.maxPoolSize - maxPoolSize;
		this.maxPoolSize = maxPoolSize;
		if (extra > 0 && poolSize > maxPoolSize) {
			Iterator<Worker> it = workers.iterator();
			while (it.hasNext() && extra > 0 && poolSize > maxPoolSize) {
				it.next().interruptIfIdle();
				--extra;
			}
		}
	}

	public synchronized int getPoolSize() {
		return poolSize;
	}

	public synchronized int getCorePoolSize() {
		return corePoolSize;
	}

	public synchronized int getMaxPoolSize() {
		return maxPoolSize;
	}

	public synchronized BlockingQueue<Runnable> getWorkQueue() {
		return workQueue;
	}

	public synchronized int getCompletedTaskNum() {
		return completedTaskNum;
	}

}

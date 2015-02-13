package net.apusic.wzy.test;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Test {
	private static Runnable createTask(final int taskID) {
		return new Runnable() {

			public void run() {
				
				System.out.println("Thrad:"+Thread.currentThread().getName()+"执行:Task " + taskID + ":start");
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				System.out.println("Thrad:"+Thread.currentThread().getName()+"执行:Task " + taskID + ":end");
			}
		};
	}

	public static void main(String[] args) {
	    try
        {
            InetAddress objAddr=InetAddress.getLocalHost();
            //获取和打印IP地址
            String sIP=objAddr.getHostAddress();
            System.out.println("本机的IP地址是："+sIP);
            //判断地址类型
            byte[] bAddr=objAddr.getAddress();
            if(bAddr.length==4)
                System.out.println("IP地址的版本是：IPv4");
            else if(bAddr.length==16)
                System.out.println("IP地址的版本是：IPv6");
        }
        catch (UnknownHostException err)
        {
            System.out.println("获取IP地址出现错误："+err);
        }
	    /*List<String> list = new ArrayList<String>();
	    list.add("AAA");
	    list.add("BBB");
	    list.add("CCC");
	    
	    System.out.println(StringUtils.join(list, ","));*/
		/*ThreadPoolExecutor tpe;
		int numTasks = 10;
		int corePoolSize =4;
		int maxPoolSize =6;
		final ThreadPool threadPool = new ThreadPool(corePoolSize, maxPoolSize,
				new LinkedBlockingQueue<Runnable>());
		for (int i = 0; i < numTasks; i++) {
			threadPool.execute(createTask(i));
		}
		threadPool.setCorePoolSize(-1);
		Thread t = new Thread(new Runnable(){

			public void run() {
				while(true){
					System.out.println("---------------------------------");
					System.out.println("当前线程数"+threadPool.getPoolSize());
					System.out.println("核心线程数"+threadPool.getCorePoolSize());
					System.out.println("完成任务数:"+threadPool.getCompletedTaskNum());
					System.out.println("---------------------------------");
				}
			}
		});
		t.start();
		System.out.println("AAAAAA");
		threadPool.close();
		System.out.println("BBBBBBBb");
		//List<Runnable> list = threadPool.closeNow();
		//System.out.println(list.size());
*/		
	}
}

package com.emotibot.jsEngine.executor;

public class Task implements Runnable{
	volatile boolean  running = true;
	int i = 0;
	@Override
	public void run() {
		while (running) {
			i++;
		}
	}
	public static void main(String[] args) throws InterruptedException {
		Task task = new Task();
		Thread th = new Thread(task);
		th.start();
		Thread.sleep(10);
		System.out.println(task.i);
		task.running=false;
		System.out.println(task.i);
		Thread.sleep(100);
		System.out.println(task.i);
		System.out.println("程序停止");
	}
}

package com.boredream.accessibilityservice.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskStepQueue {

    public static class TaskStep {
        private final CountDownLatch completionSignal;

        public TaskStep() {
            completionSignal = new CountDownLatch(1);
        }

        public void execute() {

        }

        /**
         * 挂起
         */
        public void await() throws InterruptedException {
            completionSignal.await();
        }

        /**
         * 完成
         */
        public void done() {
            completionSignal.countDown();
        }
    }

    private BlockingQueue<TaskStep> queue;

    public TaskStepQueue() {
        queue = new LinkedBlockingQueue<>();
    }

    public void addTask(TaskStep task) {
        queue.offer(task);
    }

    public void executeTasks() {
        while (!queue.isEmpty()) {
            TaskStep task = queue.peek();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            task.execute();

            // 等待任务完成信号
            try {
                task.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            queue.poll(); // 移除已完成的任务
        }
    }

}

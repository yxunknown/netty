/**
 * TimeServerHandlerExecutePool.class
 * Created in Intelij IDEA
 * <p>
 * Write Some Describe of this class here
 *
 * @author Mevur
 * @date 01/10/18 19:58
 */
package com.mevur.timeserver.nio;

import java.util.concurrent.*;

public class TimeServerHandlerExecutePool {
    private ExecutorService executorService;
    public TimeServerHandlerExecutePool(int maxPoolSize, int queueSize) {
        //构建线程池
        executorService = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                          maxPoolSize, 120L, TimeUnit.SECONDS,
                          new ArrayBlockingQueue<Runnable>(queueSize));
    }
    public void execute(Runnable task) {
        executorService.execute(task);
    }
}

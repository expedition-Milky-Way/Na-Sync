package com.deystar.Zip;

import java.io.File;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public class ZipExecutor extends ThreadPoolExecutor {
    public ZipExecutor(int coreNum, int maxCore, int queueSize) {
        super(coreNum, maxCore, 100, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<>(queueSize));
        this.setThreadFactory(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("compress--task--" + Thread.currentThread().getId());
                return t;
            }
        });
    }

    @Override
    public void execute(Runnable command) {
        super.execute(command);
    }
}

package eternal.walnut.reimu;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class InterruptingThreadPoolExecutor extends ThreadPoolExecutor {
    private Thread currentThread;

    public InterruptingThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        if (currentThread != null && currentThread != t && currentThread.isAlive())
            currentThread.interrupt();
        currentThread = t;
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        currentThread = null;
    }
}
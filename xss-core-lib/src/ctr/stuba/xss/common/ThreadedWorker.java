package ctr.stuba.xss.common;

import java.util.logging.Logger;

/**
 * Threaded worker.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
abstract public class ThreadedWorker extends Object implements Runnable {

    protected Thread workerThread = null;

    protected Logger getLogger() {
        return Logger.getLogger(this.getClass().getName());
    }

    /**
     * Spawns a thread and starts execution.
     */
    public void start() {
        this.workerThread = new Thread(this);
        this.workerThread.setName(this.getClass().getSimpleName());
        this.workerThread.start();
    }

    public void interrupt() {
        this.workerThread = null;
    }

    public boolean isRunning() {
        if (this.workerThread == null) {
            return false;
        }
        return this.workerThread.isAlive();
    }
}

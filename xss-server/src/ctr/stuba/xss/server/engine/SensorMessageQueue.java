package ctr.stuba.xss.server.engine;

import ctr.stuba.xss.common.ThreadedWorker;
import ctr.stuba.xss.event.EventAlreadyRecordedException;
import ctr.stuba.xss.sensor.message.SensorMessage;
import ctr.stuba.xss.sensor.message.SensorMessageQueueInterface;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Message queue to transfer messages from sensors to engine.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public class SensorMessageQueue extends ThreadedWorker implements SensorMessageQueueInterface {

    protected Queue<SensorMessage> queue = new ConcurrentLinkedQueue<>();
    protected final Object queueLock = new Object();
    protected final EngineWorker engine;

    public SensorMessageQueue(EngineWorker engine) {
        this.engine = engine;
    }

    @Override
    public void run() {
        try {
            while (true) {
                this.consume();
                synchronized (this.queueLock) {
                    this.queueLock.wait();
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Thread has been interrupted.", ex);
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void consume() throws EventAlreadyRecordedException {
        while (!this.queue.isEmpty()) {
            this.engine.parseSensorMessage(this.queue.poll());
        }
    }

    @Override
    public void offerMessage(SensorMessage message) {
        synchronized (this.queueLock) {
            this.queue.offer(message);
            this.queueLock.notifyAll();
        }
    }
}

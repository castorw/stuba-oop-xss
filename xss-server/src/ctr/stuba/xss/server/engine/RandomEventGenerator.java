package ctr.stuba.xss.server.engine;

import ctr.stuba.xss.cause.SensorAlarmCause;
import ctr.stuba.xss.common.ThreadedWorker;
import ctr.stuba.xss.sensor.OperationalStatus;
import ctr.stuba.xss.sensor.Sensor;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

/**
 * Random event generator. Randomly triggers events on sensors depending on
 * their random event probability.
 *
 * @author Lubomir Kaplan <castor@castor.sk>
 */
public class RandomEventGenerator extends ThreadedWorker {

    protected final EngineWorker engine;
    protected final int tickDelay;

    public RandomEventGenerator(EngineWorker engine, int tickDelay) {
        this.engine = engine;
        this.tickDelay = tickDelay;
    }

    @Override
    public void run() {
        Date currentDate = new Date();
        Random randomNumberGenerator = new Random(currentDate.getTime());
        try {
            while (true) {
                List<Sensor> sensorList = this.engine.getSensors();
                for (Sensor sensor : sensorList) {
                    if (sensor.getStatus() == OperationalStatus.UP && sensor.getDatabaseRecord().getType().equals("simulated")) {
                        int randomNum = randomNumberGenerator.nextInt(10000);
                        int probability = new Integer(sensor.getDatabaseRecord().getTriggerRegProbability().multiply(new BigDecimal(10000)).toBigInteger().toString());
                        if (randomNum <= probability) {
                            sensor.emulateAlarm(SensorAlarmCause.SIMULATED_RANDOM_EVENT);
                        }
                    }
                }
                Thread.sleep(this.tickDelay);
            }
        } catch (InterruptedException ex) {
            this.getLogger().log(Level.SEVERE, "Thread has been interrupted.", ex);
        }
    }
}
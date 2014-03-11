package ctr.stuba.xss.sensor.impl;

import ctr.stuba.xss.cause.Cause;
import ctr.stuba.xss.cause.SensorAlarmCause;
import ctr.stuba.xss.comm.psp.PhysicalSensorCommands;
import ctr.stuba.xss.hbm.XssSensor;
import ctr.stuba.xss.sensor.PhysicalSensor;
import ctr.stuba.xss.sensor.message.SensorMessageQueueInterface;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.SessionFactory;

public class PhysicalMotionSensor extends PhysicalSensor {
    
    public PhysicalMotionSensor(SessionFactory mysqlSessionFactory, SensorMessageQueueInterface messageQueue, XssSensor databaseRecord) {
        super(mysqlSessionFactory, messageQueue, databaseRecord);
        this.setValue(new Integer(0));
    }
    
    @Override
    protected void loop() {
        try {
            if (this.deviceInputStream.available() > 0) {
                while (this.deviceInputStream.available() > 0) {
                    int readCmd = this.deviceInputStream.read();
                    if (readCmd == PhysicalSensorCommands.cmdAlarm) {
                        int readType = this.deviceInputStream.read();
                        if (readType == (int) (0x01)) {
                            this.triggerAlarm(SensorAlarmCause.REAL_EVENT);
                        } else {
                            this.triggerTamper(SensorAlarmCause.REAL_TAMPER_EVENT);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(PhysicalMotionSensor.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            /*
             if (this.deviceSocket.isConnected()) {
             try {
             this.deviceOutputStream.write(PhysicalSensorCommands.cmdClose);
             this.deviceOutputStream.flush();
             } catch (IOException ex) {
             Logger.getLogger(PhysicalMotionSensor.class.getName()).log(Level.SEVERE, null, ex);
             }
             }
             */
        }
    }
    
    @Override
    public void emulateAlarm(Cause cause) {
    }
}

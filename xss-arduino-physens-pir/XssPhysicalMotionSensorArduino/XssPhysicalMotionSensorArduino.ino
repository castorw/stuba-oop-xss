#include <SPI.h>
#include <Ethernet.h>

/*
 * ----------------------------------------------------------
 *   Configuration
 * ---------------------------------------------------------- 
 */

// device mac address
byte mac[] = {  0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };


// XSS server IP address
IPAddress xssServer(0, 0, 0, 0);

// XSS server TCP port
unsigned int xssPort = 22334;

// XSS physical server key
char key[] = “longstringusedforauthentication”;

// (do not modify) Status LED pin ID
int pinStatusLed = 9;
// (do not modify) PIR trigger loop pin ID
int pinTriggerLoop = 8;
// (do not modify) TAMPER trigger loop pin ID
int pinTamperLoop = 7;

/*
 * ----------------------------------------------------------
 *     Internal constants
 *    ( DO NOT MODIFY BELOW THIS LINE )
 * ---------------------------------------------------------- 
 */
byte cmdGetKey = 0xa0;        // (server -> sensor) XSS PS key request
byte cmdSendKey = 0xa1;       // (sensor -> server) XSS PS key response
byte cmdAuthOk = 0xa2;        // (server -> sensor) Authentication OK response
byte cmdAuthFail = 0xa3;      // (server -> sensor) Authentication FAILED response
byte cmdKeepalive = 0xa4;     // (server <-> sensor) Keepalive
byte cmdAlarm = 0xa5;         // (sensor -> server) Alarm
byte cmdClose = 0xa6;         // (server -> sensor) Connection close

byte alarmNormal = 0x01;      // PIR alarm byte
byte alarmTamper = 0x02;      // TAMPER alarm byte

byte sInit = 0x01;            // Initialization status
byte sRunning = 0x02;         // Normal operation status

unsigned int keyLength = 32;  // Length of XSS PS key

/*
 * ----------------------------------------------------------
 *   Application code
 * ---------------------------------------------------------- 
 */
EthernetClient ethClient;
byte sysStatus = sInit;

void blinkStatusLed(unsigned int count, unsigned int length, unsigned int edelay) {
  for (unsigned int i = 0; i < count; i++) {
    digitalWrite(pinStatusLed, HIGH);
    delay(length/2);
    digitalWrite(pinStatusLed, LOW);
    delay(length/2);
  }
  delay(edelay);
}

void blinkStatusLedHalted(unsigned int count, unsigned int length) {
  for (;;) {
    blinkStatusLed(count, length, 3000);
  }
}

void pspBeginEthernet() {
  while (Ethernet.begin(mac) == 0) {
    blinkStatusLed(2, 600, 1000);
  }
}

void pspConnect() {
  sysStatus = sInit;
  while (sysStatus != sRunning) {
    delay(5000);
    ethClient.stop();
    while (!ethClient.connect(xssServer, xssPort)) {
      blinkStatusLed(3, 600, 1000);
    }
    byte recvByte;
    
    while (!ethClient.available()) {}
    recvByte = ethClient.read();
    
    if (recvByte == cmdGetKey) {
      ethClient.write(cmdSendKey);
      for (unsigned int i = 0; i < keyLength; i++) {
        ethClient.write(key[i]);
      }
      ethClient.flush();
      
      while (!ethClient.available()) {}
      recvByte = ethClient.read();
      if (recvByte == cmdAuthOk) {
        sysStatus = sRunning;
      }
    }
  }
}

void pspSendAlarm(byte alarmType) {
  ethClient.write(cmdAlarm);
  ethClient.write(alarmType);
  ethClient.flush();
}

void pspRead() {
  if (ethClient.available()) {
    while (ethClient.available()) {
      byte readByte = ethClient.read();
      if (readByte == cmdClose) {
        ethClient.stop();
      }
    }
  }
}

boolean ckAlarmNormal() {
  return digitalRead(pinTriggerLoop);
}

boolean ckAlarmTamper() {
  return digitalRead(pinTamperLoop);
}

void setup() {
  pinMode(pinStatusLed, OUTPUT);
  pinMode(pinTriggerLoop, OUTPUT);
  pinMode(pinTamperLoop, OUTPUT);
  blinkStatusLed(1, 200, 1000);  
  
  pspBeginEthernet();
  blinkStatusLed(2, 200, 1000);

  pspConnect();
  blinkStatusLed(3, 200, 1000);
}

void loop() {
  if (ethClient.connected()) {
    if (ckAlarmNormal() == false) {
      pspSendAlarm(alarmNormal);
      blinkStatusLed(10, 100, 1000);
    }
    if (ckAlarmTamper() == false) {
      pspSendAlarm(alarmTamper);
      blinkStatusLed(10, 100, 1000);
    }
    pspRead();
    blinkStatusLed(1, 200, 0);
    delay(300);
  } else {
    pspConnect();
  }
}

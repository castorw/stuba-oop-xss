# ************************************************************
# Sequel Pro SQL dump
# Version 4096
#
# http://www.sequelpro.com/
# http://code.google.com/p/sequel-pro/
#
# Host: 127.0.0.1 (MySQL 5.1.66-0+squeeze1)
# Database: xss_stuba
# Generation Time: 2014-03-11 17:29:21 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table xss_event
# ------------------------------------------------------------

CREATE TABLE `xss_event` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `event` varchar(255) NOT NULL DEFAULT '',
  `cause` varchar(255) NOT NULL,
  `sensor_id` int(11) DEFAULT NULL,
  `message` text,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table xss_sensor
# ------------------------------------------------------------

CREATE TABLE `xss_sensor` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` text,
  `type` enum('simulated','physical') NOT NULL DEFAULT 'simulated',
  `xssps_key` varchar(64) DEFAULT NULL,
  `trigger_type` varchar(255) NOT NULL DEFAULT 'ctr.stuba.xss.sensor.NullSensor',
  `trigger_reg_probability` decimal(5,4) NOT NULL DEFAULT '0.0030',
  `admin_status` int(1) NOT NULL DEFAULT '1',
  `auto_toggle` int(1) NOT NULL DEFAULT '0',
  `auto_enable_time` int(11) DEFAULT NULL,
  `auto_disable_time` int(11) DEFAULT NULL,
  `last_status` varchar(255) DEFAULT NULL,
  `last_status_change_time` timestamp NULL DEFAULT NULL,
  `last_trigger_time` timestamp NULL DEFAULT NULL,
  `last_tamper_time` timestamp NULL DEFAULT NULL,
  `last_modify_time` timestamp NULL DEFAULT NULL,
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `xss_sensor` WRITE;
/*!40000 ALTER TABLE `xss_sensor` DISABLE KEYS */;

INSERT INTO `xss_sensor` (`id`, `name`, `description`, `type`, `xssps_key`, `trigger_type`, `trigger_reg_probability`, `admin_status`, `auto_toggle`, `auto_enable_time`, `auto_disable_time`, `last_status`, `last_status_change_time`, `last_trigger_time`, `last_tamper_time`, `last_modify_time`, `created_time`)
VALUES
	(5,'Test RS','Physical motion sensor test','physical','p4RVC1O76C7cs5KP54nuZ2T3jDngd7Gn','ctr.stuba.xss.sensor.impl.PhysicalMotionSensor',0.0000,1,0,NULL,NULL,'ctr.stuba.xss.sensor.OperationalStatus.OPERATIONALLY_DOWN','2013-05-20 13:22:14','2013-05-06 13:29:40','2013-05-06 13:08:09',NULL,'2013-03-13 17:18:58'),
	(6,'PIR Bedroom','PIR Motion Sensor\nLocation: Bedroom','simulated',NULL,'ctr.stuba.xss.sensor.impl.SimulatedMotionSensor',0.0002,1,1,18615,80000,'ctr.stuba.xss.sensor.OperationalStatus.UP','2013-06-11 05:10:15','2013-06-11 14:16:28',NULL,NULL,'2013-04-22 00:32:43'),
	(7,'DOOR Main Entrance','Magnetic Door Sensor\nLocation: Main entrance','simulated',NULL,'ctr.stuba.xss.sensor.impl.SimulatedDoorSensor',0.0008,1,0,NULL,NULL,'ctr.stuba.xss.sensor.OperationalStatus.UP','2013-05-20 13:22:14','2013-06-11 20:20:02',NULL,NULL,'2013-04-22 00:33:15'),
	(8,'DOOR Backyard','Magnetic Door Sensor\nLocation: Backyard Door','simulated',NULL,'ctr.stuba.xss.sensor.impl.SimulatedDoorSensor',0.0001,1,0,NULL,NULL,'ctr.stuba.xss.sensor.OperationalStatus.UP','2013-05-20 13:22:14','2013-06-11 19:57:41',NULL,NULL,'2013-04-22 00:33:50'),
	(16,'TEMP Inside','Internal temperature sensor','simulated',NULL,'ctr.stuba.xss.sensor.impl.SimulatedTemperatureSensor',0.0010,1,0,0,0,'ctr.stuba.xss.sensor.OperationalStatus.UP','2013-05-20 13:22:14','2013-06-11 20:15:11',NULL,NULL,'2013-05-20 11:36:05');

/*!40000 ALTER TABLE `xss_sensor` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table xss_user
# ------------------------------------------------------------

CREATE TABLE `xss_user` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL DEFAULT '',
  `password` varchar(255) NOT NULL DEFAULT '',
  `enabled` int(1) NOT NULL DEFAULT '0',
  `last_login_time` timestamp NULL DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

LOCK TABLES `xss_user` WRITE;
/*!40000 ALTER TABLE `xss_user` DISABLE KEYS */;

INSERT INTO `xss_user` (`id`, `username`, `password`, `enabled`, `last_login_time`, `create_time`)
VALUES
	(1,'admin','4015bc9ee91e437d90df83fb64fbbe312d9c9f05',1,'0000-00-00 00:00:00','2013-03-16 02:24:58');

/*!40000 ALTER TABLE `xss_user` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

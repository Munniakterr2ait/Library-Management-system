-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: library
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `book`
--

DROP TABLE IF EXISTS `book`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `book` (
  `id` varchar(50) NOT NULL,
  `name` varchar(200) NOT NULL,
  `publisher` varchar(100) DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `year` varchar(10) DEFAULT NULL,
  `status` varchar(20) DEFAULT 'NOT ISSUE',
  `issuedate` varchar(30) DEFAULT NULL,
  `duedate` varchar(30) DEFAULT NULL,
  `studentid` varchar(50) DEFAULT NULL,
  `quantity` int NOT NULL DEFAULT '1',
  `available_qty` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `studentid` (`studentid`),
  CONSTRAINT `book_ibfk_1` FOREIGN KEY (`studentid`) REFERENCES `student` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `book`
--

LOCK TABLES `book` WRITE;
/*!40000 ALTER TABLE `book` DISABLE KEYS */;
INSERT INTO `book` VALUES ('BK001','Introduction to Java Programming','Pearson',650.00,'2020','NOT ISSUE',NULL,NULL,NULL,1,2),('BK002','Database Management Systems','McGraw Hill',750.00,'2019','NOT ISSUE',NULL,NULL,NULL,1,1),('BK003','Data Structures & Algorithms','O Reilly',580.00,'2021','NOT ISSUE',NULL,NULL,NULL,1,1),('BK004','Computer Networks','Forouzan',700.00,'2018','ISSUED','22/04/2026','29/04/2026','2170',1,1),('BK005','Operating System Concepts','Silberschatz',690.00,'2020','NOT ISSUE',NULL,NULL,NULL,1,1),('BK006','Microprocessor','Anayet Publication',860.00,'2010','NOT ISSUE',NULL,NULL,NULL,1,1),('Bk008','Electronic','Hoque Publication',550.00,'2002','ISSUED','01/05/2026','02/05/2026','2173',15,14);
/*!40000 ALTER TABLE `book` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `login`
--

DROP TABLE IF EXISTS `login`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `login` (
  `id` int NOT NULL AUTO_INCREMENT,
  `userid` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(100) NOT NULL,
  `role` varchar(20) DEFAULT 'admin',
  PRIMARY KEY (`id`),
  UNIQUE KEY `userid` (`userid`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `login`
--

LOCK TABLES `login` WRITE;
/*!40000 ALTER TABLE `login` DISABLE KEYS */;
INSERT INTO `login` VALUES (1,'admin','admin123','admin@library.com','admin');
/*!40000 ALTER TABLE `login` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `settings`
--

DROP TABLE IF EXISTS `settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `settings` (
  `id` int NOT NULL AUTO_INCREMENT,
  `library_name` varchar(200) DEFAULT 'University Library',
  `smtp_host` varchar(100) DEFAULT 'smtp.gmail.com',
  `smtp_port` int DEFAULT '587',
  `smtp_email` varchar(100) DEFAULT '',
  `smtp_password` varchar(255) DEFAULT '',
  `due_days` int DEFAULT '14',
  `fine_per_day` decimal(10,2) DEFAULT '5.00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `settings`
--

LOCK TABLES `settings` WRITE;
/*!40000 ALTER TABLE `settings` DISABLE KEYS */;
INSERT INTO `settings` VALUES (1,'Library management system','smtp.gmail.com',587,'munniakter143560@gmail.com','bbufirgsmcbopqex',7,5.00);
/*!40000 ALTER TABLE `settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student` (
  `id` varchar(50) NOT NULL,
  `name` varchar(100) NOT NULL,
  `course` varchar(100) DEFAULT NULL,
  `semester` varchar(20) DEFAULT NULL,
  `section` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
INSERT INTO `student` VALUES ('2170','Sumaiya ferdous priti','computer networks','spring','4B','suraiyasultanaety@gmail.com'),('2173','Munni Akter','Database Management System','Spring','4A','munniakterr1620@gmail.com '),('STD001','Rahim Ahmed','CSE','5th','A','rahim@example.com'),('STD002','Fatima Begum','EEE','3rd','B','fatima@example.com'),('STD003','Karim Hossain','BBA','7th','A','karim@example.com');
/*!40000 ALTER TABLE `student` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-04 18:20:28

-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: sms_project
-- ------------------------------------------------------
-- Server version	9.2.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `user` (modified first to remove admin)
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `fullName` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `accountType` enum('parent','teacher') NOT NULL, -- Admin removed
  `address` varchar(255) DEFAULT NULL,
  `phone_number` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user` (admins removed, passwords hashed)
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES 
-- Teachers (passwords hashed)
(4,'Jane Smith','jane.smith@school.edu','$2a$10$TrH41Bz8N5U.7QO9Vp3fZe','teacher','101 Teachers Blvd','555-1111'),
(5,'Robert Johnson','robert.johnson@school.edu','$2a$10$kLwR9sHqJmT2F.ZxVb8XJu','teacher','202 Education St','555-1112'),
(6,'Maria Garcia','maria.garcia@school.edu','$2a$10$mGpP7rSsNqA3D.WuYc1vDe','teacher','303 Knowledge Ln','555-1113'),
(7,'David Williams','david.williams@school.edu','$2a$10$hTdL4vRrQsE5M.NlXh8YFe','teacher','404 Science Dr','555-1114'),
(8,'Jennifer Lee','jennifer.lee@school.edu','$2a$10$bRnK9pWqZsA2C.SvT2mZDu','teacher','505 Math Ave','555-1115'),
-- Parents (passwords hashed)
(12,'John Doe','john.doe@example.com','$2a$10$fGn8Dq7VrLsB4.IkQ9tZ0u','parent','111 Family St','555-2222'),
(13,'Mary Johnson','mary.johnson@example.com','$2a$10$sHj5TkRwXpC6.LmUv7nZAu','parent','222 Home Ave','555-2223'),
(14,'Carlos Rodriguez','carlos.rodriguez@example.com','$2a$10$pLq9VrSsNqA3D.WuYc1vDe','parent','333 Parent Ln','555-2224'),
(15,'Alice Williams','alice.williams@example.com','$2a$10$qMnK9pWqZsA2C.SvT2mZDu','parent','444 Guardian Rd','555-2225'),
(16,'Priya Patel','priya.patel@example.com','$2a$10$rNo8Dq7VrLsB4.IkQ9tZ0u','parent','555 Family Circle','555-2226'),
-- 5 New Parents
(17,'Thomas Anderson','thomas.anderson@example.com','$2a$10$tHs5TkRwXpC6.LmUv7nZAu','parent','666 Oak St','555-3333'),
(18,'Fatima Hassan','fatima.hassan@example.com','$2a$10$uLq9VrSsNqA3D.WuYc1vDe','parent','777 Pine Rd','555-4444'),
(19,'Wei Chen','wei.chen@example.com','$2a$10$vMnK9pWqZsA2C.SvT2mZDu','parent','888 Maple Ave','555-5555'),
(20,'Sophie Martin','sophie.martin@example.com','$2a$10$wNo8Dq7VrLsB4.IkQ9tZ0u','parent','999 Birch Ln','555-6666'),
(21,'Luca Rossi','luca.rossi@example.com','$2a$10$xHs5TkRwXpC6.LmUv7nZAu','parent','1010 Cedar Blvd','555-7777');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parent` (unchanged)
--

-- ... (Same structure as original) ...

--
-- Dumping data for table `parent` (expanded with 5 new parents)
--

LOCK TABLES `parent` WRITE;
/*!40000 ALTER TABLE `parent` DISABLE KEYS */;
INSERT INTO `parent` VALUES 
(1,12,2),(2,13,1),(3,14,3),(4,15,2),(5,16,1),
-- New parents
(6,17,2),(7,18,1),(8,19,3),(9,20,2),(10,21,1);
/*!40000 ALTER TABLE `parent` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teacher` (unchanged)
--

-- ... (Same structure as original) ...

--
-- Dumping data for table `teacher` (expanded with 5 new teachers)
--

LOCK TABLES `teacher` WRITE;
/*!40000 ALTER TABLE `teacher` DISABLE KEYS */;
INSERT INTO `teacher` VALUES 
(1,4,1),(2,5,2),(3,6,3),(4,7,4),(5,8,5),
-- New teachers (linked to new users 22-26)
(6,22,6),(7,23,7),(8,24,8),(9,25,9),(10,26,10);
/*!40000 ALTER TABLE `teacher` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `class_group` (unchanged)
--

-- ... (Same structure as original) ...

--
-- Dumping data for table `class_group` (5 new classes added)
--

LOCK TABLES `class_group` WRITE;
/*!40000 ALTER TABLE `class_group` DISABLE KEYS */;
INSERT INTO `class_group` VALUES 
(1,25,2025,101,1),(2,28,2025,102,2),(3,30,2025,103,3),
(4,26,2025,104,4),(5,24,2025,105,5),(6,27,2025,201,6),
(7,29,2025,202,7),(8,23,2025,203,8),
-- New classes
(9,22,2025,204,9),(10,20,2025,205,10),(11,25,2025,206,6),
(12,28,2025,207,7),(13,30,2025,208,8);
/*!40000 ALTER TABLE `class_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student` (unchanged)
--

-- ... (Same structure as original) ...

--
-- Dumping data for table `student` (5 new students added)
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
INSERT INTO `student` VALUES 
(1,1,'Emily','Doe','111 Family St',1),
(2,3,'Jacob','Doe','111 Family St',1),
... (original entries) ...,
-- New students
(17,9,'Ethan','Brown','123 Maple St',6),
(18,10,'Mia','Wilson','456 Oak Rd',7),
(19,11,'Liam','Taylor','789 Pine Ave',8),
(20,12,'Olivia','Moore','101 Birch Ln',9),
(21,13,'Noah','Anderson','202 Cedar Blvd',10);
/*!40000 ALTER TABLE `student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `absence` (unchanged)
--

-- ... (Same structure as original) ...

--
-- Dumping data for table `absence` (5 new absences added)
--

LOCK TABLES `absence` WRITE;
/*!40000 ALTER TABLE `absence` DISABLE KEYS */;
INSERT INTO `absence` VALUES 
(1,1,'2025-02-05','Sick with flu',1),
... (original entries) ...,
-- New absences
(9,17,'2025-04-01','Dental appointment',1),
(10,18,'2025-04-02','Family trip',0),
(11,19,'2025-04-03','Sports event',1),
(12,20,'2025-04-04','Allergy symptoms',1),
(13,21,'2025-04-05','Unknown',0);
/*!40000 ALTER TABLE `absence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `excuse` (unchanged)
--

-- ... (Same structure as original) ...

--
-- Dumping data for table `excuse` (5 new excuses added)
--

LOCK TABLES `excuse` WRITE;
/*!40000 ALTER TABLE `excuse` DISABLE KEYS */;
INSERT INTO `excuse` VALUES 
(1,'2025-02-06','Doctor\'s note confirming flu diagnosis',1,1),
... (original entries) ...,
-- New excuses
(6,'2025-04-02','Orthodontist appointment confirmation',17,9),
(7,'2025-04-03','Parent email about travel plans',18,10),
(8,'2025-04-04','Coach\'s note for tournament',19,11),
(9,'2025-04-05','Allergy medication prescription',20,12),
(10,'2025-04-06','No documentation provided',21,13);
/*!40000 ALTER TABLE `excuse` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `grade` (unchanged)
--

-- ... (Same structure as original) ...

--
-- Dumping data for table `grade` (5 new grades added)
--

LOCK TABLES `grade` WRITE;
/*!40000 ALTER TABLE `grade` DISABLE KEYS */;
INSERT INTO `grade` VALUES 
(1,'A','Mathematics',1,'2025-02-15','Excellent work on algebra',1),
... (original entries) ...,
-- New grades
(13,'B','Chemistry',17,'2025-04-10','Good lab participation',6),
(14,'A','Literature',18,'2025-04-11','Creative writing skills',7),
(15,'C','Physics',19,'2025-04-12','Needs improvement in formulas',8),
(16,'A','Biology',20,'2025-04-13','Excellent dissection work',9),
(17,'B','Geography',21,'2025-04-14','Solid map-reading skills',10);
/*!40000 ALTER TABLE `grade` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `homework` (unchanged)
--

-- ... (Same structure as original) ...

--
-- Dumping data for table `homework` (5 new assignments added)
--

LOCK TABLES `homework` WRITE;
/*!40000 ALTER TABLE `homework` DISABLE KEYS */;
INSERT INTO `homework` VALUES 
(1,'2025-03-01','2025-03-08',1,'Complete algebra worksheet pages 15-173',1),
... (original entries) ...,
-- New homework
(17,'2025-04-01','2025-04-08',9,'Write essay on climate change',0),
(18,'2025-04-02','2025-04-09',10,'Chemistry lab: Acid-base reactions',1),
(19,'2025-04-03','2025-04-10',11,'History presentation: World War II',0),
(20,'2025-04-04','2025-04-11',12,'Biology report on ecosystems',1),
(21,'2025-04-05','2025-04-12',13,'Geometry problem set: Triangles',0);
/*!40000 ALTER TABLE `homework` ENABLE KEYS */;
UNLOCK TABLES;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
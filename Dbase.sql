-- MySQL dump 10.13  Distrib 8.0.39, for Win64 (x86_64)
--
-- Host: localhost    Database: hospital_management
-- ------------------------------------------------------
-- Server version	8.0.39

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
-- Table structure for table `appointments`
--

DROP TABLE IF EXISTS `appointments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `appointments` (
  `appointment_id` int NOT NULL AUTO_INCREMENT,
  `patient_id` varchar(255) NOT NULL,
  `doctor_id` varchar(255) NOT NULL,
  `appointment_date` date NOT NULL,
  `appointment_time` time NOT NULL,
  `specialty` varchar(255) DEFAULT NULL,
  `consultation_fee` decimal(10,2) DEFAULT NULL,
  `status` varchar(50) DEFAULT 'Scheduled',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `doctor_name` varchar(255) DEFAULT NULL,
  `hospital_id` varchar(255) DEFAULT NULL,
  `patient_age` int DEFAULT NULL,
  `patient_sex` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`appointment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `appointments`
--

LOCK TABLES `appointments` WRITE;
/*!40000 ALTER TABLE `appointments` DISABLE KEYS */;
/*!40000 ALTER TABLE `appointments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `category_name` varchar(255) NOT NULL,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `category_name` (`category_name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'Blood Work'),(3,'ECG'),(2,'Imaging'),(5,'Other Tests'),(4,'Urinalysis');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `consultation`
--

DROP TABLE IF EXISTS `consultation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `consultation` (
  `id` int NOT NULL AUTO_INCREMENT,
  `patient_name` varchar(100) NOT NULL,
  `age` int DEFAULT NULL,
  `gender` enum('Male','Female','Other') NOT NULL,
  `previous_consultation_date` datetime DEFAULT NULL,
  `current_consultation_date` datetime NOT NULL,
  `chief_complaint` text,
  `medical_history` set('No significant medical history','Chronic conditions','Previous surgeries/hospitalizations') DEFAULT NULL,
  `medications` text,
  `allergies` text,
  `family_history` text,
  `social_history` set('Non-smoker','Smoker','Alcohol use','Drug use') DEFAULT NULL,
  `review_of_systems` set('Fever','Weight loss','Fatigue','Night sweats','Chest pain','Palpitations','Shortness of breath','Cough','Wheezing','Sputum production','Nausea','Vomiting','Abdominal pain','Diarrhea','Joint pain','Muscle weakness','Back pain','Headache','Dizziness','Numbness','Seizures','Rashes','Itching','Skin lesions','Anxiety','Depression','Sleep disturbances') DEFAULT NULL,
  `bp` varchar(20) DEFAULT NULL,
  `pulse` int DEFAULT NULL,
  `temperature` decimal(4,1) DEFAULT NULL,
  `respiratory_rate` int DEFAULT NULL,
  `physical_exam` set('Well-nourished','Ill-appearing','In distress','Normal findings','Swelling','Tenderness','Lymphadenopathy') DEFAULT NULL,
  `investigations_blood_work` set('Complete Blood Count CBC','Electrolytes Na K Cl HCO3','Liver Function Tests LFTs','Renal Function Tests RFTs','Blood Glucose Level Fasting Random','Lipid Profile','Thyroid Function Tests TFTs','Coagulation Profile PT aPTT INR','Hemoglobin A1c','Arterial Blood Gas ABG','Vitamin and Mineral Levels D B12 Ca Mg','C-Reactive Protein CRP ESR') DEFAULT NULL,
  `investigations_imaging` set('Chest X-ray','Abdominal X-ray','Bone X-ray Specific Area','Abdominal Ultrasound','Pelvic Ultrasound','Renal Ultrasound','Carotid Doppler','CT Scan Chest Abdomen Pelvis Brain','MRI Brain Spine Musculoskeletal') DEFAULT NULL,
  `investigations_ecg` set('Electrocardiogram ECG','Echocardiogram ECHO','Stress Test Treadmill Pharmacological','Holter Monitor 24-hour ECG') DEFAULT NULL,
  `investigations_urinalysis` set('Routine Urinalysis','Urine Culture','24-hour Urine Collection','Urine Pregnancy Test','Urine Dipstick Glucose Blood Ketones') DEFAULT NULL,
  `investigations_other` set('Blood Cultures','Sputum Culture','Throat Culture','Stool Culture','Gastroscopy EGD','Colonoscopy','Bronchoscopy','Biopsy Tissue Sampling','HIV Screening','Hepatitis Panel','Tuberculosis Test PPD or Chest X-ray','Autoimmune Screen ANA RF','Genetic Testing','D-dimer','Prostate-Specific Antigen PSA','Pregnancy Test Urine or Serum','Arterial Blood Gas ABG') DEFAULT NULL,
  `primary_diagnosis` varchar(255) DEFAULT NULL,
  `secondary_diagnosis` varchar(255) DEFAULT NULL,
  `treatment_recommendations` text,
  `next_follow_up` date DEFAULT NULL,
  `doctor_name` varchar(100) DEFAULT NULL,
  `consultation_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_patient_name` (`patient_name`),
  KEY `idx_doctor_name` (`doctor_name`),
  CONSTRAINT `consultation_chk_1` CHECK ((`age` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consultation`
--

LOCK TABLES `consultation` WRITE;
/*!40000 ALTER TABLE `consultation` DISABLE KEYS */;
INSERT INTO `consultation` VALUES (1,'Raymund',3,'Male','2020-11-10 00:00:00','2024-11-13 14:49:29','','','','','','','',NULL,0,NULL,0,'','','','','','','','','as','2022-01-12','','2024-11-13 06:49:29');
/*!40000 ALTER TABLE `consultation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `diagnosis`
--

DROP TABLE IF EXISTS `diagnosis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `diagnosis` (
  `diagnosis_id` int NOT NULL AUTO_INCREMENT,
  `patient_id` int DEFAULT NULL,
  `primary_diagnosis` varchar(255) DEFAULT NULL,
  `secondary_diagnosis` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`diagnosis_id`),
  KEY `patient_id` (`patient_id`),
  CONSTRAINT `diagnosis_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patient_information` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `diagnosis`
--

LOCK TABLES `diagnosis` WRITE;
/*!40000 ALTER TABLE `diagnosis` DISABLE KEYS */;
/*!40000 ALTER TABLE `diagnosis` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doctors`
--

DROP TABLE IF EXISTS `doctors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doctors` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `specialist` varchar(100) NOT NULL,
  `room` varchar(10) NOT NULL,
  `patient_consult` varchar(20) NOT NULL,
  `availability` varchar(20) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `max_patients_per_day` int NOT NULL,
  `max_patients_per_slot` int DEFAULT '0',
  `slot_duration` int DEFAULT '0',
  `availability_details` varchar(255) DEFAULT NULL,
  `lunch_break` varchar(255) DEFAULT NULL,
  `telemedicine` tinyint(1) DEFAULT '0',
  `emergency_appointments` tinyint(1) DEFAULT '0',
  `day_of_week` varchar(20) DEFAULT NULL,
  `from_time` time DEFAULT NULL,
  `to_time` time DEFAULT NULL,
  `lunch_start` time DEFAULT NULL,
  `lunch_end` time DEFAULT NULL,
  `available` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_doctor_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctors`
--

LOCK TABLES `doctors` WRITE;
/*!40000 ALTER TABLE `doctors` DISABLE KEYS */;
INSERT INTO `doctors` VALUES (1,'Asss','Allergist/Immunologist','AL1','','','2024-11-02 03:16:29','2024-11-20 01:03:53',0,0,0,NULL,NULL,0,0,NULL,NULL,NULL,NULL,NULL,NULL),(2,'Raymund Estaca','Cardiologist','CA2','0 / 5','Available','2024-11-13 01:47:49','2024-11-13 01:47:49',0,0,0,NULL,NULL,0,0,NULL,NULL,NULL,NULL,NULL,NULL),(3,'asd','Allergist/Immunologist','AL1','0 / 2','Available','2024-11-13 08:01:41','2024-11-13 08:01:41',0,0,0,NULL,NULL,0,0,NULL,NULL,NULL,NULL,NULL,NULL),(4,'Dr. Smith','Cardiologist','101','Yes','Mon-Fri','2024-11-16 12:36:01','2024-11-16 12:36:01',10,5,0,NULL,'12:00-13:00',0,0,NULL,NULL,NULL,NULL,NULL,NULL),(5,'Dr. Smith','Cardiologist','101','Yes','Mon-Fri','2024-11-16 12:39:17','2024-11-16 12:39:17',10,5,0,NULL,'12:00-13:00',1,0,NULL,NULL,NULL,NULL,NULL,NULL),(6,'Dr. Smith','Cardiologist','101','Yes','Mon-Fri','2024-11-16 12:39:22','2024-11-16 12:39:22',10,5,0,NULL,'12:00-13:00',0,0,NULL,NULL,NULL,NULL,NULL,NULL),(7,'Dr. Smith','Cardiologist','101','Yes','Mon-Fri','2024-11-16 12:40:43','2024-11-16 12:40:43',10,5,30,'Details here','12:00-13:00',1,1,NULL,NULL,NULL,NULL,NULL,NULL),(8,'asdas','Anesthesiologist','AN3','0 / 20','Available','2024-11-16 12:41:04','2024-11-16 12:41:04',20,1,30,'','',0,0,NULL,NULL,NULL,NULL,NULL,NULL),(9,'Gwaposs','Allergist/Immunologist','AL2','0 / 20','Available','2024-11-16 12:41:35','2024-11-16 12:41:35',20,5,25,'','',0,0,NULL,NULL,NULL,NULL,NULL,NULL),(10,'Desmos','Dermatologist','DE4','0 / 4','Available','2024-11-16 12:48:48','2024-11-16 12:48:48',4,5,30,'','',0,0,NULL,NULL,NULL,NULL,NULL,NULL),(11,'sdfs','Allergist/Immunologist','AL4','0 / 20','Available','2024-11-16 13:19:15','2024-11-16 13:19:15',20,1,30,'','',0,0,NULL,NULL,NULL,NULL,NULL,NULL),(12,'mar','Allergist/Immunologist','AL9','0 / 5','Available','2024-11-17 00:13:28','2024-11-17 00:13:28',5,5,15,'','',0,0,NULL,NULL,NULL,NULL,NULL,NULL),(13,'23322','Allergist/Immunologist','AL3','0 / 20','Available','2024-11-17 00:26:58','2024-11-17 00:26:58',20,1,30,'',NULL,0,0,NULL,NULL,NULL,'00:00:00','00:00:00',NULL),(14,'Under','Allergist/Immunologist','AL4','0 / 20','Available','2024-11-17 00:38:45','2024-11-17 00:38:45',20,1,30,'',NULL,0,0,NULL,NULL,NULL,'00:00:00','00:00:00',NULL),(15,'12','Allergist/Immunologist','AL8','0 / 20','Available','2024-11-17 00:39:33','2024-11-17 00:39:33',20,1,30,'',NULL,0,0,NULL,NULL,NULL,'00:00:00','00:00:00',NULL),(16,'Jordan','Anesthesiologist','AN3','0 / 5','Available','2024-11-17 01:23:30','2024-11-17 01:23:30',5,9,30,'',NULL,0,0,NULL,NULL,NULL,'00:00:00','00:00:00',NULL),(17,'ASda','Allergist/Immunologist','AL6','0 / 20','Available','2024-11-17 02:35:27','2024-11-17 02:35:27',20,1,30,'',NULL,0,0,NULL,NULL,NULL,'00:00:00','00:00:00',NULL),(18,'Paxx','Cardiologist','CA27','0 / 20','Available','2024-11-17 03:33:31','2024-11-17 03:33:31',20,1,30,'','-',0,0,NULL,NULL,NULL,NULL,NULL,NULL),(19,'12312as','Allergist/Immunologist','AL24','0 / 20','Available','2024-11-17 04:16:11','2024-11-17 04:16:11',20,1,30,'','-',0,0,NULL,NULL,NULL,NULL,NULL,NULL),(20,'Asss','Allergist/Immunologist','123','','','2024-11-19 23:02:06','2024-11-20 01:03:53',20,1,30,'','',1,0,NULL,NULL,NULL,NULL,NULL,NULL),(21,'CAsdasd','Cardiologist','22','','','2024-11-19 23:02:18','2024-11-19 23:02:18',20,1,30,'','',0,0,NULL,NULL,NULL,NULL,NULL,NULL),(22,'Alisson','Gastroenterologist','2444','','','2024-11-19 23:02:39','2024-11-19 23:02:39',20,1,30,'','',1,0,NULL,NULL,NULL,NULL,NULL,NULL),(23,'Gwapo ko','Allergist/Immunologist','22','','','2024-11-19 23:11:20','2024-11-19 23:11:20',20,1,30,'','',0,0,NULL,NULL,NULL,NULL,NULL,NULL),(24,'Gwaposss','Allergist/Immunologist','22','','','2024-11-19 23:49:36','2024-11-19 23:49:36',20,1,30,'','',0,0,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `doctors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hospitalid`
--

DROP TABLE IF EXISTS `hospitalid`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hospitalid` (
  `hospital_id` varchar(20) NOT NULL,
  `name` varchar(100) NOT NULL,
  `birthday` date NOT NULL,
  `age` int NOT NULL,
  `sex` enum('Male','Female','Other','Prefer not to say') NOT NULL,
  `phone` varchar(20) NOT NULL,
  `email` varchar(100) NOT NULL,
  `street_address` varchar(255) NOT NULL,
  `city` varchar(100) NOT NULL,
  `state` char(2) NOT NULL,
  `zip_code` varchar(10) NOT NULL,
  `emergency_contact_name` varchar(100) NOT NULL,
  `emergency_contact_relationship` varchar(50) NOT NULL,
  `emergency_contact_phone` varchar(20) NOT NULL,
  `insurance_provider` varchar(100) DEFAULT NULL,
  `policy_number` varchar(50) DEFAULT NULL,
  `additional_notes` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`hospital_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hospitalid`
--

LOCK TABLES `hospitalid` WRITE;
/*!40000 ALTER TABLE `hospitalid` DISABLE KEYS */;
/*!40000 ALTER TABLE `hospitalid` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `investigations`
--

DROP TABLE IF EXISTS `investigations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `investigations` (
  `investigation_id` int NOT NULL AUTO_INCREMENT,
  `patient_id` int DEFAULT NULL,
  `blood_work` tinyint(1) DEFAULT NULL,
  `imaging` tinyint(1) DEFAULT NULL,
  `ecg` tinyint(1) DEFAULT NULL,
  `urinalysis` tinyint(1) DEFAULT NULL,
  `other` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`investigation_id`),
  KEY `patient_id` (`patient_id`),
  CONSTRAINT `investigations_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patient_information` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `investigations`
--

LOCK TABLES `investigations` WRITE;
/*!40000 ALTER TABLE `investigations` DISABLE KEYS */;
/*!40000 ALTER TABLE `investigations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `laboratorytest`
--

DROP TABLE IF EXISTS `laboratorytest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `laboratorytest` (
  `id` int NOT NULL AUTO_INCREMENT,
  `category` varchar(255) NOT NULL,
  `test_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `category` (`category`,`test_name`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `laboratorytest`
--

LOCK TABLES `laboratorytest` WRITE;
/*!40000 ALTER TABLE `laboratorytest` DISABLE KEYS */;
INSERT INTO `laboratorytest` VALUES (10,'Blood Work','Arterial Blood Gas (ABG)'),(5,'Blood Work','Blood Glucose Level (Fasting, Random)'),(12,'Blood Work','C-Reactive Protein (CRP), ESR'),(8,'Blood Work','Coagulation Profile (PT, aPTT, INR)'),(1,'Blood Work','Complete Blood Count (CBC)'),(2,'Blood Work','Electrolytes (Na, K, Cl, HCO3)'),(9,'Blood Work','Hemoglobin A1c'),(6,'Blood Work','Lipid Profile'),(3,'Blood Work','Liver Function Tests (LFTs)'),(4,'Blood Work','Renal Function Tests (RFTs)'),(7,'Blood Work','Thyroid Function Tests (TFTs)'),(11,'Blood Work','Vitamin and Mineral Levels (D, B12, Ca, Mg)'),(23,'ECG','Echocardiogram (ECHO)'),(22,'ECG','Electrocardiogram (ECG)'),(25,'ECG','Holter Monitor (24-hour ECG)'),(24,'ECG','Stress Test (Treadmill, Pharmacological)'),(16,'Imaging','Abdominal Ultrasound'),(14,'Imaging','Abdominal X-ray'),(15,'Imaging','Bone X-ray (Specific Area)'),(19,'Imaging','Carotid Doppler'),(13,'Imaging','Chest X-ray'),(20,'Imaging','CT Scan (Chest, Abdomen, Pelvis, Brain)'),(21,'Imaging','MRI (Brain, Spine, Musculoskeletal)'),(17,'Imaging','Pelvic Ultrasound'),(18,'Imaging','Renal Ultrasound'),(47,'Other Tests','Arterial Blood Gas (ABG)'),(42,'Other Tests','Autoimmune Screen (ANA, RF)'),(38,'Other Tests','Biopsy (Tissue Sampling)'),(31,'Other Tests','Blood Cultures'),(37,'Other Tests','Bronchoscopy'),(36,'Other Tests','Colonoscopy'),(44,'Other Tests','D-dimer'),(35,'Other Tests','Gastroscopy (EGD)'),(43,'Other Tests','Genetic Testing'),(40,'Other Tests','Hepatitis Panel'),(39,'Other Tests','HIV Screening'),(46,'Other Tests','Pregnancy Test (Urine or Serum)'),(45,'Other Tests','Prostate-Specific Antigen (PSA)'),(32,'Other Tests','Sputum Culture'),(34,'Other Tests','Stool Culture'),(33,'Other Tests','Throat Culture'),(41,'Other Tests','Tuberculosis Test (PPD or Chest X-ray)'),(28,'Urinalysis','24-hour Urine Collection'),(26,'Urinalysis','Routine Urinalysis'),(27,'Urinalysis','Urine Culture'),(30,'Urinalysis','Urine Dipstick (Glucose, Blood, Ketones)'),(29,'Urinalysis','Urine Pregnancy Test');
/*!40000 ALTER TABLE `laboratorytest` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medical_history`
--

DROP TABLE IF EXISTS `medical_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medical_history` (
  `history_id` int NOT NULL AUTO_INCREMENT,
  `patient_id` int DEFAULT NULL,
  `no_significant_history` tinyint(1) DEFAULT NULL,
  `chronic_conditions` tinyint(1) DEFAULT NULL,
  `previous_surgeries` tinyint(1) DEFAULT NULL,
  `medications` text,
  `allergies` text,
  `family_history` text,
  `non_smoker` tinyint(1) DEFAULT NULL,
  `smoker` tinyint(1) DEFAULT NULL,
  `alcohol_use` tinyint(1) DEFAULT NULL,
  `drug_use` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`history_id`),
  KEY `patient_id` (`patient_id`),
  CONSTRAINT `medical_history_ibfk_1` FOREIGN KEY (`patient_id`) REFERENCES `patient_information` (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medical_history`
--

LOCK TABLES `medical_history` WRITE;
/*!40000 ALTER TABLE `medical_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `medical_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medication_history`
--

DROP TABLE IF EXISTS `medication_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medication_history` (
  `record_id` int NOT NULL AUTO_INCREMENT,
  `hospital_id` varchar(15) DEFAULT NULL,
  `medication_info` text,
  `date` date DEFAULT NULL,
  PRIMARY KEY (`record_id`),
  KEY `hospital_id` (`hospital_id`),
  CONSTRAINT `medication_history_ibfk_1` FOREIGN KEY (`hospital_id`) REFERENCES `patients` (`hospital_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medication_history`
--

LOCK TABLES `medication_history` WRITE;
/*!40000 ALTER TABLE `medication_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `medication_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patient_information`
--

DROP TABLE IF EXISTS `patient_information`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `patient_information` (
  `patient_id` int NOT NULL AUTO_INCREMENT,
  `patient_name` varchar(100) NOT NULL,
  `age` int NOT NULL,
  `gender` varchar(20) NOT NULL,
  `previous_consultation_date` date DEFAULT NULL,
  `current_consultation_date` datetime NOT NULL,
  PRIMARY KEY (`patient_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patient_information`
--

LOCK TABLES `patient_information` WRITE;
/*!40000 ALTER TABLE `patient_information` DISABLE KEYS */;
/*!40000 ALTER TABLE `patient_information` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patient_tests`
--

DROP TABLE IF EXISTS `patient_tests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `patient_tests` (
  `patient_id` varchar(50) NOT NULL,
  `test_code` varchar(50) NOT NULL,
  `test_name` varchar(255) DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  `test_date` datetime DEFAULT NULL,
  `status` varchar(50) DEFAULT NULL,
  `result` text,
  PRIMARY KEY (`patient_id`,`test_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patient_tests`
--

LOCK TABLES `patient_tests` WRITE;
/*!40000 ALTER TABLE `patient_tests` DISABLE KEYS */;
/*!40000 ALTER TABLE `patient_tests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patients`
--

DROP TABLE IF EXISTS `patients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `patients` (
  `hospital_id` varchar(15) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `middle_name` varchar(50) DEFAULT NULL,
  `extension_name` varchar(10) DEFAULT NULL,
  `birthday` date DEFAULT NULL,
  `age` int DEFAULT NULL,
  `sex` enum('Male','Female','Other','Prefer not to say') DEFAULT NULL,
  `phone` varchar(15) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `street_address` varchar(100) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `state` varchar(100) DEFAULT NULL,
  `zip_code` varchar(10) DEFAULT NULL,
  `emergency_contact_name` varchar(100) DEFAULT NULL,
  `emergency_contact_relationship` varchar(50) DEFAULT NULL,
  `emergency_contact_phone` varchar(15) DEFAULT NULL,
  `insurance_provider` varchar(100) DEFAULT NULL,
  `policy_number` varchar(50) DEFAULT NULL,
  `additional_notes` text,
  PRIMARY KEY (`hospital_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patients`
--

LOCK TABLES `patients` WRITE;
/*!40000 ALTER TABLE `patients` DISABLE KEYS */;
INSERT INTO `patients` VALUES ('0009697848','Ramos','Raymund Gerard','Reyes','','2003-02-10',21,'Male','0941-151-2351','raymundgerardrestaca@gmail.com','Iponan','Cagayan de Oro','AL','90000','Gerard','Brother','0921-513-3451','','',''),('0067646934','Name','Raymund Gerard','Underson','','1923-02-10',101,'Male','0123-151-5155','raymundd@my.xue.deud.ph','Lapasan','Cagayan de Oro','Region X','90000','Name','Brother','0912-123-5124','','',''),('1000109346','Gonzales','Raymund Gerard','Reyes','','2003-02-10',21,'Male','0921-415-1231','raymundgerardrestaca@gmail.com','Iponna','Cagayan de Oro','AL','90000','Gerard','Brother','0923-156-3244','','',''),('2029748187','Meyers','Merrier','Smith','','2001-02-12',23,'Female','0923-512-3513','merrier@gmail.com','Iponan','Cagayan de Oro','Region X','90000','Name','Sister','0923-145-1566','','',''),('2339758376','Estaca','Rion Paulo','Reyes','','2005-05-10',19,'Male','0912-315-1544','rionpaulo@gmail.com','Iponan','Cagayan de Oro','AL','90000','Cherry','Mother','0921-515-5612','','',''),('3259594495','Brazil','Bob Marley','Sponge Bob','','1969-01-01',55,'Female','0955-524-2311','bobmarley@gmail.com','Lapasan','New York City','AL','90000','Koyens','Brother','0912-151-5235','Limketkai','1234-5667-2355','Asthma, Seizure, HIV, AIDS, Mallari'),('4261817698','John','Meyers','Smith','','2000-02-10',24,'Female','0923-140-1234','raymundgerardrestaca@gmail.com','Iponan','Cagayan de Oro','AL','90000','Anna','Sister','0923-123-5125','','',''),('4914090312','asd','asd','asdas','','2003-02-10',21,'Male','0931-231-2312','rasasdasda@gmail.com','Asdasd123123','Casdasdw1','NCR - National Capital Region','90000','Rasdasdad','asdasd','3452-341-2312','','',''),('6601420936','Estaca','John Smith','Reyes','','2003-02-10',21,'Male','0923-123-5138','raymundgerardrestaca@gmail.com','Iponan','Cagayan de Oro','AL','90000','Cherry','Mother','0923-156-1234','','',''),('6910585618','Llanita','Raymund Gerard','Reyes','','2003-02-10',21,'Male','0921-512-3523','raymundgerardrestaca@gmail.com','Iponan','Cagayan de Oro','AL','90000','Gerard','Brother','0912-151-2345','','',''),('9733840922','Hopkins','John','Alexson','','2000-02-10',24,'Male','0123-125-1551','raymundgerardrestaca@gmail.com','Iponan','Cagayan de Oro','Region X','90000','Alice','Sister','0912-341-1224','','',''),('9935973854','Brazil','Raymund Gerard','Reyes','','2003-02-10',21,'Male','0923-123-3412','raymundgerardrestaca@gmail.com','Iponan','Cagayan de Oro','AL','90000','Gerard','Brother','0923-141-1235','','','');
/*!40000 ALTER TABLE `patients` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prescriptions`
--

DROP TABLE IF EXISTS `prescriptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prescriptions` (
  `prescription_id` varchar(50) NOT NULL,
  `patient_id` varchar(50) DEFAULT NULL,
  `doctor` varchar(100) DEFAULT NULL,
  `date_issued` date DEFAULT NULL,
  `is_filled` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`prescription_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prescriptions`
--

LOCK TABLES `prescriptions` WRITE;
/*!40000 ALTER TABLE `prescriptions` DISABLE KEYS */;
/*!40000 ALTER TABLE `prescriptions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `receipts`
--

DROP TABLE IF EXISTS `receipts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `receipts` (
  `receipt_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `hospital_id` varchar(50) NOT NULL,
  `consultation_fee` decimal(10,2) DEFAULT NULL,
  `payment_method` varchar(50) DEFAULT NULL,
  `admin_name` varchar(100) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `time` time DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  `last_name` varchar(50) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `middle_name` varchar(50) DEFAULT NULL,
  `balance` double DEFAULT '0',
  `specialtyComboBox` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`receipt_id`),
  UNIQUE KEY `receipt_id` (`receipt_id`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `receipts`
--

LOCK TABLES `receipts` WRITE;
/*!40000 ALTER TABLE `receipts` DISABLE KEYS */;
INSERT INTO `receipts` VALUES (26,'2339758376',100.00,'Cash',NULL,'2024-11-13','08:05:15','Paid','Estaca','Rion Paulo','Reyes',0,NULL),(27,'6601420936',NULL,NULL,NULL,'2024-11-13','09:48:48','Pending','Estaca','John Smith','Reyes',0,NULL),(28,'3259594495',0.00,NULL,NULL,'2024-11-13','09:52:03','Pending','Brazil','Bob Marley','Sponge Bob',0,NULL),(29,'0067646934',0.00,'Cash',NULL,'2024-11-13','09:59:14','Paid','Name','Raymund Gerard','Underson',0,NULL),(30,'9733840922',233.00,'Cash',NULL,'2024-11-13','18:05:45','Paid','Hopkins','John','Alexson',0,NULL),(31,'9733840922',233.00,'Cash',NULL,'2024-11-13','18:11:13','Paid','Hopkins','John','Alexson',0,NULL),(32,'9733840922',233.00,'Cash',NULL,'2024-11-13','18:23:39','Paid','Hopkins','John','Alexson',0,NULL),(33,'9733840922',233.00,'Cash',NULL,'2024-11-13','18:26:24','Paid','Hopkins','John','Alexson',0,NULL),(34,'9733840922',500.00,'Cash',NULL,'2024-11-13','18:32:42','Paid','Hopkins','John','Alexson',0,NULL),(35,'9733840922',450.00,'Cash',NULL,'2024-11-13','18:32:53','Paid','Hopkins','John','Alexson',0,NULL),(36,'9733840922',450.00,'Cash',NULL,'2024-11-13','18:35:22','Paid','Hopkins','John','Alexson',0,NULL),(37,'9733840922',450.00,'Cash',NULL,'2024-11-13','18:44:36','Paid','Hopkins','John','Alexson',0,NULL),(38,'9733840922',700.00,NULL,NULL,'2024-11-13','19:20:38','Pending','Hopkins','John','Alexson',0,NULL),(39,'2029748187',700.00,'Cash',NULL,'2024-11-13','19:55:39','Paid','Meyers','Merrier','Smith',0,NULL),(40,'2029748187',700.00,NULL,NULL,'2024-11-13','19:55:42','Pending','Meyers','Merrier','Smith',0,NULL),(41,'2029748187',800.00,NULL,NULL,'2024-11-13','19:56:44','Pending','Meyers','Merrier','Smith',0,NULL),(42,'0009697848',700.00,'Cash',NULL,'2024-11-14','20:05:29','Paid','Ramos','Raymund Gerard','Reyes',0,NULL),(43,'0009697848',700.00,'Cash',NULL,'2024-11-14','20:13:17','Paid','Ramos','Raymund Gerard','Reyes',0,NULL),(44,'0009697848',700.00,'Cash',NULL,'2024-11-14','20:14:57','Paid','Ramos','Raymund Gerard','Reyes',0,NULL),(45,'0009697848',700.00,'Cash',NULL,'2024-11-14','20:15:29','Paid','Ramos','Raymund Gerard','Reyes',0,NULL),(46,'0009697848',700.00,'Cash',NULL,'2024-11-14','20:15:34','Paid','Ramos','Raymund Gerard','Reyes',0,NULL),(47,'0009697848',700.00,'Cash',NULL,'2024-11-14','20:15:39','Paid','Ramos','Raymund Gerard','Reyes',0,NULL),(48,'0009697848',700.00,'Cash',NULL,'2024-11-14','21:08:27','Paid','Ramos','Raymund Gerard','Reyes',0,'Allergist/Immunologist'),(49,'0009697848',700.00,'Cash',NULL,'2024-11-14','21:09:42','Paid','Ramos','Raymund Gerard','Reyes',0,'Allergist/Immunologist'),(50,'0009697848',700.00,'Cash',NULL,'2024-11-14','21:10:20','Paid','Ramos','Raymund Gerard','Reyes',0,'Allergist/Immunologist'),(51,'0009697848',700.00,'Cash',NULL,'2024-11-14','21:32:05','Paid','Ramos','Raymund Gerard','Reyes',0,'Allergist/Immunologist'),(52,'0009697848',700.00,'Cash',NULL,'2024-11-14','21:32:26','Paid','Ramos','Raymund Gerard','Reyes',0,'Allergist/Immunologist'),(53,'0009697848',700.00,'Cash',NULL,'2024-11-14','21:32:47','Paid','Ramos','Raymund Gerard','Reyes',0,'Allergist/Immunologist'),(54,'0009697848',700.00,'Cash',NULL,'2024-11-14','21:38:58','Paid','Ramos','Raymund Gerard','Reyes',0,'Allergist/Immunologist'),(55,'0009697848',600.00,'Cash',NULL,'2024-11-15','07:09:00','Paid','Ramos','Raymund Gerard','Reyes',0,'Allergist/Immunologist'),(56,'4261817698',800.00,'Cash',NULL,'2024-11-15','07:33:46','Paid','John','Meyers','Smith',0,'Cardiologist'),(57,'1000109346',500.00,NULL,NULL,'2024-11-15','07:35:06','Pending','Gonzales','Raymund Gerard','Reyes',0,'Dermatologist'),(58,'0009697848',550.00,NULL,NULL,'2024-11-15','07:50:49','Pending','Ramos','Raymund Gerard',NULL,0,'Anesthesiologist'),(59,'0009697848',800.00,NULL,NULL,'2024-11-15','07:59:07','Pending','Ramos','Raymund Gerard',NULL,0,'Cardiologist'),(60,'0009697848',600.00,'Cash',NULL,'2024-11-15','07:59:12','Paid','Ramos','Raymund Gerard',NULL,0,'Endocrinologist'),(61,'0009697848',450.00,'Cash',NULL,'2024-11-17','19:17:10','Paid','Ramos','Raymund Gerard',NULL,0,'Geriatrician');
/*!40000 ALTER TABLE `receipts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refills`
--

DROP TABLE IF EXISTS `refills`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refills` (
  `refill_id` int NOT NULL AUTO_INCREMENT,
  `prescription_id` varchar(50) DEFAULT NULL,
  `refill_date` date DEFAULT NULL,
  PRIMARY KEY (`refill_id`),
  KEY `prescription_id` (`prescription_id`),
  CONSTRAINT `refills_ibfk_1` FOREIGN KEY (`prescription_id`) REFERENCES `prescriptions` (`prescription_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refills`
--

LOCK TABLES `refills` WRITE;
/*!40000 ALTER TABLE `refills` DISABLE KEYS */;
/*!40000 ALTER TABLE `refills` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tests`
--

DROP TABLE IF EXISTS `tests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tests` (
  `test_id` int NOT NULL AUTO_INCREMENT,
  `test_name` varchar(255) NOT NULL,
  `category_id` int DEFAULT NULL,
  `price` decimal(10,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`test_id`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `tests_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tests`
--

LOCK TABLES `tests` WRITE;
/*!40000 ALTER TABLE `tests` DISABLE KEYS */;
INSERT INTO `tests` VALUES (1,'Complete Blood Count (CBC)',1,25.00),(2,'Electrolytes (Na, K, Cl, HCO3)',1,30.00),(3,'Liver Function Tests (LFTs)',1,35.00),(4,'Renal Function Tests (RFTs)',1,40.00),(5,'Blood Glucose Level (Fasting, Random)',1,20.00),(6,'Lipid Profile',1,50.00),(7,'Thyroid Function Tests (TFTs)',1,45.00),(8,'Coagulation Profile (PT, aPTT, INR)',1,60.00),(9,'Hemoglobin A1c',1,55.00),(10,'Arterial Blood Gas (ABG)',1,70.00),(11,'Vitamin and Mineral Levels (D, B12, Ca, Mg)',1,65.00),(12,'C-Reactive Protein (CRP), ESR',1,75.00),(13,'Chest X-ray',2,100.00),(14,'Abdominal X-ray',2,110.00),(15,'Bone X-ray (Specific Area)',2,90.00),(16,'Abdominal Ultrasound',2,120.00),(17,'Pelvic Ultrasound',2,130.00),(18,'Renal Ultrasound',2,140.00),(19,'Carotid Doppler',2,150.00),(20,'CT Scan (Chest, Abdomen, Pelvis, Brain)',2,200.00),(21,'MRI (Brain, Spine, Musculoskeletal)',2,300.00),(22,'Electrocardiogram (ECG)',3,75.00),(23,'Echocardiogram (ECHO)',3,150.00),(24,'Stress Test (Treadmill, Pharmacological)',3,200.00),(25,'Holter Monitor (24-hour ECG)',3,250.00),(26,'Routine Urinalysis',4,15.00),(27,'Urine Culture',4,20.00),(28,'24-hour Urine Collection',4,25.00),(29,'Urine Pregnancy Test',4,10.00),(30,'Urine Dipstick (Glucose, Blood, Ketones)',4,12.50),(31,'Blood Cultures',5,80.00),(32,'Sputum Culture',5,85.00),(33,'Throat Culture',5,50.00),(34,'Stool Culture',5,60.00),(35,'Gastroscopy (EGD)',5,200.00),(36,'Colonoscopy',5,250.00),(37,'Bronchoscopy',5,220.00),(38,'Biopsy (Tissue Sampling)',5,150.00),(39,'HIV Screening',5,100.00),(40,'Hepatitis Panel',5,120.00),(41,'Tuberculosis Test (PPD or Chest X-ray)',5,90.00),(42,'Autoimmune Screen (ANA, RF)',5,95.00),(43,'Genetic Testing',5,300.00),(44,'D-dimer',5,50.00),(45,'Prostate-Specific Antigen (PSA)',5,80.00),(46,'Pregnancy Test (Urine or Serum)',5,15.00),(47,'Arterial Blood Gas (ABG)',5,70.00);
/*!40000 ALTER TABLE `tests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transaction` (
  `id` int NOT NULL AUTO_INCREMENT,
  `hospital_id` varchar(255) NOT NULL,
  `patient_name` varchar(255) NOT NULL,
  `age` int NOT NULL,
  `sex` varchar(10) NOT NULL,
  `doctor_name` varchar(255) DEFAULT NULL,
  `selected_tests` text NOT NULL,
  `total_amount` int NOT NULL,
  `status` varchar(20) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction`
--

LOCK TABLES `transaction` WRITE;
/*!40000 ALTER TABLE `transaction` DISABLE KEYS */;
INSERT INTO `transaction` VALUES (1,'asda','asda',123,'Female','as','Blood Cultures, Urine Dipstick (Glucose, Blood, Ketones), Sputum Culture, Gastroscopy (EGD)',0,'PENDING','2024-11-20 10:05:48');
/*!40000 ALTER TABLE `transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `age` int NOT NULL,
  `sex` varchar(20) NOT NULL,
  `address` text NOT NULL,
  `phone` varchar(20) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Enter your unique username','Enter your full name',0,'Select','Enter your full address','(___) ___-____','Enter your email address','Enter a strong password'),(2,'RaymundEstaca','Raymund Gerard Estaca',21,'Male','Iponan CDO','(097) 572-4678','raymundgerardrestaca@gmail.com','Raymund@Estaca01'),(4,'Raymund','GerardReyes',21,'Male','Iponan CDO','(097) 575-2433','raymundgerardrestaca1@gmail.com','Reyes'),(6,'Max','MaxwellJohnson',21,'Male','Berkeley Canada','(097) 575-2467','Maxwell@gmail.com','Maxwell'),(11,'RaymundEstaca1','RaymundEstaca',21,'Male','Iponan','(092) 312-3123','raymundgerardrestaca2@gmail.com','Raymund@Estaca01'),(14,'RaymundG.','RaymundEstaca',21,'Male','Iponan, Cagayan de Oro','(0975) 752-4678','raymundgerard@gmail.com','Raymund@Estaca01');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-11-22 13:43:45

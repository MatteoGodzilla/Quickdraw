/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19  Distrib 10.11.13-MariaDB, for Linux (x86_64)
--
-- Host: 192.168.1.200    Database: QuickDraw
-- ------------------------------------------------------
-- Server version	10.11.11-MariaDB-0+deb12u1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ActiveContract`
--

DROP TABLE IF EXISTS `ActiveContract`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `ActiveContract` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idContract` int(11) NOT NULL,
  `startTime` bigint(20) unsigned NOT NULL COMMENT 'saved as a UTC Unix timestamp',
  `reward` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `ActiveAdventure_Adventure_FK` (`idContract`),
  CONSTRAINT `ActiveAdventure_Adventure_FK` FOREIGN KEY (`idContract`) REFERENCES `Contract` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ActiveContract`
--

LOCK TABLES `ActiveContract` WRITE;
/*!40000 ALTER TABLE `ActiveContract` DISABLE KEYS */;
/*!40000 ALTER TABLE `ActiveContract` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `AssignedMercenary`
--

DROP TABLE IF EXISTS `AssignedMercenary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `AssignedMercenary` (
  `idEmployedMercenary` int(11) NOT NULL,
  `idActiveContract` int(11) NOT NULL,
  PRIMARY KEY (`idEmployedMercenary`,`idActiveContract`),
  KEY `AssignedMercenary_ActiveAdventure_FK` (`idActiveContract`),
  CONSTRAINT `AssignedMercenary_ActiveAdventure_FK` FOREIGN KEY (`idActiveContract`) REFERENCES `ActiveContract` (`id`),
  CONSTRAINT `AssignedMercenary_EmployedMercenary_FK` FOREIGN KEY (`idEmployedMercenary`) REFERENCES `EmployedMercenary` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `AssignedMercenary`
--

LOCK TABLES `AssignedMercenary` WRITE;
/*!40000 ALTER TABLE `AssignedMercenary` DISABLE KEYS */;
/*!40000 ALTER TABLE `AssignedMercenary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Bullet`
--

DROP TABLE IF EXISTS `Bullet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Bullet` (
  `type` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(128) NOT NULL,
  `capacity` int(11) NOT NULL,
  PRIMARY KEY (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Bullet`
--

LOCK TABLES `Bullet` WRITE;
/*!40000 ALTER TABLE `Bullet` DISABLE KEYS */;
INSERT INTO `Bullet` VALUES
(1,'.45',999),
(2,'.58',100),
(3,'.73',60);
/*!40000 ALTER TABLE `Bullet` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `BulletShop`
--

DROP TABLE IF EXISTS `BulletShop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `BulletShop` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idBullet` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `cost` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `BulletShop_Bullet_FK` (`idBullet`),
  CONSTRAINT `BulletShop_Bullet_FK` FOREIGN KEY (`idBullet`) REFERENCES `Bullet` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `BulletShop`
--

LOCK TABLES `BulletShop` WRITE;
/*!40000 ALTER TABLE `BulletShop` DISABLE KEYS */;
INSERT INTO `BulletShop` VALUES
(1,1,1,0),
(2,2,1,5),
(3,3,1,10),
(4,2,5,20),
(5,3,5,45),
(6,2,10,40),
(7,3,10,85),
(8,2,20,80),
(9,3,20,170);
/*!40000 ALTER TABLE `BulletShop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Contract`
--

DROP TABLE IF EXISTS `Contract`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Contract` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `requiredTime` int(11) NOT NULL COMMENT 'in milliseconds',
  `requiredPower` int(11) NOT NULL DEFAULT 0,
  `maxMercenaries` int(11) NOT NULL,
  `minReward` int(11) NOT NULL,
  `maxReward` int(11) NOT NULL,
  `startCost` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Contract`
--

LOCK TABLES `Contract` WRITE;
/*!40000 ALTER TABLE `Contract` DISABLE KEYS */;
INSERT INTO `Contract` VALUES
(1,'Contract 1',60000,0,1,0,100,0),
(2,'Contract 2',120000,0,1,0,200,0);
/*!40000 ALTER TABLE `Contract` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Duel`
--

DROP TABLE IF EXISTS `Duel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Duel` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idPlayerA` int(11) NOT NULL,
  `idPlayerB` int(11) NOT NULL,
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '0 - Da verificare\n1 - Verificato\n2 - Annullato',
  `timestamp` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Duel`
--

LOCK TABLES `Duel` WRITE;
/*!40000 ALTER TABLE `Duel` DISABLE KEYS */;
/*!40000 ALTER TABLE `Duel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `EmployedMercenary`
--

DROP TABLE IF EXISTS `EmployedMercenary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `EmployedMercenary` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idPlayer` int(11) NOT NULL,
  `idMercenary` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `EmployedMercenary_Player_FK` (`idPlayer`),
  KEY `EmployedMercenary_Mercenary_FK` (`idMercenary`),
  CONSTRAINT `EmployedMercenary_Mercenary_FK` FOREIGN KEY (`idMercenary`) REFERENCES `Mercenary` (`id`),
  CONSTRAINT `EmployedMercenary_Player_FK` FOREIGN KEY (`idPlayer`) REFERENCES `Player` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `EmployedMercenary`
--

LOCK TABLES `EmployedMercenary` WRITE;
/*!40000 ALTER TABLE `EmployedMercenary` DISABLE KEYS */;
INSERT INTO `EmployedMercenary` VALUES
(1,8,1),
(2,8,2),
(3,8,4);
/*!40000 ALTER TABLE `EmployedMercenary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Friendship`
--

DROP TABLE IF EXISTS `Friendship`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Friendship` (
  `idPlayerFrom` int(11) NOT NULL,
  `idPlayerFriend` int(11) NOT NULL,
  PRIMARY KEY (`idPlayerFrom`,`idPlayerFriend`),
  KEY `Friendship_Player_FK_1` (`idPlayerFriend`),
  CONSTRAINT `Friendship_Player_FK` FOREIGN KEY (`idPlayerFrom`) REFERENCES `Player` (`id`),
  CONSTRAINT `Friendship_Player_FK_1` FOREIGN KEY (`idPlayerFriend`) REFERENCES `Player` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Friendship`
--

LOCK TABLES `Friendship` WRITE;
/*!40000 ALTER TABLE `Friendship` DISABLE KEYS */;
INSERT INTO `Friendship` VALUES
(8,1),
(8,2);
/*!40000 ALTER TABLE `Friendship` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Login`
--

DROP TABLE IF EXISTS `Login`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Login` (
  `email` varchar(254) NOT NULL,
  `password` binary(60) NOT NULL,
  `idPlayer` int(11) NOT NULL,
  `authToken` char(36) DEFAULT NULL,
  PRIMARY KEY (`email`),
  KEY `Login_Player_FK` (`idPlayer`),
  CONSTRAINT `Login_Player_FK` FOREIGN KEY (`idPlayer`) REFERENCES `Player` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Login`
--

LOCK TABLES `Login` WRITE;
/*!40000 ALTER TABLE `Login` DISABLE KEYS */;
INSERT INTO `Login` VALUES
('blablacar@no','$2b$12$qRjMm4nfyuUu2tOLiPMXCepuOYqTMmsGEtxvaLd5748CYGJydeXfe',8,'0196f890-d7d1-7782-a684-d66e00f85e18'),
('dallathemage@test.com','$2b$12$ijrf6vB9dtANg/hmuiYPH.W4h0Z5/KcR8vZqSJ3LW77cQrYrIGZKe',2,NULL),
('ginopippo@alice.it','$2b$12$HOuP8GwKNN.YzG/gebKCpegRfTOwXU/FGWadHovEFj7aYdYYyvdzq',3,'01969c1c-25f6-7603-b285-56bccfd04b5a'),
('matteogodzilla@test.com','$2b$12$P.pFViQFcOU3owCJe7TxLeNq7RhyH0UbXcNwWrZvo5rpjELL.2fV.',1,'0197c55b-9e4e-715c-8a29-240104a83653'),
('test1@user','$2b$12$3B3OkEUkjmPtckNuMPAteOweMNBHxsHridop.wQ/5fWaoLmlzwHfe',9,'0688276a09557cdf848b2d63e99063f0');
/*!40000 ALTER TABLE `Login` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Medikit`
--

DROP TABLE IF EXISTS `Medikit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Medikit` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `healthRecover` int(11) NOT NULL,
  `description` varchar(128) DEFAULT NULL,
  `capacity` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Medikit`
--

LOCK TABLES `Medikit` WRITE;
/*!40000 ALTER TABLE `Medikit` DISABLE KEYS */;
INSERT INTO `Medikit` VALUES
(1,50,'Very Large Medikit',2),
(2,20,'Large Medikit',5),
(3,10,'Normal Medikit',10),
(4,5,'Small Medikit',20),
(5,1,'Very Small Medikit',100);
/*!40000 ALTER TABLE `Medikit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MedikitShop`
--

DROP TABLE IF EXISTS `MedikitShop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `MedikitShop` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idMedikit` int(11) NOT NULL,
  `cost` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `MedikitShop_Medikit_FK` (`idMedikit`),
  CONSTRAINT `MedikitShop_Medikit_FK` FOREIGN KEY (`idMedikit`) REFERENCES `Medikit` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MedikitShop`
--

LOCK TABLES `MedikitShop` WRITE;
/*!40000 ALTER TABLE `MedikitShop` DISABLE KEYS */;
INSERT INTO `MedikitShop` VALUES
(1,1,100,1),
(2,2,40,1),
(3,3,20,1),
(4,4,10,1),
(5,5,2,1),
(6,2,70,2),
(7,3,35,2),
(8,4,18,2),
(9,5,4,2),
(10,3,90,5),
(11,4,45,5),
(12,5,8,5),
(13,5,15,10);
/*!40000 ALTER TABLE `MedikitShop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Mercenary`
--

DROP TABLE IF EXISTS `Mercenary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Mercenary` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `power` int(11) NOT NULL,
  `requiredLevel` int(11) NOT NULL,
  `employmentCost` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Mercenary`
--

LOCK TABLES `Mercenary` WRITE;
/*!40000 ALTER TABLE `Mercenary` DISABLE KEYS */;
INSERT INTO `Mercenary` VALUES
(1,'Big Buck',10,0,5),
(2,'Bunny',8,0,3),
(3,'Jackson',20,1,40),
(4,'Tobuy',5,0,30);
/*!40000 ALTER TABLE `Mercenary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Player`
--

DROP TABLE IF EXISTS `Player`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Player` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `health` int(11) NOT NULL DEFAULT 100,
  `maxHealth` int(11) NOT NULL DEFAULT 100,
  `exp` int(11) NOT NULL DEFAULT 0,
  `money` int(11) NOT NULL DEFAULT 0,
  `bounty` int(11) NOT NULL DEFAULT 0,
  `username` varchar(128) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Player`
--

LOCK TABLES `Player` WRITE;
/*!40000 ALTER TABLE `Player` DISABLE KEYS */;
INSERT INTO `Player` VALUES
(1,100,100,0,0,0,'MatteoGodzilla'),
(2,100,100,0,0,99,'DallaTheMage'),
(3,100,100,0,0,0,'PippoGino'),
(4,100,100,0,0,0,'blablacar'),
(7,100,100,0,0,0,'blablacar'),
(8,100,100,0,177,0,'blablacar'),
(9,100,100,0,0,0,'test1user');
/*!40000 ALTER TABLE `Player` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PlayerBullet`
--

DROP TABLE IF EXISTS `PlayerBullet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `PlayerBullet` (
  `idPlayer` int(11) NOT NULL,
  `idBullet` int(11) NOT NULL,
  `amount` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`idPlayer`,`idBullet`),
  KEY `PlayerBullet_Bullet_FK` (`idBullet`),
  CONSTRAINT `PlayerBullet_Bullet_FK` FOREIGN KEY (`idBullet`) REFERENCES `Bullet` (`type`),
  CONSTRAINT `PlayerBullet_Player_FK` FOREIGN KEY (`idPlayer`) REFERENCES `Player` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PlayerBullet`
--

LOCK TABLES `PlayerBullet` WRITE;
/*!40000 ALTER TABLE `PlayerBullet` DISABLE KEYS */;
/*!40000 ALTER TABLE `PlayerBullet` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PlayerMedikit`
--

DROP TABLE IF EXISTS `PlayerMedikit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `PlayerMedikit` (
  `idPlayer` int(11) NOT NULL,
  `idMedikit` int(11) NOT NULL,
  `amount` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`idPlayer`,`idMedikit`),
  KEY `PlayerMedikit_Medikit_FK` (`idMedikit`),
  CONSTRAINT `PlayerMedikit_Medikit_FK` FOREIGN KEY (`idMedikit`) REFERENCES `Medikit` (`id`),
  CONSTRAINT `PlayerMedikit_Player_FK` FOREIGN KEY (`idPlayer`) REFERENCES `Player` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PlayerMedikit`
--

LOCK TABLES `PlayerMedikit` WRITE;
/*!40000 ALTER TABLE `PlayerMedikit` DISABLE KEYS */;
INSERT INTO `PlayerMedikit` VALUES
(1,1,1),
(1,2,2);
/*!40000 ALTER TABLE `PlayerMedikit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PlayerUpgrade`
--

DROP TABLE IF EXISTS `PlayerUpgrade`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `PlayerUpgrade` (
  `idPlayer` int(11) NOT NULL,
  `idUpgrade` int(11) NOT NULL,
  PRIMARY KEY (`idPlayer`,`idUpgrade`),
  KEY `PlayerUpgrade_UpgradeShop_FK` (`idUpgrade`),
  CONSTRAINT `PlayerUpgrade_Player_FK` FOREIGN KEY (`idPlayer`) REFERENCES `Player` (`id`),
  CONSTRAINT `PlayerUpgrade_UpgradeShop_FK` FOREIGN KEY (`idUpgrade`) REFERENCES `UpgradeShop` (`idUpgrade`)
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PlayerUpgrade`
--

LOCK TABLES `PlayerUpgrade` WRITE;
/*!40000 ALTER TABLE `PlayerUpgrade` DISABLE KEYS */;
INSERT INTO `PlayerUpgrade` VALUES
(1,1),
(1,2),
(1,6);
/*!40000 ALTER TABLE `PlayerUpgrade` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PlayerWeapon`
--

DROP TABLE IF EXISTS `PlayerWeapon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `PlayerWeapon` (
  `idPlayer` int(11) NOT NULL,
  `idWeapon` int(11) NOT NULL,
  PRIMARY KEY (`idPlayer`,`idWeapon`),
  KEY `PlayerWeapon_Weapon_FK` (`idWeapon`),
  CONSTRAINT `PlayerWeapon_Player_FK` FOREIGN KEY (`idPlayer`) REFERENCES `Player` (`id`),
  CONSTRAINT `PlayerWeapon_Weapon_FK` FOREIGN KEY (`idWeapon`) REFERENCES `Weapon` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `PlayerWeapon`
--

LOCK TABLES `PlayerWeapon` WRITE;
/*!40000 ALTER TABLE `PlayerWeapon` DISABLE KEYS */;
INSERT INTO `PlayerWeapon` VALUES
(1,1);
/*!40000 ALTER TABLE `PlayerWeapon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Round`
--

DROP TABLE IF EXISTS `Round`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Round` (
  `idDuel` int(11) NOT NULL,
  `roundNumber` int(11) NOT NULL,
  `idPlayer` int(11) NOT NULL,
  `won` tinyint(1) NOT NULL,
  `idWeaponUsed` int(11) NOT NULL,
  `bulletsUsed` int(11) NOT NULL DEFAULT 1,
  `damage` int(11) NOT NULL,
  PRIMARY KEY (`idDuel`,`roundNumber`,`idPlayer`),
  CONSTRAINT `Round_Duel_FK` FOREIGN KEY (`idDuel`) REFERENCES `Duel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Round`
--

LOCK TABLES `Round` WRITE;
/*!40000 ALTER TABLE `Round` DISABLE KEYS */;
/*!40000 ALTER TABLE `Round` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `UpgradeShop`
--

DROP TABLE IF EXISTS `UpgradeShop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `UpgradeShop` (
  `idUpgrade` int(11) NOT NULL AUTO_INCREMENT,
  `type` int(11) NOT NULL,
  `level` int(11) NOT NULL,
  `cost` int(11) NOT NULL,
  PRIMARY KEY (`idUpgrade`),
  KEY `UpgradeShop_UpgradeTypes_FK` (`type`),
  CONSTRAINT `UpgradeShop_UpgradeTypes_FK` FOREIGN KEY (`type`) REFERENCES `UpgradeTypes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `UpgradeShop`
--

LOCK TABLES `UpgradeShop` WRITE;
/*!40000 ALTER TABLE `UpgradeShop` DISABLE KEYS */;
INSERT INTO `UpgradeShop` VALUES
(1,1,1,10),
(2,1,2,20),
(3,1,3,40),
(4,1,4,80),
(5,1,5,160),
(6,2,1,10),
(7,2,2,20),
(8,2,3,40),
(9,2,4,80),
(10,2,5,160),
(11,3,1,10),
(12,3,2,20),
(13,3,3,40),
(14,3,4,80),
(15,3,5,160),
(16,4,1,10),
(17,4,2,20),
(18,4,3,40),
(19,4,4,80),
(20,4,5,160),
(21,5,1,10),
(22,5,2,20),
(23,5,3,40),
(24,5,4,80),
(25,5,5,160);
/*!40000 ALTER TABLE `UpgradeShop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `UpgradeTypes`
--

DROP TABLE IF EXISTS `UpgradeTypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `UpgradeTypes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `Description` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `UpgradeTypes`
--

LOCK TABLES `UpgradeTypes` WRITE;
/*!40000 ALTER TABLE `UpgradeTypes` DISABLE KEYS */;
INSERT INTO `UpgradeTypes` VALUES
(1,'Max Health'),
(2,'Max Mercenaries'),
(3,'Max Active Contracts'),
(4,'Boost Money'),
(5,'Boost Bounty'),
(6,'Boost Exp');
/*!40000 ALTER TABLE `UpgradeTypes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Weapon`
--

DROP TABLE IF EXISTS `Weapon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Weapon` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bulletType` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `damage` int(11) NOT NULL,
  `cost` int(11) NOT NULL DEFAULT 0,
  `bulletsShot` int(11) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `Weapon_Bullet_FK` (`bulletType`),
  CONSTRAINT `Weapon_Bullet_FK` FOREIGN KEY (`bulletType`) REFERENCES `Bullet` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Weapon`
--

LOCK TABLES `Weapon` WRITE;
/*!40000 ALTER TABLE `Weapon` DISABLE KEYS */;
INSERT INTO `Weapon` VALUES
(1,1,'Colt Navy Revolver',5,0,1),
(2,2,'Springfield 1975	',20,50,5),
(3,1,'Peacemaker',7,12,2),
(4,3,'Winchester',50,200,5);
/*!40000 ALTER TABLE `Weapon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `activecontract`
--

DROP TABLE IF EXISTS `activecontract`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `activecontract` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idContract` int(11) NOT NULL,
  `startTime` int(11) NOT NULL,
  `reward` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idContract` (`idContract`),
  CONSTRAINT `activecontract_ibfk_1` FOREIGN KEY (`idContract`) REFERENCES `contract` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activecontract`
--

LOCK TABLES `activecontract` WRITE;
/*!40000 ALTER TABLE `activecontract` DISABLE KEYS */;
/*!40000 ALTER TABLE `activecontract` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `assignedmercenary`
--

DROP TABLE IF EXISTS `assignedmercenary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `assignedmercenary` (
  `idActiveContract` int(11) NOT NULL,
  `idEmployedMercenary` int(11) NOT NULL,
  PRIMARY KEY (`idActiveContract`,`idEmployedMercenary`),
  KEY `idEmployedMercenary` (`idEmployedMercenary`),
  CONSTRAINT `assignedmercenary_ibfk_1` FOREIGN KEY (`idActiveContract`) REFERENCES `activecontract` (`id`),
  CONSTRAINT `assignedmercenary_ibfk_2` FOREIGN KEY (`idEmployedMercenary`) REFERENCES `employedmercenary` (`idMercenary`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `assignedmercenary`
--

LOCK TABLES `assignedmercenary` WRITE;
/*!40000 ALTER TABLE `assignedmercenary` DISABLE KEYS */;
/*!40000 ALTER TABLE `assignedmercenary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bullet`
--

DROP TABLE IF EXISTS `bullet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `bullet` (
  `type` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) NOT NULL,
  `capacity` int(11) NOT NULL,
  PRIMARY KEY (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bullet`
--

LOCK TABLES `bullet` WRITE;
/*!40000 ALTER TABLE `bullet` DISABLE KEYS */;
/*!40000 ALTER TABLE `bullet` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bulletshop`
--

DROP TABLE IF EXISTS `bulletshop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `bulletshop` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idBullet` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `cost` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idBullet` (`idBullet`),
  CONSTRAINT `bulletshop_ibfk_1` FOREIGN KEY (`idBullet`) REFERENCES `bullet` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bulletshop`
--

LOCK TABLES `bulletshop` WRITE;
/*!40000 ALTER TABLE `bulletshop` DISABLE KEYS */;
/*!40000 ALTER TABLE `bulletshop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contract`
--

DROP TABLE IF EXISTS `contract`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `contract` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `requiredTime` int(11) NOT NULL,
  `requiredPower` int(11) NOT NULL,
  `maxMercenaries` int(11) NOT NULL,
  `minReward` int(11) NOT NULL,
  `maxReward` int(11) NOT NULL,
  `startCost` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contract`
--

LOCK TABLES `contract` WRITE;
/*!40000 ALTER TABLE `contract` DISABLE KEYS */;
/*!40000 ALTER TABLE `contract` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employedmercenary`
--

DROP TABLE IF EXISTS `employedmercenary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `employedmercenary` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idPlayer` int(11) NOT NULL,
  `idMercenary` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idPlayer` (`idPlayer`),
  KEY `idMercenary` (`idMercenary`),
  CONSTRAINT `employedmercenary_ibfk_1` FOREIGN KEY (`idPlayer`) REFERENCES `player` (`id`),
  CONSTRAINT `employedmercenary_ibfk_2` FOREIGN KEY (`idMercenary`) REFERENCES `mercenary` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employedmercenary`
--

LOCK TABLES `employedmercenary` WRITE;
/*!40000 ALTER TABLE `employedmercenary` DISABLE KEYS */;
/*!40000 ALTER TABLE `employedmercenary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `friendship`
--

DROP TABLE IF EXISTS `friendship`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `friendship` (
  `idPlayerFrom` int(11) NOT NULL,
  `idPlayerTo` int(11) NOT NULL,
  PRIMARY KEY (`idPlayerFrom`,`idPlayerTo`),
  KEY `idPlayerTo` (`idPlayerTo`),
  CONSTRAINT `friendship_ibfk_1` FOREIGN KEY (`idPlayerFrom`) REFERENCES `player` (`id`),
  CONSTRAINT `friendship_ibfk_2` FOREIGN KEY (`idPlayerTo`) REFERENCES `player` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `friendship`
--

LOCK TABLES `friendship` WRITE;
/*!40000 ALTER TABLE `friendship` DISABLE KEYS */;
/*!40000 ALTER TABLE `friendship` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `login`
--

DROP TABLE IF EXISTS `login`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `login` (
  `email` varchar(255) NOT NULL,
  `password` blob DEFAULT NULL,
  `idPlayer` int(11) NOT NULL,
  PRIMARY KEY (`email`),
  KEY `ix_login_idPlayer` (`idPlayer`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `login`
--

LOCK TABLES `login` WRITE;
/*!40000 ALTER TABLE `login` DISABLE KEYS */;
INSERT INTO `login` VALUES
('test1@user','$2b$12$YdrePGoeDnOEVthpxG0sD.vY4F.AjBL0ZGk9wHAKnaF6L9js/HBB2',1);
/*!40000 ALTER TABLE `login` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medkit`
--

DROP TABLE IF EXISTS `medkit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `medkit` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `healthRecover` int(11) NOT NULL,
  `description` varchar(255) NOT NULL,
  `capacity` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medkit`
--

LOCK TABLES `medkit` WRITE;
/*!40000 ALTER TABLE `medkit` DISABLE KEYS */;
/*!40000 ALTER TABLE `medkit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medkitshop`
--

DROP TABLE IF EXISTS `medkitshop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `medkitshop` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idMedKit` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `cost` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idMedKit` (`idMedKit`),
  CONSTRAINT `medkitshop_ibfk_1` FOREIGN KEY (`idMedKit`) REFERENCES `medkit` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medkitshop`
--

LOCK TABLES `medkitshop` WRITE;
/*!40000 ALTER TABLE `medkitshop` DISABLE KEYS */;
/*!40000 ALTER TABLE `medkitshop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mercenary`
--

DROP TABLE IF EXISTS `mercenary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `mercenary` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `power` int(11) NOT NULL,
  `requiredLevel` int(11) NOT NULL,
  `employmentCost` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mercenary`
--

LOCK TABLES `mercenary` WRITE;
/*!40000 ALTER TABLE `mercenary` DISABLE KEYS */;
/*!40000 ALTER TABLE `mercenary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `player`
--

DROP TABLE IF EXISTS `player`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `player` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `health` int(11) NOT NULL,
  `maxHealth` int(11) NOT NULL,
  `exp` int(11) NOT NULL,
  `money` int(11) NOT NULL,
  `bounty` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `player`
--

LOCK TABLES `player` WRITE;
/*!40000 ALTER TABLE `player` DISABLE KEYS */;
INSERT INTO `player` VALUES
(1,100,100,0,0,0,'test1user');
/*!40000 ALTER TABLE `player` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `playerbullet`
--

DROP TABLE IF EXISTS `playerbullet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `playerbullet` (
  `idPlayer` int(11) NOT NULL,
  `idBullet` int(11) NOT NULL,
  `amount` int(11) NOT NULL,
  PRIMARY KEY (`idPlayer`,`idBullet`),
  KEY `idBullet` (`idBullet`),
  CONSTRAINT `playerbullet_ibfk_1` FOREIGN KEY (`idPlayer`) REFERENCES `player` (`id`),
  CONSTRAINT `playerbullet_ibfk_2` FOREIGN KEY (`idBullet`) REFERENCES `bullet` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playerbullet`
--

LOCK TABLES `playerbullet` WRITE;
/*!40000 ALTER TABLE `playerbullet` DISABLE KEYS */;
/*!40000 ALTER TABLE `playerbullet` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `playermedkit`
--

DROP TABLE IF EXISTS `playermedkit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `playermedkit` (
  `idPlayer` int(11) NOT NULL,
  `idMedKit` int(11) NOT NULL,
  `amount` int(11) NOT NULL,
  PRIMARY KEY (`idPlayer`,`idMedKit`),
  KEY `idMedKit` (`idMedKit`),
  CONSTRAINT `playermedkit_ibfk_1` FOREIGN KEY (`idPlayer`) REFERENCES `player` (`id`),
  CONSTRAINT `playermedkit_ibfk_2` FOREIGN KEY (`idMedKit`) REFERENCES `medkit` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playermedkit`
--

LOCK TABLES `playermedkit` WRITE;
/*!40000 ALTER TABLE `playermedkit` DISABLE KEYS */;
/*!40000 ALTER TABLE `playermedkit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `playerupgrade`
--

DROP TABLE IF EXISTS `playerupgrade`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `playerupgrade` (
  `idPlayer` int(11) NOT NULL,
  `idUpgrade` int(11) NOT NULL,
  PRIMARY KEY (`idPlayer`,`idUpgrade`),
  KEY `idUpgrade` (`idUpgrade`),
  CONSTRAINT `playerupgrade_ibfk_1` FOREIGN KEY (`idPlayer`) REFERENCES `player` (`id`),
  CONSTRAINT `playerupgrade_ibfk_2` FOREIGN KEY (`idUpgrade`) REFERENCES `upgradetypes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playerupgrade`
--

LOCK TABLES `playerupgrade` WRITE;
/*!40000 ALTER TABLE `playerupgrade` DISABLE KEYS */;
/*!40000 ALTER TABLE `playerupgrade` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `playerweapon`
--

DROP TABLE IF EXISTS `playerweapon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `playerweapon` (
  `idPlayer` int(11) NOT NULL,
  `idWeapon` int(11) NOT NULL,
  PRIMARY KEY (`idPlayer`,`idWeapon`),
  KEY `idWeapon` (`idWeapon`),
  CONSTRAINT `playerweapon_ibfk_1` FOREIGN KEY (`idPlayer`) REFERENCES `player` (`id`),
  CONSTRAINT `playerweapon_ibfk_2` FOREIGN KEY (`idWeapon`) REFERENCES `weapon` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playerweapon`
--

LOCK TABLES `playerweapon` WRITE;
/*!40000 ALTER TABLE `playerweapon` DISABLE KEYS */;
/*!40000 ALTER TABLE `playerweapon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `upgradeshop`
--

DROP TABLE IF EXISTS `upgradeshop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `upgradeshop` (
  `idUpgrade` int(11) NOT NULL,
  `value` int(11) NOT NULL,
  `cost` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  PRIMARY KEY (`idUpgrade`),
  CONSTRAINT `upgradeshop_ibfk_1` FOREIGN KEY (`idUpgrade`) REFERENCES `upgradetypes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `upgradeshop`
--

LOCK TABLES `upgradeshop` WRITE;
/*!40000 ALTER TABLE `upgradeshop` DISABLE KEYS */;
/*!40000 ALTER TABLE `upgradeshop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `upgradetypes`
--

DROP TABLE IF EXISTS `upgradetypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `upgradetypes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `description` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `upgradetypes`
--

LOCK TABLES `upgradetypes` WRITE;
/*!40000 ALTER TABLE `upgradetypes` DISABLE KEYS */;
/*!40000 ALTER TABLE `upgradetypes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `weapon`
--

DROP TABLE IF EXISTS `weapon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `weapon` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `damage` int(11) NOT NULL,
  `cost` int(11) NOT NULL,
  `bulletType` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `bulletType` (`bulletType`),
  CONSTRAINT `weapon_ibfk_1` FOREIGN KEY (`bulletType`) REFERENCES `bullet` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `weapon`
--

LOCK TABLES `weapon` WRITE;
/*!40000 ALTER TABLE `weapon` DISABLE KEYS */;
/*!40000 ALTER TABLE `weapon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'QuickDraw'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-07-24 20:53:14

/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19  Distrib 10.11.10-MariaDB, for Linux (x86_64)
--
-- Host: matteogodzilla.ddns.net    Database: QuickDraw
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
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ActiveContract` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idContract` int(11) NOT NULL,
  `startTime` int(11) NOT NULL,
  `reward` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `ActiveAdventure_Adventure_FK` (`idContract`),
  CONSTRAINT `ActiveAdventure_Adventure_FK` FOREIGN KEY (`idContract`) REFERENCES `Contract` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
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
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Bullet` (
  `type` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(128) NOT NULL,
  `capacity` int(11) NOT NULL,
  PRIMARY KEY (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Bullet`
--

LOCK TABLES `Bullet` WRITE;
/*!40000 ALTER TABLE `Bullet` DISABLE KEYS */;
/*!40000 ALTER TABLE `Bullet` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `BulletShop`
--

DROP TABLE IF EXISTS `BulletShop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `BulletShop` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idBullet` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `cost` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `BulletShop_Bullet_FK` (`idBullet`),
  CONSTRAINT `BulletShop_Bullet_FK` FOREIGN KEY (`idBullet`) REFERENCES `Bullet` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `BulletShop`
--

LOCK TABLES `BulletShop` WRITE;
/*!40000 ALTER TABLE `BulletShop` DISABLE KEYS */;
/*!40000 ALTER TABLE `BulletShop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Contract`
--

DROP TABLE IF EXISTS `Contract`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Contract` (
  `name` varchar(100) NOT NULL,
  `requiredTime` int(11) NOT NULL,
  `requiredPower` int(11) NOT NULL DEFAULT 0,
  `maxMercenaries` int(11) NOT NULL,
  `minReward` int(11) NOT NULL,
  `maxReward` int(11) NOT NULL,
  `startCost` int(11) NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Contract`
--

LOCK TABLES `Contract` WRITE;
/*!40000 ALTER TABLE `Contract` DISABLE KEYS */;
/*!40000 ALTER TABLE `Contract` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `EmployedMercenary`
--

DROP TABLE IF EXISTS `EmployedMercenary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `EmployedMercenary` (
  `idPlayer` int(11) NOT NULL,
  `idMercenary` int(11) NOT NULL,
  `id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `EmployedMercenary_Player_FK` (`idPlayer`),
  KEY `EmployedMercenary_Mercenary_FK` (`idMercenary`),
  CONSTRAINT `EmployedMercenary_Mercenary_FK` FOREIGN KEY (`idMercenary`) REFERENCES `Mercenary` (`id`),
  CONSTRAINT `EmployedMercenary_Player_FK` FOREIGN KEY (`idPlayer`) REFERENCES `Player` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `EmployedMercenary`
--

LOCK TABLES `EmployedMercenary` WRITE;
/*!40000 ALTER TABLE `EmployedMercenary` DISABLE KEYS */;
/*!40000 ALTER TABLE `EmployedMercenary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Friendship`
--

DROP TABLE IF EXISTS `Friendship`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40000 ALTER TABLE `Friendship` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Login`
--

DROP TABLE IF EXISTS `Login`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Login` (
  `email` varchar(254) NOT NULL,
  `password` binary(60) NOT NULL,
  `idPlayer` int(11) NOT NULL,
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
/*!40000 ALTER TABLE `Login` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Medikit`
--

DROP TABLE IF EXISTS `Medikit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Medikit` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `healthRecover` int(11) NOT NULL,
  `description` varchar(128) DEFAULT NULL,
  `capacity` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Medikit`
--

LOCK TABLES `Medikit` WRITE;
/*!40000 ALTER TABLE `Medikit` DISABLE KEYS */;
/*!40000 ALTER TABLE `Medikit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MedikitShop`
--

DROP TABLE IF EXISTS `MedikitShop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MedikitShop` (
  `idMedikit` int(11) NOT NULL,
  `cost` int(11) NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `quantity` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `MedikitShop_Medikit_FK` (`idMedikit`),
  CONSTRAINT `MedikitShop_Medikit_FK` FOREIGN KEY (`idMedikit`) REFERENCES `Medikit` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MedikitShop`
--

LOCK TABLES `MedikitShop` WRITE;
/*!40000 ALTER TABLE `MedikitShop` DISABLE KEYS */;
/*!40000 ALTER TABLE `MedikitShop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Mercenary`
--

DROP TABLE IF EXISTS `Mercenary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Mercenary` (
  `id` int(11) NOT NULL,
  `name` varchar(128) NOT NULL,
  `power` int(11) NOT NULL,
  `requiredLevel` int(11) NOT NULL,
  `employmentCost` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Mercenary`
--

LOCK TABLES `Mercenary` WRITE;
/*!40000 ALTER TABLE `Mercenary` DISABLE KEYS */;
/*!40000 ALTER TABLE `Mercenary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Player`
--

DROP TABLE IF EXISTS `Player`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Player` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `health` int(11) NOT NULL,
  `maxHealth` int(11) NOT NULL,
  `exp` int(11) NOT NULL,
  `money` int(11) NOT NULL,
  `bounty` int(11) NOT NULL,
  `username` varchar(128) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Player`
--

LOCK TABLES `Player` WRITE;
/*!40000 ALTER TABLE `Player` DISABLE KEYS */;
/*!40000 ALTER TABLE `Player` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PlayerBullet`
--

DROP TABLE IF EXISTS `PlayerBullet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = utf8 */;
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
/*!40000 ALTER TABLE `PlayerMedikit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PlayerUpgrade`
--

DROP TABLE IF EXISTS `PlayerUpgrade`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40000 ALTER TABLE `PlayerUpgrade` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `PlayerWeapon`
--

DROP TABLE IF EXISTS `PlayerWeapon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40000 ALTER TABLE `PlayerWeapon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `UpgradeShop`
--

DROP TABLE IF EXISTS `UpgradeShop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UpgradeShop` (
  `idUpgrade` int(11) NOT NULL AUTO_INCREMENT,
  `type` int(11) NOT NULL,
  `value` int(11) NOT NULL,
  `cost` int(11) NOT NULL,
  PRIMARY KEY (`idUpgrade`)
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `UpgradeShop`
--

LOCK TABLES `UpgradeShop` WRITE;
/*!40000 ALTER TABLE `UpgradeShop` DISABLE KEYS */;
/*!40000 ALTER TABLE `UpgradeShop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `UpgradeTypes`
--

DROP TABLE IF EXISTS `UpgradeTypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UpgradeTypes` (
  `id` int(11) NOT NULL,
  `Description` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `UpgradeTypes`
--

LOCK TABLES `UpgradeTypes` WRITE;
/*!40000 ALTER TABLE `UpgradeTypes` DISABLE KEYS */;
/*!40000 ALTER TABLE `UpgradeTypes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Weapon`
--

DROP TABLE IF EXISTS `Weapon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Weapon` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bulletType` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `damage` int(11) NOT NULL,
  `cost` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `Weapon_Bullet_FK` (`bulletType`),
  CONSTRAINT `Weapon_Bullet_FK` FOREIGN KEY (`bulletType`) REFERENCES `Bullet` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Weapon`
--

LOCK TABLES `Weapon` WRITE;
/*!40000 ALTER TABLE `Weapon` DISABLE KEYS */;
/*!40000 ALTER TABLE `Weapon` ENABLE KEYS */;
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

-- Dump completed on 2025-04-05 19:26:46

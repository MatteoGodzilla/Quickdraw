/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19  Distrib 10.11.13-MariaDB, for Linux (x86_64)
--
-- Host: matteogodzilla.net    Database: QuickDraw
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
  CONSTRAINT `AssignedMercenary_ActiveAdventure_FK` FOREIGN KEY (`idActiveContract`) REFERENCES `StartedContract` (`id`) ON DELETE CASCADE,
  CONSTRAINT `AssignedMercenary_EmployedMercenary_FK` FOREIGN KEY (`idEmployedMercenary`) REFERENCES `EmployedMercenary` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Bandit`
--

DROP TABLE IF EXISTS `Bandit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Bandit` (
  `id` int(11) NOT NULL,
  `name` varchar(128) NOT NULL DEFAULT '"Bandit"',
  `hp` int(11) NOT NULL DEFAULT 100,
  `minDamage` int(11) NOT NULL DEFAULT 1,
  `maxDamage` int(11) NOT NULL DEFAULT 10,
  `minExp` int(11) NOT NULL DEFAULT 1,
  `maxExp` int(11) NOT NULL DEFAULT 10,
  `minSpeed` int(11) NOT NULL DEFAULT 500,
  `maxSpeed` int(11) NOT NULL DEFAULT 1000,
  `minMoney` int(11) NOT NULL DEFAULT 1,
  `maxMoney` int(11) NOT NULL DEFAULT 100,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `BanditIstance`
--

DROP TABLE IF EXISTS `BanditIstance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `BanditIstance` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idBandit` int(11) NOT NULL,
  `idRequest` int(11) NOT NULL,
  `defeated` tinyint(1) NOT NULL DEFAULT 0,
  `frozen` tinyint(1) DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `BanditIstance_Bandit_FK` (`idBandit`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=251 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `BanditPool`
--

DROP TABLE IF EXISTS `BanditPool`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `BanditPool` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `banditId` int(11) NOT NULL DEFAULT 1,
  `spawnChance` int(11) NOT NULL DEFAULT 100,
  `levelRequired` int(11) NOT NULL DEFAULT 1,
  `minSpawn` int(11) NOT NULL DEFAULT 1,
  `maxSpawn` int(11) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `BanditPool_Bandit_FK` (`banditId`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `BaseStats`
--

DROP TABLE IF EXISTS `BaseStats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `BaseStats` (
  `upgradeType` int(11) NOT NULL,
  `baseValue` int(11) NOT NULL DEFAULT 1,
  `evaluation` enum('INCREMENT','MULTIPLIER') NOT NULL DEFAULT 'INCREMENT',
  PRIMARY KEY (`upgradeType`),
  CONSTRAINT `BaseStats_UpgradeTypes_FK` FOREIGN KEY (`upgradeType`) REFERENCES `UpgradeTypes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
  `requiredLevel` smallint(6) DEFAULT 1,
  PRIMARY KEY (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
  `timestamp` bigint(20) unsigned DEFAULT NULL COMMENT 'UTC timestamp in seconds',
  PRIMARY KEY (`id`),
  KEY `Duel_Player_FK` (`idPlayerA`),
  KEY `Duel_Player_FK_1` (`idPlayerB`),
  CONSTRAINT `Duel_Player_FK` FOREIGN KEY (`idPlayerA`) REFERENCES `Player` (`id`),
  CONSTRAINT `Duel_Player_FK_1` FOREIGN KEY (`idPlayerB`) REFERENCES `Player` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Friendship`
--

DROP TABLE IF EXISTS `Friendship`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Friendship` (
  `idPlayerFrom` int(11) NOT NULL,
  `idPlayerTo` int(11) NOT NULL,
  PRIMARY KEY (`idPlayerFrom`,`idPlayerTo`),
  KEY `Friendship_Player_FK_1` (`idPlayerTo`),
  CONSTRAINT `Friendship_Player_FK` FOREIGN KEY (`idPlayerFrom`) REFERENCES `Player` (`id`),
  CONSTRAINT `Friendship_Player_FK_1` FOREIGN KEY (`idPlayerTo`) REFERENCES `Player` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Level`
--

DROP TABLE IF EXISTS `Level`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Level` (
  `level` int(11) NOT NULL,
  `expRequired` int(11) NOT NULL,
  PRIMARY KEY (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
  `requiredLevel` smallint(6) DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Player`
--

DROP TABLE IF EXISTS `Player`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `Player` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `health` int(11) NOT NULL DEFAULT 100,
  `exp` int(11) NOT NULL DEFAULT 0,
  `money` int(11) NOT NULL DEFAULT 0,
  `bounty` int(11) NOT NULL DEFAULT 0,
  `username` varchar(128) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
  CONSTRAINT `PlayerUpgrade_UpgradeShop_FK` FOREIGN KEY (`idUpgrade`) REFERENCES `UpgradeShop` (`idUpgrade`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
-- Table structure for table `PoolRequest`
--

DROP TABLE IF EXISTS `PoolRequest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `PoolRequest` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idPlayer` int(11) NOT NULL,
  `expireTime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `PoolRequest_Player_FK` (`idPlayer`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
  `won` int(11) NOT NULL COMMENT '0 = Won, 1 = Lost, 2 = Draw',
  `idWeaponUsed` int(11) NOT NULL,
  `bulletsUsed` int(11) NOT NULL DEFAULT 1,
  `damage` int(11) NOT NULL,
  PRIMARY KEY (`idDuel`,`roundNumber`,`idPlayer`),
  CONSTRAINT `Round_Duel_FK` FOREIGN KEY (`idDuel`) REFERENCES `Duel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `StartedContract`
--

DROP TABLE IF EXISTS `StartedContract`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `StartedContract` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `idContract` int(11) NOT NULL,
  `idPlayer` int(11) NOT NULL,
  `startTime` bigint(20) unsigned NOT NULL COMMENT 'UTC Unix timestamp in seconds',
  `redeemed` tinyint(1) NOT NULL DEFAULT 0,
  `successful` tinyint(1) NOT NULL DEFAULT 0,
  `reward` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `ActiveAdventure_Adventure_FK` (`idContract`),
  KEY `StartedContract_Player_FK` (`idPlayer`),
  CONSTRAINT `ActiveAdventure_Adventure_FK` FOREIGN KEY (`idContract`) REFERENCES `Contract` (`id`),
  CONSTRAINT `StartedContract_Player_FK` FOREIGN KEY (`idPlayer`) REFERENCES `Player` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=267 DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
  `modifier` int(11) DEFAULT 1,
  PRIMARY KEY (`idUpgrade`),
  KEY `UpgradeShop_UpgradeTypes_FK` (`type`),
  CONSTRAINT `UpgradeShop_UpgradeTypes_FK` FOREIGN KEY (`type`) REFERENCES `UpgradeTypes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
  `requiredLevel` smallint(6) DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `Weapon_Bullet_FK` (`bulletType`),
  CONSTRAINT `Weapon_Bullet_FK` FOREIGN KEY (`bulletType`) REFERENCES `Bullet` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf16 COLLATE=utf16_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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

-- Dump completed on 2025-09-01 11:18:50

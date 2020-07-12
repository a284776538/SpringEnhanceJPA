/*
Navicat MySQL Data Transfer

Source Server         : 测试
Source Server Version : 50624
Source Host           : 47.107.87.11:3307
Source Database       : tp

Target Server Type    : MYSQL
Target Server Version : 50624
File Encoding         : 65001

Date: 2020-07-12 16:13:42
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for distributed_lock
-- ----------------------------
DROP TABLE IF EXISTS `distributed_lock`;
CREATE TABLE `distributed_lock` (
  `id` varchar(100) CHARACTER SET utf8 NOT NULL,
  `keyword` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `lock_time` datetime DEFAULT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  `is_delete` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_id` (`id`) USING BTREE,
  UNIQUE KEY `key_unuque` (`keyword`) USING BTREE,
  KEY `key_lock_time` (`lock_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

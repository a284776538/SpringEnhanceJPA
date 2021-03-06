/*
Navicat MySQL Data Transfer

Source Server         : 测试
Source Server Version : 50624
Source Host           : 47.107.87.11:3307
Source Database       : tp

Target Server Type    : MYSQL
Target Server Version : 50624
File Encoding         : 65001

Date: 2020-07-12 16:13:52
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for ejpa_slow_query
-- ----------------------------
DROP TABLE IF EXISTS `ejpa_slow_query`;
CREATE TABLE `ejpa_slow_query` (
  `id` varchar(255) NOT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  `is_delete` datetime DEFAULT NULL,
  `time` int(11) NOT NULL,
  `sql` varchar(255) NOT NULL,
  `method` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_id` (`id`) USING BTREE,
  KEY `key_method` (`method`) USING BTREE,
  KEY `key_time` (`time`) USING BTREE,
  KEY `key_sql` (`sql`,`method`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

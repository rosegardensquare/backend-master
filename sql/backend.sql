/*
Navicat MySQL Data Transfer

Source Server         : aliyun
Source Server Version : 50731
Source Host           : 123.57.29.77:3306
Source Database       : backend

Target Server Type    : MYSQL
Target Server Version : 50731
File Encoding         : 65001

Date: 2021-01-26 23:51:39
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for common_user
-- ----------------------------
DROP TABLE IF EXISTS `common_user`;
CREATE TABLE `common_user` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL COMMENT '用户名',
  `nickname` varchar(255) DEFAULT NULL COMMENT '昵称',
  `tel` varchar(255) DEFAULT NULL COMMENT '手机号',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `password` varchar(20) DEFAULT NULL COMMENT '密码',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别（ 1：男；2：女）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of common_user
-- ----------------------------
INSERT INTO `common_user` VALUES ('086480ab533b4a0c8fdc2b9af8675da3', '112', '123', '13685968596', '2020-12-14', null, '0', '2020-12-19 00:00:12', '2020-12-19 00:00:12', '1');
INSERT INTO `common_user` VALUES ('465b155bca7f461b9508db1807d75165', '张帅3', 'black', '13685968596', '2020-12-25', null, '1', '2020-12-12 00:18:18', '2020-12-12 00:18:18', '1');
INSERT INTO `common_user` VALUES ('49587c8990a040578ae4441921e7d65a', '张帅', 'black', '13685968596', '2020-12-29', null, '1', '2020-12-12 00:18:18', '2020-12-12 00:18:18', '0');

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` varchar(255) NOT NULL,
  `permission_name` varchar(50) DEFAULT NULL COMMENT '菜单名称',
  `icon` varchar(50) DEFAULT NULL COMMENT '图标代码',
  `path` varchar(50) DEFAULT NULL COMMENT '路径',
  `parentId` varchar(255) DEFAULT NULL COMMENT '父id',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `del` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  `sort` varchar(50) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES ('1', '用户管理', 'el-icon-s-claim', null, null, '2020-12-18 09:25:48', '2020-12-18 09:25:48', '0', '4');
INSERT INTO `sys_permission` VALUES ('2', '用户列表', 'el-icon-s-tools', '/list', '1', '2020-12-18 09:26:44', '2020-12-18 09:26:44', '0', '2');
INSERT INTO `sys_permission` VALUES ('3', '系统管理', 'el-icon-s-promotion', null, null, '2020-12-18 09:26:59', '2020-12-18 09:26:59', '0', '3');
INSERT INTO `sys_permission` VALUES ('4', '角色权限', 'el-icon-picture', '/roleList', '3', '2020-12-18 09:27:19', '2020-12-18 09:27:19', '0', '5');
INSERT INTO `sys_permission` VALUES ('5', '用户列表', 'el-icon-s-tools', '/userList', '3', '2020-12-18 19:19:29', '2020-12-18 19:19:29', '0', '1');
INSERT INTO `sys_permission` VALUES ('6', '权限列表', 'el-icon-s-tools', '/permiList', '3', '2021-01-25 11:51:20', '2021-01-25 11:51:16', '0', '2');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` varchar(255) NOT NULL,
  `role_code` varchar(50) DEFAULT NULL COMMENT '角色编码',
  `role_name` varchar(30) DEFAULT NULL COMMENT '角色名称或角色权限',
  `parent_id` varchar(255) DEFAULT NULL COMMENT '父id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('1', 'ROLE_ADMIN', '管理员', null);
INSERT INTO `sys_role` VALUES ('6', 'ROLE_USER', '用户1', null);

-- ----------------------------
-- Table structure for sys_role_perm
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_perm`;
CREATE TABLE `sys_role_perm` (
  `id` varchar(255) NOT NULL,
  `role_id` varchar(255) DEFAULT NULL,
  `perm_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_role_perm
-- ----------------------------
INSERT INTO `sys_role_perm` VALUES ('0049a3f2-f479-4040-bf58-c548ef532784', '6', '5');
INSERT INTO `sys_role_perm` VALUES ('1', '1', '1');
INSERT INTO `sys_role_perm` VALUES ('2', '1', '2');
INSERT INTO `sys_role_perm` VALUES ('3', '1', '3');
INSERT INTO `sys_role_perm` VALUES ('3aa5aec0-3091-452b-aa61-355a0b6a90e7', '6', '4');
INSERT INTO `sys_role_perm` VALUES ('4', '1', '4');
INSERT INTO `sys_role_perm` VALUES ('5', '1', '5');
INSERT INTO `sys_role_perm` VALUES ('8', '1', '6');
INSERT INTO `sys_role_perm` VALUES ('a8710d2c-a9cf-4c21-ab34-992bd21d580f', '6', '3');
INSERT INTO `sys_role_perm` VALUES ('fbac6d5c-6c9c-46a6-b187-b99ae44deda0', '6', '6');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` varchar(255) NOT NULL,
  `user_name` varchar(30) DEFAULT NULL,
  `pass_word` varchar(100) DEFAULT NULL,
  `description` varchar(50) DEFAULT NULL,
  `state` bigint(1) DEFAULT NULL,
  `del` bigint(1) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `real_pwd` varchar(50) DEFAULT NULL COMMENT '真实密码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1', 'zs', '$2a$10$2ixWWTUmOztnFHvcdmpZHOsHzFnB.KFE7eZmyhs4TPUpKAdda4hPO', null, null, '0', '2020-12-18 18:03:27', '2020-12-18 18:03:37', '666666');
INSERT INTO `sys_user` VALUES ('2', 'ls', '$2a$10$ftDcCXs54S0poQMwt81uwuA3QkwJZULxOOSeBFA5PPkRwArJfrwhK', '', null, '0', '2020-12-18 18:03:27', '2020-12-18 18:03:37', '123456');
INSERT INTO `sys_user` VALUES ('4', null, null, null, null, null, '2021-01-13 15:44:19', '2021-01-13 15:44:19', null);
INSERT INTO `sys_user` VALUES ('b5921da01c824aef83bde6283dd5bfeb', 'dl', '$2a$10$NL06nqnD.Jf69TwdfTZR3.1eniM85pYi.1f5CtVIQRmp/49ExiiEu', null, null, '1', '2020-12-19 19:21:05', '2020-12-19 19:21:05', '666666');
INSERT INTO `sys_user` VALUES ('bc1cde74b2ae49dc95cfb0c4fb54cdf3', 'er', '$2a$10$MhGcBVm1nvQLC2xz5W/FuusjzPue4oVTc2uIG1AfX7CzY3SXxoe/.', null, null, '0', '2020-12-19 00:10:48', '2020-12-19 00:10:48', null);
INSERT INTO `sys_user` VALUES ('c972b920d4a6419a818da1038727e020', 'ol', '$2a$10$h6A8YJYaxu/2XvzrApQ8oeBt2ESZseVYeaxmeIl3wVAiU7c9n1YY2', null, null, '0', '2020-12-19 19:00:29', '2020-12-19 19:00:29', '666666');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` varchar(255) NOT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `role_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES ('1', '1', '1');
INSERT INTO `sys_user_role` VALUES ('2', '2', '6');

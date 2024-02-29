CREATE TABLE `api_log` (
                           `seq` BIGINT(19) NOT NULL AUTO_INCREMENT,
                           `txid` VARCHAR(50) NOT NULL DEFAULT '' COLLATE 'utf8mb4_unicode_ci',
                           `username` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci',
                           `method` CHAR(5) NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci',
                           `uri` TEXT NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci',
                           `code` SMALLINT(5) NULL DEFAULT '0',
                           `params` TEXT NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci',
                           `req_datetime` DATETIME NULL DEFAULT NULL,
                           `res_datetime` DATETIME NULL DEFAULT NULL,
                           PRIMARY KEY (`seq`) USING BTREE,
                           UNIQUE INDEX `txid` (`txid`) USING BTREE
)
    COMMENT='게이트웨이 라우트 로그'
    COLLATE='utf8mb4_unicode_ci'
    ENGINE=InnoDB
    AUTO_INCREMENT=38
;

CREATE TABLE `api_route` (
                             `route_id` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_unicode_ci',
                             `service` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci',
                             `path` VARCHAR(100) NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci',
                             `method` VARCHAR(6) NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci',
                             `desc` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci',
                             `permit_all` TINYINT(3) NULL DEFAULT NULL,
                             PRIMARY KEY (`route_id`) USING BTREE,
                             INDEX `service` (`service`) USING BTREE,
                             CONSTRAINT `FK_api_route_api_services` FOREIGN KEY (`service`) REFERENCES `api_service` (`service`) ON UPDATE CASCADE ON DELETE CASCADE
)
    COMMENT='게이트웨이 라우트'
    COLLATE='utf8mb4_unicode_ci'
    ENGINE=InnoDB
;

CREATE TABLE `api_service` (
                               `service` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_unicode_ci',
                               `uri` VARCHAR(100) NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci',
                               PRIMARY KEY (`service`) USING BTREE
)
    COLLATE='utf8mb4_unicode_ci'
    ENGINE=InnoDB
;

CREATE TABLE `auth_role` (
                             `role` VARCHAR(20) NOT NULL COLLATE 'utf8mb4_unicode_ci',
                             `role_desc` VARCHAR(100) NOT NULL COLLATE 'utf8mb4_unicode_ci',
                             PRIMARY KEY (`role`) USING BTREE
)
    COMMENT='롤'
    COLLATE='utf8mb4_unicode_ci'
    ENGINE=InnoDB
;

CREATE TABLE `auth_role_mapping` (
                                     `auth_role` VARCHAR(20) NOT NULL COLLATE 'utf8mb4_unicode_ci',
                                     `route_id` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_unicode_ci',
                                     PRIMARY KEY (`auth_role`, `route_id`) USING BTREE,
                                     INDEX `FK_auth_role_mapping_api_route` (`route_id`) USING BTREE,
                                     CONSTRAINT `FK_auth_role_mapping_api_route` FOREIGN KEY (`route_id`) REFERENCES `api_route` (`route_id`) ON UPDATE CASCADE ON DELETE CASCADE,
                                     CONSTRAINT `FK_auth_role_mapping_auth_role` FOREIGN KEY (`auth_role`) REFERENCES `auth_role` (`role`) ON UPDATE CASCADE ON DELETE CASCADE
)
    COLLATE='utf8mb4_unicode_ci'
    ENGINE=InnoDB
;

CREATE TABLE `auth_user` (
                             `seq` BIGINT(19) NOT NULL AUTO_INCREMENT,
                             `uid` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_unicode_ci',
                             `name` VARCHAR(10) NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci',
                             `email` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci',
                             `title` VARCHAR(10) NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci',
                             `is_admin` TINYINT(3) NULL DEFAULT '0',
                             `created_date` DATETIME NULL DEFAULT NULL,
                             `last_login_date` DATETIME NULL DEFAULT NULL,
                             PRIMARY KEY (`seq`) USING BTREE,
                             UNIQUE INDEX `uid` (`uid`) USING BTREE
)
    COMMENT='인증 유저 정보'
    COLLATE='utf8mb4_unicode_ci'
    ENGINE=InnoDB
    AUTO_INCREMENT=6
;


CREATE TABLE `auth_user_service` (
                                     `uid` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_unicode_ci',
                                     `service` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_unicode_ci',
                                     `role` VARCHAR(20) NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci',
                                     PRIMARY KEY (`uid`, `service`) USING BTREE,
                                     INDEX `FK_auth_user_service_auth_role` (`role`) USING BTREE,
                                     INDEX `FK_auth_user_service_api_service` (`service`) USING BTREE,
                                     CONSTRAINT `FK_auth_user_service_api_service` FOREIGN KEY (`service`) REFERENCES `api_service` (`service`) ON UPDATE CASCADE ON DELETE CASCADE,
                                     CONSTRAINT `FK_auth_user_service_auth_role` FOREIGN KEY (`role`) REFERENCES `auth_role` (`role`) ON UPDATE CASCADE ON DELETE CASCADE,
                                     CONSTRAINT `FK_auth_user_service_auth_user` FOREIGN KEY (`uid`) REFERENCES `auth_user` (`uid`) ON UPDATE CASCADE ON DELETE CASCADE
)
    COLLATE='utf8mb4_unicode_ci'
    ENGINE=InnoDB
;

# --- !Ups

CREATE TABLE IF NOT EXISTS `kit_identity` (
    `singleton_id` TINYINT NOT NULL,
    `kit_id` CHAR(36) NOT NULL,
    `legacy_id` VARCHAR(255) NULL,
    `source` VARCHAR(32) NOT NULL,
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME NOT NULL,
    PRIMARY KEY (`singleton_id`),
    UNIQUE KEY `uk_kit_identity_kit_id` (`kit_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

# --- !Downs

DROP TABLE IF EXISTS `kit_identity`;

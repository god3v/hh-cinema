-- cinema_db.movie definition

CREATE TABLE `movie` (
  `movie_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `movie_rating` enum('G','PG','PG13','R') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `release_date` date NOT NULL,
  `thumbnail_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `running_time` int unsigned NOT NULL,
  `genre` enum('ACTION','COMEDY','DRAMA','HORROR','ROMANCE') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint unsigned NOT NULL,
  `created_at` timestamp NOT NULL,
  `updated_by` bigint unsigned,
  `updated_at` timestamp,
  PRIMARY KEY (`movie_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- cinema_db.screen definition

CREATE TABLE `screen` (
  `screen_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `screen_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint unsigned NOT NULL,
  `created_at` timestamp NOT NULL,
  `updated_by` bigint unsigned,
  `updated_at` timestamp,
  PRIMARY KEY (`screen_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- cinema_db.screen_seat definition

CREATE TABLE `screen_seat` (
  `screen_seat_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `screen_id` bigint unsigned NOT NULL,
  `seat_row` int unsigned NOT NULL,
  `seat_col` int unsigned NOT NULL,
  `created_by` bigint unsigned NOT NULL,
  `created_at` timestamp NOT NULL,
  `updated_by` bigint unsigned,
  `updated_at` timestamp,
  PRIMARY KEY (`screen_seat_id`)
) ENGINE=InnoDB AUTO_INCREMENT=126 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- cinema_db.screening_schedule definition

CREATE TABLE `screening_schedule` (
  `screening_schedule_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `movie_id` bigint unsigned NOT NULL,
  `screen_id` bigint unsigned NOT NULL,
  `started_at` timestamp NOT NULL,
  `ended_at` timestamp NOT NULL,
  `created_by` bigint unsigned NOT NULL,
  `created_at` timestamp NOT NULL,
  `updated_by` bigint unsigned,
  `updated_at` timestamp,
  PRIMARY KEY (`screening_schedule_id`)
) ENGINE=InnoDB AUTO_INCREMENT=501 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- cinema_db.ticket_reservation definition

CREATE TABLE `ticket_reservation` (
  `ticket_reservation_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `screening_schedule_id` bigint unsigned NOT NULL,
  `screen_seat_id` bigint unsigned NOT NULL,
  `user_id` bigint unsigned NOT NULL,
  `created_by` bigint unsigned NOT NULL,
  `created_at` timestamp NOT NULL,
  `updated_by` bigint unsigned,
  `updated_at` timestamp,
  PRIMARY KEY (`ticket_reservation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- cinema_db.`user` definition

CREATE TABLE `user` (
  `user_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `created_by` bigint unsigned NOT NULL,
  `created_at` timestamp NOT NULL,
  `updated_by` bigint unsigned,
  `updated_at` timestamp,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

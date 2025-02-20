CREATE DATABASE media CHARACTER SET utf8mb4;

CREATE TABLE `video_program` (
  `id` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL,
  `title` varchar(254) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(254) COLLATE utf8mb4_unicode_ci DEFAULT "",
  `state` int(4) NOT NULL,
  `themes` varchar(254) COLLATE utf8mb4_unicode_ci DEFAULT "" ,
  `type` varchar(254) COLLATE utf8mb4_unicode_ci NOT NULL,
  `likes` int(10) NOT NULL,
  `user_id` varchar(254) COLLATE utf8mb4_unicode_ci NOT NULL,
  `create_time` bigint(20) NOT NULL,
  `modify_time` bigint(20) NOT NULL,
  `del_flag` varchar(254) COLLATE utf8mb4_unicode_ci NOT NULL,
  `finish_play` int(11) NOT NULL,
  `play` int(11) NOT NULL,
  `video_store_id` varchar(254) COLLATE utf8mb4_unicode_ci DEFAULT "",
  `cover_store_id` varchar(254) COLLATE utf8mb4_unicode_ci  DEFAULT "",
  `ext` varchar(1024) COLLATE utf8mb4_unicode_ci  DEFAULT "",
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


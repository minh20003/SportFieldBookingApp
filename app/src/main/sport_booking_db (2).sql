-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th12 30, 2025 lúc 04:19 PM
-- Phiên bản máy phục vụ: 10.4.32-MariaDB
-- Phiên bản PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `sport_booking_db`
--
CREATE DATABASE IF NOT EXISTS `sport_booking_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `sport_booking_db`;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `bookings`
--

DROP TABLE IF EXISTS `bookings`;
CREATE TABLE `bookings` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `field_id` int(11) DEFAULT NULL,
  `booking_date` date NOT NULL,
  `time_slot_start` time NOT NULL,
  `time_slot_end` time NOT NULL,
  `total_price` decimal(10,2) NOT NULL,
  `status` enum('pending','confirmed','completed','cancelled') DEFAULT 'pending',
  `payment_method` enum('cash','vnpay','momo') NOT NULL DEFAULT 'cash',
  `payment_status` enum('unpaid','paid') NOT NULL DEFAULT 'unpaid',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `bookings`
--

INSERT INTO `bookings` (`id`, `user_id`, `field_id`, `booking_date`, `time_slot_start`, `time_slot_end`, `total_price`, `status`, `payment_method`, `payment_status`, `created_at`) VALUES
(1, 1, 1, '2025-10-20', '17:00:00', '18:00:00', 250000.00, 'pending', 'cash', 'unpaid', '2025-10-19 16:42:09'),
(2, 1, 1, '2025-10-23', '21:00:00', '22:00:00', 200000.00, 'completed', 'cash', 'paid', '2025-10-20 07:34:04'),
(3, 1, 2, '2025-10-23', '20:00:00', '21:00:00', 200000.00, 'cancelled', 'cash', 'unpaid', '2025-10-20 09:42:06'),
(4, 1, 1, '2025-10-24', '16:00:00', '17:00:00', 200000.00, 'pending', 'cash', 'unpaid', '2025-10-20 09:44:30'),
(5, 1, 2, '2025-10-29', '19:00:00', '20:00:00', 200000.00, 'pending', 'cash', 'unpaid', '2025-10-20 10:16:41'),
(6, 2, 1, '2025-10-23', '12:00:00', '13:00:00', 200000.00, 'cancelled', 'cash', 'unpaid', '2025-10-21 07:17:47'),
(7, 2, 1, '2025-10-24', '17:00:00', '18:00:00', 200000.00, 'pending', 'cash', 'unpaid', '2025-10-21 11:18:03'),
(8, 3, 2, '2025-10-24', '12:00:00', '13:00:00', 200000.00, 'completed', 'cash', 'unpaid', '2025-10-23 05:49:14'),
(9, 2, 2, '2025-10-30', '08:00:00', '09:00:00', 200000.00, 'cancelled', 'cash', 'unpaid', '2025-10-27 08:46:01'),
(10, 2, 2, '2025-10-29', '11:00:00', '12:00:00', 200000.00, 'completed', 'cash', 'paid', '2025-10-27 09:05:11'),
(11, 4, 1, '2025-10-31', '12:00:00', '13:00:00', 200000.00, 'completed', 'cash', 'unpaid', '2025-10-27 09:06:42'),
(12, 4, 1, '2025-10-30', '12:00:00', '13:00:00', 200000.00, 'completed', 'cash', 'unpaid', '2025-10-27 14:17:04'),
(13, 2, 3, '2025-10-31', '16:00:00', '17:00:00', 200000.00, 'pending', 'cash', 'unpaid', '2025-10-29 13:37:01'),
(14, 4, 2, '2025-10-31', '20:00:00', '21:00:00', 200000.00, 'completed', 'cash', 'paid', '2025-10-30 06:19:38'),
(15, 4, 1, '2025-11-05', '10:00:00', '11:00:00', 200000.00, 'pending', 'cash', 'unpaid', '2025-10-30 07:12:34'),
(16, 2, 17, '2025-11-05', '17:00:00', '18:00:00', 60000.00, 'pending', 'cash', 'unpaid', '2025-11-04 06:52:43'),
(17, 2, 32, '2025-11-07', '08:00:00', '09:00:00', 120000.00, 'completed', 'cash', 'paid', '2025-11-04 07:21:43'),
(18, 2, 16, '2025-11-07', '20:00:00', '21:00:00', 100000.00, 'cancelled', 'cash', 'unpaid', '2025-11-04 17:21:11'),
(19, 2, 14, '2025-11-07', '17:00:00', '18:00:00', 300000.00, 'completed', 'cash', 'paid', '2025-11-04 17:22:29'),
(20, 5, 29, '2025-11-08', '12:00:00', '13:00:00', 70000.00, 'completed', 'cash', 'paid', '2025-11-04 17:29:21'),
(21, 5, 26, '2025-11-07', '20:00:00', '21:00:00', 100000.00, 'completed', 'cash', 'paid', '2025-11-04 17:46:39'),
(22, 2, 26, '2025-11-09', '17:00:00', '18:00:00', 120000.00, 'completed', 'cash', 'paid', '2025-11-07 16:45:27'),
(23, 2, 23, '2025-11-22', '17:00:00', '18:00:00', 180000.00, 'pending', 'cash', 'unpaid', '2025-11-07 17:02:58');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `chat_messages`
--

DROP TABLE IF EXISTS `chat_messages`;
CREATE TABLE `chat_messages` (
  `id` int(11) NOT NULL,
  `room_id` int(11) NOT NULL,
  `sender_id` int(11) NOT NULL,
  `message` text NOT NULL,
  `is_read` tinyint(1) NOT NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `chat_messages`
--

INSERT INTO `chat_messages` (`id`, `room_id`, `sender_id`, `message`, `is_read`, `created_at`) VALUES
(1, 1, 5, 'san ban con can ng khong', 0, '2025-11-07 15:43:27'),
(2, 2, 5, 'san ban con ng khong', 1, '2025-11-07 15:43:51'),
(3, 2, 2, 'con ban oi', 1, '2025-11-07 16:24:05'),
(4, 2, 5, 'ok luon b oi', 1, '2025-11-07 16:27:07'),
(5, 3, 7, 'anh bạn cho tôi chơi với', 0, '2025-11-07 17:01:11'),
(6, 2, 2, '01iwns téttyty', 1, '2025-11-07 17:03:57'),
(7, 2, 5, 'alo an', 1, '2025-11-07 17:41:19'),
(8, 2, 5, 'étttt', 1, '2025-11-07 17:41:39'),
(9, 2, 5, 'okhuytgfrded', 1, '2025-11-07 17:42:08'),
(10, 2, 2, 'jbvjbvh', 1, '2025-11-07 17:42:15'),
(11, 2, 5, 'uygbuyguyg', 1, '2025-11-07 17:42:24'),
(12, 4, 8, 'heloo a zai', 1, '2025-11-07 18:20:05'),
(13, 4, 4, 'ksbdhsh', 1, '2025-11-07 18:20:24'),
(14, 4, 8, 'dgnehmry', 1, '2025-11-07 18:20:30'),
(15, 4, 8, '????????????', 1, '2025-11-07 18:20:40'),
(16, 4, 4, '????', 1, '2025-11-07 18:20:50');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `chat_rooms`
--

DROP TABLE IF EXISTS `chat_rooms`;
CREATE TABLE `chat_rooms` (
  `id` int(11) NOT NULL,
  `post_id` int(11) DEFAULT NULL COMMENT 'Liên kết với find_teammates post',
  `user1_id` int(11) NOT NULL,
  `user2_id` int(11) NOT NULL,
  `last_message` text DEFAULT NULL,
  `last_message_time` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `chat_rooms`
--

INSERT INTO `chat_rooms` (`id`, `post_id`, `user1_id`, `user2_id`, `last_message`, `last_message_time`, `created_at`) VALUES
(1, 1, 1, 5, 'san ban con can ng khong', '2025-11-07 15:43:27', '2025-11-07 15:43:12'),
(2, 7, 2, 5, 'uygbuyguyg', '2025-11-07 17:42:24', '2025-11-07 15:43:43'),
(3, 1, 1, 7, 'anh bạn cho tôi chơi với', '2025-11-07 17:01:11', '2025-11-07 17:01:01'),
(4, 9, 4, 8, '????', '2025-11-07 18:20:50', '2025-11-07 18:19:57');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `field_prices`
--

DROP TABLE IF EXISTS `field_prices`;
CREATE TABLE `field_prices` (
  `id` int(11) NOT NULL,
  `field_id` int(11) DEFAULT NULL,
  `time_slot` time NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `is_peak_hour` tinyint(1) DEFAULT 0,
  `day_of_week` enum('monday','tuesday','wednesday','thursday','friday','saturday','sunday','all') DEFAULT 'all'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `field_prices`
--

INSERT INTO `field_prices` (`id`, `field_id`, `time_slot`, `price`, `is_peak_hour`, `day_of_week`) VALUES
(2, 1, '10:00:00', 300000.00, 0, 'wednesday'),
(3, 4, '06:00:00', 200000.00, 0, 'all'),
(4, 4, '08:00:00', 250000.00, 1, 'all'),
(5, 4, '17:00:00', 300000.00, 1, 'all'),
(6, 4, '20:00:00', 250000.00, 1, 'all'),
(7, 4, '22:00:00', 200000.00, 0, 'all'),
(8, 5, '06:00:00', 180000.00, 0, 'all'),
(9, 5, '08:00:00', 220000.00, 1, 'all'),
(10, 5, '17:00:00', 280000.00, 1, 'all'),
(11, 5, '20:00:00', 220000.00, 1, 'all'),
(12, 6, '06:00:00', 150000.00, 0, 'all'),
(13, 6, '08:00:00', 200000.00, 1, 'all'),
(14, 6, '17:00:00', 250000.00, 1, 'all'),
(15, 6, '20:00:00', 200000.00, 1, 'all'),
(16, 7, '06:00:00', 220000.00, 0, 'all'),
(17, 7, '17:00:00', 300000.00, 1, 'all'),
(18, 7, '20:00:00', 280000.00, 1, 'all'),
(19, 8, '06:00:00', 200000.00, 0, 'all'),
(20, 8, '17:00:00', 280000.00, 1, 'all'),
(21, 8, '20:00:00', 250000.00, 1, 'all'),
(22, 9, '06:00:00', 250000.00, 0, 'all'),
(23, 9, '08:00:00', 300000.00, 1, 'all'),
(24, 9, '17:00:00', 350000.00, 1, 'all'),
(25, 9, '20:00:00', 300000.00, 1, 'all'),
(26, 10, '06:00:00', 150000.00, 0, 'all'),
(27, 10, '08:00:00', 180000.00, 1, 'all'),
(28, 10, '17:00:00', 200000.00, 1, 'all'),
(29, 10, '20:00:00', 180000.00, 1, 'all'),
(30, 11, '06:00:00', 80000.00, 0, 'all'),
(31, 11, '17:00:00', 120000.00, 1, 'all'),
(32, 11, '20:00:00', 100000.00, 1, 'all'),
(33, 12, '06:00:00', 180000.00, 0, 'all'),
(34, 12, '17:00:00', 250000.00, 1, 'all'),
(35, 12, '20:00:00', 220000.00, 1, 'all'),
(36, 13, '06:00:00', 100000.00, 0, 'all'),
(37, 13, '08:00:00', 120000.00, 1, 'all'),
(38, 13, '17:00:00', 150000.00, 1, 'all'),
(39, 13, '20:00:00', 130000.00, 1, 'all'),
(40, 14, '06:00:00', 200000.00, 0, 'all'),
(41, 14, '08:00:00', 250000.00, 1, 'all'),
(42, 14, '17:00:00', 300000.00, 1, 'all'),
(43, 14, '20:00:00', 250000.00, 1, 'all'),
(44, 15, '06:00:00', 120000.00, 0, 'all'),
(45, 15, '17:00:00', 180000.00, 1, 'all'),
(46, 15, '20:00:00', 150000.00, 1, 'all'),
(47, 16, '06:00:00', 80000.00, 0, 'all'),
(48, 16, '17:00:00', 120000.00, 1, 'all'),
(49, 16, '20:00:00', 100000.00, 1, 'all'),
(50, 17, '06:00:00', 40000.00, 0, 'all'),
(51, 17, '08:00:00', 50000.00, 1, 'all'),
(52, 17, '17:00:00', 60000.00, 1, 'all'),
(53, 17, '20:00:00', 50000.00, 1, 'all'),
(54, 18, '06:00:00', 50000.00, 0, 'all'),
(55, 18, '17:00:00', 70000.00, 1, 'all'),
(56, 18, '20:00:00', 60000.00, 1, 'all'),
(57, 19, '06:00:00', 45000.00, 0, 'all'),
(58, 19, '17:00:00', 65000.00, 1, 'all'),
(59, 19, '20:00:00', 55000.00, 1, 'all'),
(60, 20, '06:00:00', 150000.00, 0, 'all'),
(61, 20, '08:00:00', 200000.00, 1, 'all'),
(62, 20, '17:00:00', 250000.00, 1, 'all'),
(63, 20, '20:00:00', 200000.00, 1, 'all'),
(64, 21, '06:00:00', 180000.00, 0, 'all'),
(65, 21, '17:00:00', 250000.00, 1, 'all'),
(66, 21, '20:00:00', 220000.00, 1, 'all'),
(67, 22, '06:00:00', 200000.00, 0, 'all'),
(68, 22, '17:00:00', 280000.00, 1, 'all'),
(69, 22, '20:00:00', 250000.00, 1, 'all'),
(70, 23, '06:00:00', 120000.00, 0, 'all'),
(71, 23, '17:00:00', 180000.00, 1, 'all'),
(72, 23, '20:00:00', 160000.00, 1, 'all'),
(73, 24, '06:00:00', 80000.00, 0, 'all'),
(74, 24, '08:00:00', 100000.00, 1, 'all'),
(75, 24, '17:00:00', 120000.00, 1, 'all'),
(76, 24, '20:00:00', 100000.00, 1, 'all'),
(77, 25, '06:00:00', 60000.00, 0, 'all'),
(78, 25, '17:00:00', 90000.00, 1, 'all'),
(79, 25, '20:00:00', 80000.00, 1, 'all'),
(80, 26, '06:00:00', 80000.00, 0, 'all'),
(81, 26, '17:00:00', 120000.00, 1, 'all'),
(82, 26, '20:00:00', 100000.00, 1, 'all'),
(83, 27, '06:00:00', 90000.00, 0, 'all'),
(84, 27, '17:00:00', 130000.00, 1, 'all'),
(85, 27, '20:00:00', 110000.00, 1, 'all'),
(86, 28, '08:00:00', 60000.00, 0, 'all'),
(87, 28, '12:00:00', 80000.00, 1, 'all'),
(88, 28, '17:00:00', 100000.00, 1, 'all'),
(89, 28, '20:00:00', 90000.00, 1, 'all'),
(90, 28, '22:00:00', 70000.00, 0, 'all'),
(91, 29, '08:00:00', 50000.00, 0, 'all'),
(92, 29, '12:00:00', 70000.00, 1, 'all'),
(93, 29, '17:00:00', 90000.00, 1, 'all'),
(94, 29, '20:00:00', 80000.00, 1, 'all'),
(95, 30, '06:00:00', 100000.00, 0, 'all'),
(96, 30, '08:00:00', 120000.00, 1, 'all'),
(97, 30, '17:00:00', 150000.00, 1, 'all'),
(98, 30, '20:00:00', 130000.00, 1, 'all'),
(99, 31, '06:00:00', 120000.00, 0, 'all'),
(100, 31, '08:00:00', 150000.00, 1, 'all'),
(101, 31, '17:00:00', 180000.00, 1, 'all'),
(102, 31, '20:00:00', 160000.00, 1, 'all'),
(103, 32, '06:00:00', 100000.00, 0, 'all'),
(104, 32, '08:00:00', 120000.00, 1, 'all'),
(105, 32, '17:00:00', 150000.00, 1, 'all'),
(106, 32, '20:00:00', 130000.00, 1, 'all'),
(107, 33, '06:00:00', 90000.00, 0, 'all'),
(108, 33, '08:00:00', 110000.00, 1, 'all'),
(109, 33, '17:00:00', 140000.00, 1, 'all'),
(110, 33, '20:00:00', 120000.00, 1, 'all');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `find_teammates`
--

DROP TABLE IF EXISTS `find_teammates`;
CREATE TABLE `find_teammates` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `sport_type` varchar(255) NOT NULL,
  `play_date` date NOT NULL,
  `time_slot` time NOT NULL,
  `players_needed` int(11) NOT NULL,
  `description` text DEFAULT NULL,
  `status` enum('open','closed') DEFAULT 'open',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `find_teammates`
--

INSERT INTO `find_teammates` (`id`, `user_id`, `sport_type`, `play_date`, `time_slot`, `players_needed`, `description`, `status`, `created_at`) VALUES
(1, 1, 'Bóng đá', '2025-10-25', '19:00:00', 3, 'Tìm 3 bạn đá sân 7 giao hữu, trình độ trung bình.', 'open', '2025-10-20 10:00:08'),
(6, 4, '1', '2025-10-31', '06:30:00', 3, '4', 'open', '2025-10-26 07:01:28'),
(7, 2, 'cau long', '2025-10-31', '05:29:00', 3, '300k/ng', 'open', '2025-10-29 13:56:33'),
(8, 5, 'tennisssss', '2025-11-14', '15:46:00', 7, 'ai cx dc', 'open', '2025-11-07 17:45:56'),
(9, 4, 'test tin nhan', '2025-11-14', '15:23:00', 1, 'bsb', 'open', '2025-11-07 18:19:46');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `find_teammates_participants`
--

DROP TABLE IF EXISTS `find_teammates_participants`;
CREATE TABLE `find_teammates_participants` (
  `id` int(11) NOT NULL,
  `post_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `joined_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `find_teammates_participants`
--

INSERT INTO `find_teammates_participants` (`id`, `post_id`, `user_id`, `joined_at`) VALUES
(1, 1, 3, '2025-10-21 14:00:13'),
(3, 1, 2, '2025-10-25 09:14:06'),
(4, 1, 4, '2025-10-25 13:24:44'),
(7, 6, 2, '2025-10-26 07:01:51'),
(9, 6, 5, '2025-11-04 17:47:52'),
(10, 7, 5, '2025-11-07 04:14:24'),
(11, 1, 5, '2025-11-07 15:43:10'),
(12, 1, 7, '2025-11-07 17:00:58'),
(13, 8, 2, '2025-11-07 17:46:10'),
(14, 9, 8, '2025-11-07 18:19:51');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `notifications`
--

DROP TABLE IF EXISTS `notifications`;
CREATE TABLE `notifications` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `type` enum('booking','teammate_join','teammate_request','system','promotion','chat') NOT NULL DEFAULT 'system',
  `title` varchar(255) NOT NULL,
  `message` text NOT NULL,
  `data` text DEFAULT NULL COMMENT 'JSON data: {post_id, booking_id, chat_room_id, etc}',
  `is_read` tinyint(1) NOT NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `notifications`
--

INSERT INTO `notifications` (`id`, `user_id`, `type`, `title`, `message`, `data`, `is_read`, `created_at`) VALUES
(1, 1, 'teammate_join', 'Có người mới tham gia! ????', 'hinh asdiuiw vừa tham gia vào tin tìm người chơi của bạn.', '{\"post_id\":1,\"joiner_id\":5,\"joiner_name\":\"hinh asdiuiw\"}', 0, '2025-11-07 15:43:10'),
(2, 1, 'chat', 'Tin nhắn mới từ hinh asdiuiw', 'san ban con can ng khong', '{\"room_id\":1}', 0, '2025-11-07 15:43:27'),
(3, 2, 'chat', 'Tin nhắn mới từ hinh asdiuiw', 'san ban con ng khong', '{\"room_id\":2}', 1, '2025-11-07 15:43:54'),
(4, 5, 'chat', 'Tin nhắn mới từ nguyen van a', 'con ban oi', '{\"room_id\":2}', 1, '2025-11-07 16:24:05'),
(5, 2, 'chat', 'Tin nhắn mới từ hinh asdiuiw', 'ok luon b oi', '{\"room_id\":2}', 1, '2025-11-07 16:27:08'),
(6, 2, 'booking', 'Đơn đặt sân đã được xác nhận!', 'Đơn đặt #22 của bạn đã được xác nhận. Hãy đến sân đúng giờ nhé!', '{\"booking_id\":\"22\"}', 1, '2025-11-07 16:46:52'),
(7, 2, 'booking', 'Đơn đặt sân hoàn thành', 'Cảm ơn bạn đã sử dụng dịch vụ! Hãy đánh giá sân để giúp chúng tôi cải thiện.', '{\"booking_id\":\"22\"}', 1, '2025-11-07 16:46:55'),
(8, 5, 'booking', 'Đơn đặt sân đã được xác nhận!', 'Đơn đặt #21 của bạn đã được xác nhận. Hãy đến sân đúng giờ nhé!', '{\"booking_id\":\"21\"}', 1, '2025-11-07 16:46:57'),
(9, 5, 'booking', 'Đơn đặt sân hoàn thành', 'Cảm ơn bạn đã sử dụng dịch vụ! Hãy đánh giá sân để giúp chúng tôi cải thiện.', '{\"booking_id\":\"21\"}', 1, '2025-11-07 16:46:59'),
(10, 5, 'booking', 'Đơn đặt sân đã được xác nhận!', 'Đơn đặt #20 của bạn đã được xác nhận. Hãy đến sân đúng giờ nhé!', '{\"booking_id\":\"20\"}', 1, '2025-11-07 16:47:02'),
(11, 5, 'booking', 'Đơn đặt sân hoàn thành', 'Cảm ơn bạn đã sử dụng dịch vụ! Hãy đánh giá sân để giúp chúng tôi cải thiện.', '{\"booking_id\":\"20\"}', 1, '2025-11-07 16:47:04'),
(12, 2, 'booking', 'Đơn đặt sân đã được xác nhận!', 'Đơn đặt #19 của bạn đã được xác nhận. Hãy đến sân đúng giờ nhé!', '{\"booking_id\":\"19\"}', 1, '2025-11-07 16:47:06'),
(13, 2, 'booking', 'Đơn đặt sân hoàn thành', 'Cảm ơn bạn đã sử dụng dịch vụ! Hãy đánh giá sân để giúp chúng tôi cải thiện.', '{\"booking_id\":\"19\"}', 1, '2025-11-07 16:47:09'),
(14, 2, 'booking', 'Đơn đặt sân đã bị hủy', 'Đơn đặt #18 của bạn đã bị hủy. Vui lòng liên hệ để biết thêm chi tiết.', '{\"booking_id\":\"18\"}', 1, '2025-11-07 16:47:12'),
(15, 2, 'booking', 'Đơn đặt sân đã được xác nhận!', 'Đơn đặt #17 của bạn đã được xác nhận. Hãy đến sân đúng giờ nhé!', '{\"booking_id\":\"17\"}', 1, '2025-11-07 16:47:15'),
(16, 2, 'booking', 'Đơn đặt sân hoàn thành', 'Cảm ơn bạn đã sử dụng dịch vụ! Hãy đánh giá sân để giúp chúng tôi cải thiện.', '{\"booking_id\":\"17\"}', 1, '2025-11-07 16:47:18'),
(17, 1, 'teammate_join', 'Có người mới tham gia! ????', 'Quang minh dinh vừa tham gia vào tin tìm người chơi của bạn.', '{\"post_id\":1,\"joiner_id\":7,\"joiner_name\":\"Quang minh dinh\"}', 0, '2025-11-07 17:00:58'),
(18, 1, 'chat', 'Tin nhắn mới từ Quang minh dinh', 'anh bạn cho tôi chơi với', '{\"room_id\":3}', 0, '2025-11-07 17:01:11'),
(19, 5, 'chat', 'Tin nhắn mới từ nguyen van a', '01iwns téttyty', '{\"room_id\":2}', 1, '2025-11-07 17:03:57'),
(20, 2, 'chat', 'Tin nhắn mới từ hinh asdiuiw', 'alo an', '{\"room_id\":2}', 0, '2025-11-07 17:41:22'),
(21, 2, 'chat', 'Tin nhắn mới từ hinh asdiuiw', 'étttt', '{\"room_id\":2}', 0, '2025-11-07 17:41:40'),
(22, 2, 'chat', 'Tin nhắn mới từ hinh asdiuiw', 'okhuytgfrded', '{\"room_id\":2}', 0, '2025-11-07 17:42:09'),
(23, 5, 'chat', 'Tin nhắn mới từ nguyen van a', 'jbvjbvh', '{\"room_id\":2}', 0, '2025-11-07 17:42:15'),
(24, 2, 'chat', 'Tin nhắn mới từ hinh asdiuiw', 'uygbuyguyg', '{\"room_id\":2}', 0, '2025-11-07 17:42:25'),
(25, 5, 'teammate_join', 'Có người mới tham gia! ????', 'nguyen van a vừa tham gia vào tin tìm người chơi của bạn.', '{\"post_id\":8,\"joiner_id\":2,\"joiner_name\":\"nguyen van a\"}', 1, '2025-11-07 17:46:10'),
(26, 4, 'teammate_join', 'Có người mới tham gia! ????', 'Danh Vô vừa tham gia vào tin tìm người chơi của bạn.', '{\"post_id\":9,\"joiner_id\":8,\"joiner_name\":\"Danh V\\u00f4\"}', 1, '2025-11-07 18:19:51'),
(27, 4, 'chat', 'Tin nhắn mới từ Danh Vô', 'heloo a zai', '{\"room_id\":4}', 1, '2025-11-07 18:20:06'),
(28, 8, 'chat', 'Tin nhắn mới từ nguyen quna minh', 'ksbdhsh', '{\"room_id\":4}', 0, '2025-11-07 18:20:25'),
(29, 4, 'chat', 'Tin nhắn mới từ Danh Vô', 'dgnehmry', '{\"room_id\":4}', 0, '2025-11-07 18:20:31'),
(30, 4, 'chat', 'Tin nhắn mới từ Danh Vô', '????????????', '{\"room_id\":4}', 0, '2025-11-07 18:20:41'),
(31, 8, 'chat', 'Tin nhắn mới từ nguyen quna minh', '????', '{\"room_id\":4}', 0, '2025-11-07 18:20:51');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `reviews`
--

DROP TABLE IF EXISTS `reviews`;
CREATE TABLE `reviews` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `field_id` int(11) DEFAULT NULL,
  `booking_id` int(11) DEFAULT NULL,
  `rating` int(11) DEFAULT NULL CHECK (`rating` >= 1 and `rating` <= 5),
  `comment` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `reviews`
--

INSERT INTO `reviews` (`id`, `user_id`, `field_id`, `booking_id`, `rating`, `comment`, `created_at`) VALUES
(1, 1, 1, 1, 5, 'Sân rất đẹp, cỏ êm, phục vụ tốt!', '2025-10-21 07:04:59'),
(2, 4, 1, 11, 5, 'san dep', '2025-10-29 13:43:27'),
(3, 4, 1, 12, 4, 'ngon', '2025-10-30 06:19:06');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `sport_fields`
--

DROP TABLE IF EXISTS `sport_fields`;
CREATE TABLE `sport_fields` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `sport_type` enum('football','volleyball','basketball','table_tennis','tennis','badminton','billiards','golf','pickleball') NOT NULL,
  `address` text DEFAULT NULL,
  `description` text DEFAULT NULL,
  `images` text DEFAULT NULL,
  `amenities` text DEFAULT NULL,
  `status` enum('active','maintenance','inactive') DEFAULT 'active'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `sport_fields`
--

INSERT INTO `sport_fields` (`id`, `name`, `sport_type`, `address`, `description`, `images`, `amenities`, `status`) VALUES
(1, 'Sân bóng đá cỏ nhân tạo ABC vui từng giờ', 'football', '123 Đường Cầu Giấy, Hà Nội', 'Sân 7 người, cỏ mới, có đèn chiếu sáng ban đêm.', '[\"https://images.unsplash.com/photo-1589487391730-58f20eb2c308?w=800\", \"https://images.unsplash.com/photo-1560272564-c83b66b1ad12?w=800\"]', '{\"parking\": true, \"shower\": true, \"drinks\": true}', 'active'),
(2, 'Sân cầu lông XYZ', 'badminton', '456 Đường Láng, Hà Nội', 'Nhà thi đấu tiêu chuẩn, 4 sân, thảm mới.', '[\"https://images.unsplash.com/photo-1626224583764-f87db24ac4ea\", \"https://images.unsplash.com/photo-1626224583764-f87db24ac4ea\"]', '{\"parking\": true, \"shower\": false, \"drinks\": true}', 'active'),
(3, 'Sân Tennis Quận Đống Đa', 'tennis', '789 Phố Chùa Bộc, Đống Đa, Hà Nội', 'Sân đất nện tiêu chuẩn, không gian thoáng đãng.', '[\"https://images.unsplash.com/photo-1554068865-24cecd4e34b8\", \"https://images.unsplash.com/photo-1622279457486-62dcc4a431d6\"]', '{\"parking\": false, \"shower\": true, \"drinks\": false}', 'active'),
(4, 'Sân bóng Mỹ Đình Sports Center', 'football', 'Đường Lê Quang Đạo, Mỹ Đình, Nam Từ Liêm, Hà Nội', 'Sân cỏ nhân tạo 5 người và 7 người, hệ thống đèn chiếu sáng hiện đại, phù hợp tổ chức giải đấu.', '[\"https://images.unsplash.com/photo-1574629810360-7efbbe195018?w=800\", \"https://images.unsplash.com/photo-1459865264687-595d652de67e?w=800\"]', '{\"parking\": true, \"shower\": true, \"drinks\": true, \"locker\": true, \"wifi\": true}', 'active'),
(5, 'Sân bóng Thành Công Complex', 'football', '57 Láng Hạ, Thành Công, Ba Đình, Hà Nội', 'Sân bóng mini 5-7 người trên tầng thượng, view đẹp, không khí thoáng mát, có phòng thay đồ riêng biệt.', '[\"https://images.unsplash.com/photo-1529900748604-07564a03e7a6?w=800\"]', '{\"parking\": true, \"shower\": true, \"drinks\": true, \"locker\": true, \"wifi\": false}', 'active'),
(6, 'Sân bóng Long Biên Football Arena', 'football', 'Nguyễn Văn Linh, Sài Đồng, Long Biên, Hà Nội', 'Cụm 3 sân bóng đá 5-7-11 người, cỏ nhân tạo Hàn Quốc chất lượng cao, có căng tin phục vụ đồ uống.', '[\"https://images.unsplash.com/photo-1589487391730-58f20eb2c308?w=800\", \"https://images.unsplash.com/photo-1560272564-c83b66b1ad12?w=800\"]', '{\"parking\": true, \"shower\": true, \"drinks\": true, \"locker\": true, \"wifi\": true}', 'active'),
(7, 'Sân bóng Royal City', 'football', '72A Nguyễn Trãi, Thượng Đình, Thanh Xuân, Hà Nội', 'Sân bóng trong khu đô thị Royal City, cỏ nhân tạo mới 100%, hệ thống tưới tự động.', '[\"https://images.unsplash.com/photo-1589803899418-c8b6a297b05b?w=800\"]', '{\"parking\": true, \"shower\": true, \"drinks\": true, \"locker\": true, \"wifi\": true}', 'active'),
(8, 'Sân bóng Times City Park', 'football', '458 Minh Khai, Vĩnh Tuy, Hai Bà Trưng, Hà Nội', 'Sân bóng sân thượng view đẹp, cỏ nhân tạo cao cấp, phục vụ cả ngày và đêm.', '[\"https://images.unsplash.com/photo-1552667466-07770ae110d0?w=800\"]', '{\"parking\": true, \"shower\": true, \"drinks\": true, \"locker\": true, \"wifi\": true}', 'active'),
(9, 'Sân bóng Keangnam Landmark', 'football', 'Phạm Hùng, Mễ Trì, Nam Từ Liêm, Hà Nội', 'Sân bóng cao cấp trong tổ hợp Keangnam, cơ sở vật chất 5 sao, phục vụ giải đấu chuyên nghiệp.', '[\"https://images.unsplash.com/photo-1577223625816-7546f52a2882?w=800\"]', '{\"parking\": true, \"shower\": true, \"drinks\": true, \"locker\": true, \"wifi\": true}', 'active'),
(10, 'Sân bóng chuyền Thanh Xuân Sport', 'volleyball', 'Khuất Duy Tiến, Thanh Xuân, Hà Nội', 'Sân bóng chuyền trong nhà, sàn gỗ cao su chuyên dụng, lưới chuẩn thi đấu quốc tế, phục vụ CLB và giải đấu.', '[\"https://images.unsplash.com/photo-1612872087720-bb876e2e67d1?w=800\", \"https://images.unsplash.com/photo-1612534847738-b4b53e471f39?w=800\"]', '{\"parking\": true, \"shower\": true, \"drinks\": true, \"locker\": true, \"wifi\": true}', 'active'),
(11, 'Bóng chuyền Đống Đa Center', 'volleyball', 'Láng Hạ, Đống Đa, Hà Nội', '3 sân bóng chuyền ngoài trời, mặt sân cát trắng, phù hợp bóng chuyền bãi biển.', '[\"https://images.unsplash.com/photo-1587280501635-68a0e82cd5ff?w=800\"]', '{\"parking\": false, \"shower\": true, \"drinks\": true, \"locker\": false, \"wifi\": false}', 'active'),
(12, 'Volleyball Arena Mỹ Đình', 'volleyball', 'Lê Quang Đạo, Mỹ Đình, Nam Từ Liêm, Hà Nội', 'Nhà thi đấu bóng chuyền chuyên nghiệp, khán đài 300 chỗ, sàn cao su, ánh sáng chuẩn.', '[\"https://images.unsplash.com/photo-1587280501635-68a0e82cd5ff?w=800\"]', '{\"parking\": true, \"shower\": true, \"drinks\": true, \"locker\": true, \"wifi\": true}', 'active'),
(13, 'Sân bóng rổ Nguyễn Du Sport', 'basketball', 'Nguyễn Du, Hai Bà Trưng, Hà Nội', 'Sân bóng rổ ngoài trời 2 rổ full court, mặt sân bê tông nhựa cao cấp, có ánh sáng đêm.', '[\"https://images.unsplash.com/photo-1546519638-68e109498ffc?w=800\", \"https://images.unsplash.com/photo-1519861531473-9200262188bf?w=800\"]', '{\"parking\": true, \"shower\": false, \"drinks\": true, \"locker\": false, \"wifi\": false}', 'active'),
(14, 'Basketball Arena Mỹ Đình', 'basketball', 'Lê Quang Đạo, Mỹ Đình, Nam Từ Liêm, Hà Nội', 'Nhà thi đấu bóng rổ trong nhà chuẩn quốc tế, sàn gỗ chuyên dụng, khán đài 500 chỗ, tổ chức giải VBA.', '[\"https://images.unsplash.com/photo-1515523110800-9415d13b84a8?w=800\"]', '{\"parking\": true, \"shower\": true, \"drinks\": true, \"locker\": true, \"wifi\": true}', 'active'),
(15, 'Sân bóng rổ Times City', 'basketball', 'Minh Khai, Hai Bà Trưng, Hà Nội', 'Sân bóng rổ tầng thượng, view thoáng đãng, phục vụ giải đấu phong trào và giao lưu.', '[\"https://images.unsplash.com/photo-1608245449230-4ac19066d2d0?w=800\"]', '{\"parking\": true, \"shower\": true, \"drinks\": true, \"locker\": true, \"wifi\": true}', 'active'),
(16, 'Basketball Hoàng Mai Complex', 'basketball', 'Giáp Bát, Hoàng Mai, Hà Nội', 'Sân bóng rổ trong nhà, sàn PU cao cấp, 2 rổ chuẩn NBA, giá sinh viên.', '[\"https://images.unsplash.com/photo-1519861531473-9200262188bf?w=800\"]', '{\"parking\": true, \"shower\": false, \"drinks\": true, \"locker\": false, \"wifi\": false}', 'active'),
(17, 'Bóng bàn Hà Đông Center', 'table_tennis', 'Phố Quang Trung, Hà Đông, Hà Nội', 'Nhà thi đấu bóng bàn với 10 bàn chuẩn quốc tế Double Fish, điều hòa mát mẻ, ánh sáng tốt.', '[\"https://images.unsplash.com/photo-1534158914592-062992fbe900?w=800\"]', '{\"parking\": true, \"shower\": false, \"drinks\": true, \"locker\": false, \"wifi\": true}', 'active'),
(18, 'Table Tennis Hoàng Mai', 'table_tennis', 'Giáp Bát, Hoàng Mai, Hà Nội', '8 bàn bóng bàn chất lượng cao, sàn cao su, tổ chức giải thường xuyên, có HLV.', '[\"https://images.unsplash.com/photo-1611647832580-377268dba7cb?w=800\"]', '{\"parking\": true, \"shower\": true, \"drinks\": true, \"locker\": true, \"wifi\": false}', 'active'),
(19, 'Bóng bàn Thăng Long', 'table_tennis', 'Trần Duy Hưng, Cầu Giấy, Hà Nội', '12 bàn bóng bàn trong khu thể thao tổng hợp, phục vụ từ 6h-23h, có cho thuê vợt.', '[\"https://images.unsplash.com/photo-1534158914592-062992fbe900?w=800\"]', '{\"parking\": true, \"shower\": true, \"drinks\": true, \"locker\": true, \"wifi\": true}', 'active'),
(20, 'Tennis Ciputra Hanoi', 'tennis', 'Khu đô thị Ciputra, Tây Hồ, Hà Nội', '6 sân tennis đất nện cao cấp, phù hợp thi đấu chuyên nghiệp, có HLV quốc tế, tổ chức giải ITF.', '[\"https://images.unsplash.com/photo-1622163642998-1ea32b0bbc67?w=800\", \"https://images.unsplash.com/photo-1595435934249-5df7ed86e1c0?w=800\"]', '{\"parking\": true, \"shower\": true, \"drinks\": true, \"locker\": true, \"wifi\": true}', 'active'),
(21, 'Tennis Royal City Premium', 'tennis', 'Royal City, Thanh Xuân, Hà Nội', '5 sân tennis cao cấp trong khu đô thị, mặt sân acrylic, có ánh sáng đêm, phục vụ 24/7.', '[\"https://images.unsplash.com/photo-1595435934249-5df7ed86e1c0?w=800\"]', '{\"parking\": true, \"shower\": true, \"drinks\": true, \"locker\": true, \"wifi\": true}', 'active'),
(22, 'Tennis Vinhomes Riverside', 'tennis', 'Vinhomes Riverside, Long Biên, Hà Nội', '8 sân tennis cao cấp trong khu đô thị xanh, cơ sở vật chất 5 sao, có spa và massage.', '[\"https://images.unsplash.com/photo-1554068865-24cecd4e34b8?w=800\"]', '{\"parking\": true, \"shower\": true, \"drinks\": true, \"locker\": true, \"wifi\": true}', 'active'),
(23, 'Tennis Mỹ Đình Sport Complex', 'tennis', 'Đường Lê Quang Đạo, Mỹ Đình, Nam Từ Liêm, Hà Nội', '4 sân tennis cứng ngoài trời, ánh sáng tốt, không gian rộng rãi, giá hợp lý.', '[\"https://images.unsplash.com/photo-1622163642998-1ea32b0bbc67?w=800\"]', '{\"parking\": true, \"shower\": true, \"drinks\": true, \"locker\": true, \"wifi\": false}', 'active'),
(24, 'Cầu lông VinSport Cầu Giấy', 'badminton', 'Trần Thái Tông, Dịch Vọng, Cầu Giấy, Hà Nội', 'Nhà thi đấu 8 sân cầu lông chuẩn quốc tế BWF, sàn gỗ Đức, điều hòa mát mẻ, phục vụ thi đấu chuyên nghiệp.', '[\"https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=800\", \"https://images.unsplash.com/photo-1554068865-24cecd4e34b8?w=800\"]', '{\"parking\": true, \"shower\": true, \"drinks\": true, \"locker\": true, \"wifi\": true}', 'active'),
(25, 'Cầu lông Thanh Xuân Sport', 'badminton', 'Nguyễn Trãi, Thanh Xuân, Hà Nội', '10 sân cầu lông rộng rãi, thoáng mát, thảm Yonex chính hãng, có phòng tập gym kèm theo.', '[\"https://images.unsplash.com/photo-1563299796-17596ed6b017?w=800\"]', '{\"parking\": true, \"shower\": true, \"drinks\": true, \"locker\": true, \"wifi\": true}', 'active'),
(26, 'Cầu lông Thái Hà Sport Center', 'badminton', 'Thái Hà, Đống Đa, Hà Nội', 'Nhà thi đấu cao cấp với 12 sân cầu lông, phục vụ từ sáng sớm đến tối muộn, có huấn luyện viên chuyên nghiệp.', '[\"https://images.unsplash.com/photo-1612872087720-bb876e2e67d1?w=800\"]', '{\"parking\": true, \"shower\": true, \"drinks\": true, \"locker\": true, \"wifi\": true}', 'active'),
(27, 'Cầu lông Mỹ Đình National', 'badminton', 'Mỹ Đình 2, Nam Từ Liêm, Hà Nội', 'Trung tâm cầu lông quy mô lớn với 15 sân chuẩn thi đấu quốc gia, tổ chức giải thường xuyên.', '[\"https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=800\"]', '{\"parking\": true, \"shower\": true, \"drinks\": true, \"locker\": true, \"wifi\": true}', 'active'),
(28, 'Club Bi-a Royal City', 'billiards', 'Royal City, Thanh Xuân, Hà Nội', 'Câu lạc bộ bi-a cao cấp với 20 bàn Bida Pool và Carom, bàn nhập khẩu từ Đức, không gian sang trọng.', '[\"https://images.unsplash.com/photo-1604719312566-8912e9227c6a?w=800\", \"https://images.unsplash.com/photo-1561122639-0cf96cb684e8?w=800\"]', '{\"parking\": true, \"shower\": false, \"drinks\": true, \"locker\": true, \"wifi\": true}', 'active'),
(29, 'Bi-a Times City Center', 'football', 'Minh Khai, Hai Bà Trưng, Hà Nội', '15 bàn bi-a các loại: Pool, Snooker, Carom. Bàn Brunswick chính hãng, phục vụ 24/7.', '[\"field_690e3328262239.60374688.png\"]', '{\"parking\": true, \"shower\": false, \"drinks\": true, \"locker\": false, \"wifi\": true}', 'active'),
(30, 'Golf Driving Range Long Biên', 'golf', 'Ngọc Thụy, Long Biên, Hà Nội', 'Sân tập golf 2 tầng với 40 booth, có HLV PGA, phục vụ 24/7, máy phân tích swing Trackman.', '[\"https://images.unsplash.com/photo-1535131749006-b7f58c99034b?w=800\", \"https://images.unsplash.com/photo-1587174486073-ae5e5cff23aa?w=800\"]', '{\"parking\": true, \"shower\": true, \"drinks\": true, \"locker\": true, \"wifi\": true}', 'active'),
(31, 'Golf Center Mỹ Đình', 'golf', 'Lê Quang Đạo, Mỹ Đình, Nam Từ Liêm, Hà Nội', 'Sân tập golf 3 tầng, 60 booth, putting green, bunker practice, pro shop bán đồ golf.', '[\"https://images.unsplash.com/photo-1587174486073-ae5e5cff23aa?w=800\"]', '{\"parking\": true, \"shower\": true, \"drinks\": true, \"locker\": true, \"wifi\": true}', 'active'),
(32, 'Pickleball Club Ciputra', 'pickleball', 'Khu đô thị Ciputra, Tây Hồ, Hà Nội', 'Sân pickleball đầu tiên tại Hà Nội, 4 sân chuẩn quốc tế, mặt sân acrylic, có HLV người nước ngoài.', '[\"https://images.unsplash.com/photo-1554068865-24cecd4e34b8?w=800\", \"https://images.unsplash.com/photo-1622163642998-1ea32b0bbc67?w=800\"]', '{\"parking\": true, \"shower\": true, \"drinks\": true, \"locker\": true, \"wifi\": true}', 'active'),
(33, 'Pickleball Arena Vinhomes', 'pickleball', 'Vinhomes Smart City, Nam Từ Liêm, Hà Nội', '6 sân pickleball trong nhà, điều hòa mát mẻ, ánh sáng tốt, cho thuê vợt và bóng.', '[\"https://images.unsplash.com/photo-1554068865-24cecd4e34b8?w=800\"]', '{\"parking\": true, \"shower\": true, \"drinks\": true, \"locker\": true, \"wifi\": true}', 'active');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `role` enum('user','admin') DEFAULT 'user',
  `fcm_token` text DEFAULT NULL,
  `loyalty_points` int(11) NOT NULL DEFAULT 0,
  `membership_tier` enum('bronze','silver','gold') NOT NULL DEFAULT 'bronze',
  `reset_otp` varchar(10) DEFAULT NULL,
  `otp_expiry` datetime DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `users`
--

INSERT INTO `users` (`id`, `full_name`, `email`, `password`, `phone`, `role`, `fcm_token`, `loyalty_points`, `membership_tier`, `reset_otp`, `otp_expiry`, `created_at`) VALUES
(1, 'Đinh Quang Minh', 'minhdqhe170267@fpt.edu.vn', '$2y$10$O0UpB34z/24LYBtw9lPUfOvZLpmvUqr8HCn/GsVNDjSmZV18iuNAm', NULL, 'admin', NULL, 0, 'bronze', NULL, NULL, '2025-10-19 16:15:40'),
(2, 'nguyen van a', 'dinh48967@gmail.com', '$2y$10$fwxk3ZUwNodttbl/VD.R7Os44pFhZb0uh9lDehTsXWWvZt5hoCth.', '8845666256256', 'user', 'eWwss94uScWp8d_-rgil4u:APA91bEIau1cX_HJVCPy36kHnv9oQ0dGPvrG3l-4i-wi5G4Zxgg4PHc_Ojie9rS1LC9MXaIsb0GfUUdszpPMjvHHiMODMPGeCYlI5pOvhXGFSwWDc1Tv_Ds', 0, 'bronze', NULL, NULL, '2025-10-21 07:15:50'),
(3, 'rewqr', '1@GMAIL.COM', '$2y$10$DyJiWg0pq2XFD5StdBTTk.Q3mqLyZSTySRA2HAMaq3fqq9YbsNNWC', '5551234567', 'user', NULL, 0, 'bronze', NULL, NULL, '2025-10-21 13:53:02'),
(4, 'nguyen quna minh', 'dquangminh79@gmail.com', '$2y$10$n5TFJURQ.Ahf.zkxIa7zIuKk1tSEshfY31qBjJCYfLqNrqAekuCT6', NULL, 'user', 'fZ3seqfpRkCm71Do6RVJRU:APA91bHdZ9ur8aSQ5CU-fpiSMN3IkSbyUEWMfpIcRzIa3Bd2ivwHtXztNGymQ5ypkVH64d5WflaSBIhRvbT83NIw72zyweq2Ar_xjyQWRnXfLFu2YcLPjMY', 0, 'bronze', '184312', '2025-10-29 20:54:24', '2025-10-25 13:02:12'),
(5, 'hinh asdiuiw', 'asdiuiwhinh@gmail.com', '$2y$10$ncnO7JqGUyDETBEkhBPghuSkjTzg0ano3au7E.cbN3cvbIAAnTX5q', NULL, 'user', NULL, 0, 'bronze', NULL, NULL, '2025-10-26 15:42:56'),
(6, 'Ihidhsjh Mihhsgh', 'ihidhsjhmihhsgh@gmail.com', '$2y$10$izIJE/IrtaoYs6Y7FMEY7.NUd7liAeVvrervXcg39docPiprUouc.', NULL, 'user', NULL, 0, 'bronze', NULL, NULL, '2025-10-26 15:55:34'),
(7, 'Quang minh dinh', 'dinhminhdaica@yahoo.com', '$2y$10$85oKSeVurAlpBSZndMWkMOxdE/CA51VMogkY7ePzdMhF4nXxF238G', NULL, 'user', NULL, 0, 'bronze', NULL, NULL, '2025-11-07 17:00:22'),
(8, 'Danh Vô', 'vod03019@gmail.com', '$2y$10$0CTUIjVuiCPK05UfBjHuoujW3GXDM9BkHKDzaLCfs1h.Eoh9nK4AO', NULL, 'user', 'cT4Y5tBOQniCLfq3ZFBBZJ:APA91bG4WI3A6zX4Za1T62hvD9Su8OGv7UtwK5k0cLEpgzDGPVt2tjwufBPKHMbK2lP4rWP7WWAL-XbNBMT9zTyb7xUT_4vTW86PKPsbfMiywbjuRMc8O7k', 0, 'bronze', NULL, NULL, '2025-11-07 18:18:07');

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `bookings`
--
ALTER TABLE `bookings`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `field_id` (`field_id`);

--
-- Chỉ mục cho bảng `chat_messages`
--
ALTER TABLE `chat_messages`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_room_id` (`room_id`),
  ADD KEY `idx_sender_id` (`sender_id`),
  ADD KEY `idx_created_at` (`created_at`),
  ADD KEY `idx_is_read` (`is_read`);

--
-- Chỉ mục cho bảng `chat_rooms`
--
ALTER TABLE `chat_rooms`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_chat_users` (`user1_id`,`user2_id`,`post_id`),
  ADD KEY `idx_user1` (`user1_id`),
  ADD KEY `idx_user2` (`user2_id`),
  ADD KEY `idx_post` (`post_id`);

--
-- Chỉ mục cho bảng `field_prices`
--
ALTER TABLE `field_prices`
  ADD PRIMARY KEY (`id`),
  ADD KEY `field_id` (`field_id`);

--
-- Chỉ mục cho bảng `find_teammates`
--
ALTER TABLE `find_teammates`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Chỉ mục cho bảng `find_teammates_participants`
--
ALTER TABLE `find_teammates_participants`
  ADD PRIMARY KEY (`id`),
  ADD KEY `post_id` (`post_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Chỉ mục cho bảng `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_user_id` (`user_id`),
  ADD KEY `idx_is_read` (`is_read`),
  ADD KEY `idx_created_at` (`created_at`);

--
-- Chỉ mục cho bảng `reviews`
--
ALTER TABLE `reviews`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `field_id` (`field_id`),
  ADD KEY `booking_id` (`booking_id`);

--
-- Chỉ mục cho bảng `sport_fields`
--
ALTER TABLE `sport_fields`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `bookings`
--
ALTER TABLE `bookings`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT cho bảng `chat_messages`
--
ALTER TABLE `chat_messages`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT cho bảng `chat_rooms`
--
ALTER TABLE `chat_rooms`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT cho bảng `field_prices`
--
ALTER TABLE `field_prices`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=111;

--
-- AUTO_INCREMENT cho bảng `find_teammates`
--
ALTER TABLE `find_teammates`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT cho bảng `find_teammates_participants`
--
ALTER TABLE `find_teammates_participants`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT cho bảng `notifications`
--
ALTER TABLE `notifications`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- AUTO_INCREMENT cho bảng `reviews`
--
ALTER TABLE `reviews`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT cho bảng `sport_fields`
--
ALTER TABLE `sport_fields`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;

--
-- AUTO_INCREMENT cho bảng `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `bookings`
--
ALTER TABLE `bookings`
  ADD CONSTRAINT `bookings_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `bookings_ibfk_2` FOREIGN KEY (`field_id`) REFERENCES `sport_fields` (`id`);

--
-- Các ràng buộc cho bảng `chat_messages`
--
ALTER TABLE `chat_messages`
  ADD CONSTRAINT `chat_messages_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `chat_rooms` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `chat_messages_ibfk_2` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Các ràng buộc cho bảng `chat_rooms`
--
ALTER TABLE `chat_rooms`
  ADD CONSTRAINT `chat_rooms_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `find_teammates` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `chat_rooms_ibfk_2` FOREIGN KEY (`user1_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `chat_rooms_ibfk_3` FOREIGN KEY (`user2_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Các ràng buộc cho bảng `field_prices`
--
ALTER TABLE `field_prices`
  ADD CONSTRAINT `field_prices_ibfk_1` FOREIGN KEY (`field_id`) REFERENCES `sport_fields` (`id`) ON DELETE CASCADE;

--
-- Các ràng buộc cho bảng `find_teammates`
--
ALTER TABLE `find_teammates`
  ADD CONSTRAINT `find_teammates_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Các ràng buộc cho bảng `find_teammates_participants`
--
ALTER TABLE `find_teammates_participants`
  ADD CONSTRAINT `find_teammates_participants_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `find_teammates` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `find_teammates_participants_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Các ràng buộc cho bảng `notifications`
--
ALTER TABLE `notifications`
  ADD CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Các ràng buộc cho bảng `reviews`
--
ALTER TABLE `reviews`
  ADD CONSTRAINT `reviews_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `reviews_ibfk_2` FOREIGN KEY (`field_id`) REFERENCES `sport_fields` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `reviews_ibfk_3` FOREIGN KEY (`booking_id`) REFERENCES `bookings` (`id`) ON DELETE CASCADE;


--
-- Siêu dữ liệu
--
USE `phpmyadmin`;

--
-- Siêu dữ liệu cho bảng bookings
--

--
-- Siêu dữ liệu cho bảng chat_messages
--

--
-- Siêu dữ liệu cho bảng chat_rooms
--

--
-- Siêu dữ liệu cho bảng field_prices
--

--
-- Siêu dữ liệu cho bảng find_teammates
--

--
-- Siêu dữ liệu cho bảng find_teammates_participants
--

--
-- Siêu dữ liệu cho bảng notifications
--

--
-- Siêu dữ liệu cho bảng reviews
--

--
-- Siêu dữ liệu cho bảng sport_fields
--

--
-- Siêu dữ liệu cho bảng users
--

--
-- Đang đổ dữ liệu cho bảng `pma__table_uiprefs`
--

INSERT INTO `pma__table_uiprefs` (`username`, `db_name`, `table_name`, `prefs`, `last_update`) VALUES
('root', 'sport_booking_db', 'users', '{\"sorted_col\":\"`users`.`google_id` DESC\"}', '2025-11-04 05:29:03');

--
-- Siêu dữ liệu cho cơ sở dữ liệu sport_booking_db
--
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

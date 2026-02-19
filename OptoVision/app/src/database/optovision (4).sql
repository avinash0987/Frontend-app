-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Feb 10, 2026 at 01:53 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `optovision`
--

-- --------------------------------------------------------

--
-- Table structure for table `exercise_logs`
--

CREATE TABLE `exercise_logs` (
  `log_id` int(11) NOT NULL,
  `profile_id` int(11) NOT NULL,
  `exercise_id` int(11) NOT NULL,
  `performed_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `eye_exercises`
--

CREATE TABLE `eye_exercises` (
  `exercise_id` int(11) NOT NULL,
  `exercise_name` varchar(100) DEFAULT NULL,
  `duration_seconds` int(11) DEFAULT NULL,
  `description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `games`
--

CREATE TABLE `games` (
  `game_id` int(11) NOT NULL,
  `game_name` varchar(100) DEFAULT NULL,
  `description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `games`
--

INSERT INTO `games` (`game_id`, `game_name`, `description`) VALUES
(1, 'Focus Tracking', 'Track and tap moving targets'),
(2, 'Color Match', 'Match colors quickly'),
(3, 'Memory Vision', 'Remember visual patterns'),
(4, 'Eye Coordination', 'Coordinate eye movements');

-- --------------------------------------------------------

--
-- Table structure for table `game_logs`
--

CREATE TABLE `game_logs` (
  `log_id` int(11) NOT NULL,
  `profile_id` int(11) NOT NULL,
  `game_id` int(11) NOT NULL,
  `score` int(11) DEFAULT NULL,
  `played_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `game_logs`
--

INSERT INTO `game_logs` (`log_id`, `profile_id`, `game_id`, `score`, `played_at`) VALUES
(37, 1, 1, 48, '2026-02-09 08:56:57'),
(38, 1, 2, 70, '2026-02-09 08:57:34'),
(39, 1, 3, 50, '2026-02-09 08:58:10'),
(40, 1, 4, 47, '2026-02-09 08:58:44'),
(41, 1, 1, 54, '2026-02-10 12:20:51'),
(42, 1, 2, 34, '2026-02-10 12:21:24'),
(43, 1, 3, 82, '2026-02-10 12:21:55'),
(44, 1, 4, 59, '2026-02-10 12:22:30');

-- --------------------------------------------------------

--
-- Table structure for table `notifications`
--

CREATE TABLE `notifications` (
  `notification_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `title` varchar(100) DEFAULT NULL,
  `message` text DEFAULT NULL,
  `is_read` tinyint(1) DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `profiles`
--

CREATE TABLE `profiles` (
  `profile_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `relationship` enum('self','father','mother','child','other') DEFAULT NULL,
  `age` int(11) NOT NULL,
  `gender` enum('male','female','other') DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `date_of_birth` varchar(20) DEFAULT NULL,
  `street_address` varchar(255) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `country` varchar(100) DEFAULT NULL,
  `prior_eye_condition` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE current_timestamp(),
  `right_eye_power` varchar(10) DEFAULT '6/6',
  `left_eye_power` varchar(10) DEFAULT '6/6',
  `profile_image` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `profiles`
--

INSERT INTO `profiles` (`profile_id`, `user_id`, `name`, `relationship`, `age`, `gender`, `email`, `phone`, `date_of_birth`, `street_address`, `city`, `country`, `prior_eye_condition`, `created_at`, `updated_at`, `right_eye_power`, `left_eye_power`, `profile_image`) VALUES
(1, 1, 'Timmareddy Prem Kumar Reddy', 'self', 25, 'male', 'tprem6565@gmail.com', '8985545407', '09/03/2005', 'pedda jonnavaram', 'kadapa', 'India', 'Wears: Yes, Glasses, Last exam: 06/02/2026', '2026-02-06 10:59:24', '2026-02-10 05:45:21', '6/6', '6/6', 'profile_1_1770702318.jpg'),
(2, 2, 'sodam siva Siddarth Reddy', 'self', 25, 'male', 'siddarthreddy.5377@gmail.com', '9133333337', NULL, NULL, NULL, NULL, 'Wears: Yes, Glasses, Last exam: 07/02/2026', '2026-02-07 08:12:19', '2026-02-07 08:12:33', '6/6', '6/6', NULL),
(3, 2, 'Kesava', 'self', 24, 'male', 'kesava@gmail.com', '8374203019', NULL, NULL, NULL, NULL, 'Wears: No', '2026-02-07 17:36:06', '2026-02-07 17:36:34', '6/6', '6/6', NULL),
(4, 2, 'Charan', 'self', 19, 'male', NULL, NULL, NULL, NULL, NULL, NULL, 'Wears: No, Last exam: 05/02/2026', '2026-02-08 17:12:58', '2026-02-08 17:13:37', '6/6', '6/6', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `reports`
--

CREATE TABLE `reports` (
  `report_id` int(11) NOT NULL,
  `profile_id` int(11) NOT NULL,
  `overall_score` decimal(5,2) DEFAULT NULL,
  `summary` text DEFAULT NULL,
  `recommendations` text DEFAULT NULL,
  `generated_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `reports`
--

INSERT INTO `reports` (`report_id`, `profile_id`, `overall_score`, `summary`, `recommendations`, `generated_at`) VALUES
(18, 1, 37.00, 'Based on your test scores and game performance, your overall eye health score is 37%. Your risk level is classified as \'Very High\'. Immediate consultation with an eye specialist is strongly recommended.', '{\"risk_level\":\"Very High\",\"overall_score\":37,\"avg_right_test_score\":30,\"avg_left_test_score\":26.7,\"avg_test_score\":28.3,\"avg_game_score\":50,\"summary\":\"Based on your test scores and game performance, your overall eye health score is 37%. Your risk level is classified as \'Very High\'. Immediate consultation with an eye specialist is strongly recommended.\",\"right_eye_status\":\"Severe Concern\",\"left_eye_status\":\"Severe Concern\",\"suggested_action\":\"Immediate consultation with an eye specialist is strongly recommended.\",\"required_nutrients\":[\"Omega-3 Fatty Acids\",\"Lutein\",\"Zeaxanthin\",\"Vitamin A\",\"Selenium\",\"Zinc\",\"Vitamin E\"],\"test_scores\":[{\"test_id\":1,\"test_name\":\"Distance Vision Test\",\"right_score\":40,\"left_score\":20,\"average_score\":30},{\"test_id\":2,\"test_name\":\"Near Vision Test\",\"right_score\":20,\"left_score\":40,\"average_score\":30},{\"test_id\":3,\"test_name\":\"Color Vision Test\",\"right_score\":20,\"left_score\":20,\"average_score\":20},{\"test_id\":4,\"test_name\":\"Astigmatism Test\",\"right_score\":40,\"left_score\":0,\"average_score\":20},{\"test_id\":5,\"test_name\":\"Contrast Sensitivity Test\",\"right_score\":40,\"left_score\":40,\"average_score\":40},{\"test_id\":6,\"test_name\":\"Visual Field Test\",\"right_score\":20,\"left_score\":40,\"average_score\":30}],\"game_scores\":[],\"generated_at\":\"2026-02-09 09:42:54\"}', '2026-02-09 08:42:54'),
(19, 1, 39.50, 'Based on your test scores and game performance, your overall eye health score is 39.5%. Your risk level is classified as \'Very High\'. Immediate consultation with an eye specialist is strongly recommended.', '{\"risk_level\":\"Very High\",\"overall_score\":39.5,\"avg_right_test_score\":26.7,\"avg_left_test_score\":33.3,\"avg_test_score\":30,\"avg_game_score\":53.8,\"games_played\":true,\"tests_completed\":6,\"summary\":\"Based on your test scores and game performance, your overall eye health score is 39.5%. Your risk level is classified as \'Very High\'. Immediate consultation with an eye specialist is strongly recommended.\",\"right_eye_status\":\"Severe Concern\",\"left_eye_status\":\"Severe Concern\",\"suggested_action\":\"Immediate consultation with an eye specialist is strongly recommended.\",\"required_nutrients\":[\"Omega-3 Fatty Acids\",\"Lutein\",\"Zeaxanthin\",\"Vitamin A\",\"Selenium\",\"Zinc\",\"Vitamin E\"],\"test_scores\":[{\"test_id\":1,\"test_name\":\"Distance Vision Test\",\"right_score\":20,\"left_score\":0,\"average_score\":10},{\"test_id\":2,\"test_name\":\"Near Vision Test\",\"right_score\":40,\"left_score\":40,\"average_score\":40},{\"test_id\":3,\"test_name\":\"Color Vision Test\",\"right_score\":0,\"left_score\":60,\"average_score\":30},{\"test_id\":4,\"test_name\":\"Astigmatism Test\",\"right_score\":20,\"left_score\":20,\"average_score\":20},{\"test_id\":5,\"test_name\":\"Contrast Sensitivity Test\",\"right_score\":40,\"left_score\":40,\"average_score\":40},{\"test_id\":6,\"test_name\":\"Visual Field Test\",\"right_score\":40,\"left_score\":40,\"average_score\":40}],\"game_scores\":[{\"game_id\":1,\"game_name\":\"Focus Tracking\",\"score\":48},{\"game_id\":2,\"game_name\":\"Color Match\",\"score\":70},{\"game_id\":3,\"game_name\":\"Memory Vision\",\"score\":50},{\"game_id\":4,\"game_name\":\"Eye Coordination\",\"score\":47}],\"generated_at\":\"2026-02-09 09:58:49\"}', '2026-02-09 08:58:49'),
(20, 1, 51.20, 'Based on your test scores and game performance, your overall eye health score is 51.2%. Your risk level is classified as \'High\'. Consult an eye specialist soon. Schedule an appointment within the next few weeks.', '{\"risk_level\":\"High\",\"overall_score\":51.2,\"avg_right_test_score\":46.7,\"avg_left_test_score\":50,\"avg_test_score\":48.3,\"avg_game_score\":55.5,\"games_played\":true,\"tests_completed\":6,\"summary\":\"Based on your test scores and game performance, your overall eye health score is 51.2%. Your risk level is classified as \'High\'. Consult an eye specialist soon. Schedule an appointment within the next few weeks.\",\"right_eye_status\":\"Severe Concern\",\"left_eye_status\":\"Moderate Concern\",\"suggested_action\":\"Consult an eye specialist soon. Schedule an appointment within the next few weeks.\",\"required_nutrients\":[\"Omega-3 Fatty Acids\",\"Lutein\",\"Zeaxanthin\",\"Vitamin E\",\"Zinc\"],\"test_scores\":[{\"test_id\":1,\"test_name\":\"Distance Vision Test\",\"right_score\":80,\"left_score\":100,\"average_score\":90},{\"test_id\":2,\"test_name\":\"Near Vision Test\",\"right_score\":100,\"left_score\":80,\"average_score\":90},{\"test_id\":3,\"test_name\":\"Color Vision Test\",\"right_score\":40,\"left_score\":80,\"average_score\":60},{\"test_id\":4,\"test_name\":\"Astigmatism Test\",\"right_score\":40,\"left_score\":0,\"average_score\":20},{\"test_id\":5,\"test_name\":\"Contrast Sensitivity Test\",\"right_score\":0,\"left_score\":0,\"average_score\":0},{\"test_id\":6,\"test_name\":\"Visual Field Test\",\"right_score\":20,\"left_score\":40,\"average_score\":30}],\"game_scores\":[{\"game_id\":1,\"game_name\":\"Focus Tracking\",\"score\":51},{\"game_id\":2,\"game_name\":\"Color Match\",\"score\":52},{\"game_id\":3,\"game_name\":\"Memory Vision\",\"score\":66},{\"game_id\":4,\"game_name\":\"Eye Coordination\",\"score\":53}],\"generated_at\":\"2026-02-10 13:22:33\"}', '2026-02-10 12:22:33');

-- --------------------------------------------------------

--
-- Table structure for table `report_tests`
--

CREATE TABLE `report_tests` (
  `report_test_id` int(11) NOT NULL,
  `report_id` int(11) NOT NULL,
  `test_id` int(11) NOT NULL,
  `interpretation` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `test_results`
--

CREATE TABLE `test_results` (
  `result_id` int(11) NOT NULL,
  `session_id` int(11) NOT NULL,
  `eye` enum('left','right','both') DEFAULT NULL,
  `score` decimal(5,2) DEFAULT NULL,
  `status` enum('normal','attention','critical') DEFAULT NULL,
  `raw_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`raw_data`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `test_results`
--

INSERT INTO `test_results` (`result_id`, `session_id`, `eye`, `score`, `status`, `raw_data`) VALUES
(251, 126, 'right', 80.00, 'normal', NULL),
(252, 126, 'left', 100.00, 'normal', NULL),
(253, 127, 'right', 100.00, 'normal', NULL),
(254, 127, 'left', 80.00, 'normal', NULL),
(255, 128, 'right', 40.00, 'attention', NULL),
(256, 128, 'left', 80.00, 'attention', NULL),
(257, 129, 'right', 40.00, 'critical', NULL),
(258, 129, 'left', 0.00, 'critical', NULL),
(259, 130, 'right', 0.00, 'critical', NULL),
(260, 130, 'left', 0.00, 'critical', NULL),
(261, 131, 'right', 20.00, 'critical', NULL),
(262, 131, 'left', 40.00, 'critical', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `test_sessions`
--

CREATE TABLE `test_sessions` (
  `session_id` int(11) NOT NULL,
  `profile_id` int(11) NOT NULL,
  `test_id` int(11) NOT NULL,
  `session_date` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `test_sessions`
--

INSERT INTO `test_sessions` (`session_id`, `profile_id`, `test_id`, `session_date`) VALUES
(126, 1, 1, '2026-02-10 12:17:32'),
(127, 1, 2, '2026-02-10 12:18:00'),
(128, 1, 3, '2026-02-10 12:18:23'),
(129, 1, 4, '2026-02-10 12:18:50'),
(130, 1, 5, '2026-02-10 12:19:09'),
(131, 1, 6, '2026-02-10 12:19:30');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `full_name` varchar(100) DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `password_hash` varchar(255) NOT NULL,
  `is_verified` tinyint(1) DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`user_id`, `full_name`, `email`, `phone`, `password_hash`, `is_verified`, `created_at`, `updated_at`) VALUES
(1, 'Prem Kumar Reddy', 'tprem6565@gmail.com', NULL, '$2y$10$NP8mhgwPRWDzGBinXt.A..sK4bGME87l3YHYDdG1VeDKesk.RylCq', 0, '2026-02-06 10:23:27', '2026-02-08 15:41:10'),
(2, 'Siva Siddartha', 'siddarthreddy.5377@gmail.com', NULL, '$2y$10$tPlZEkzQSNwbNSFJOeQnTORzZkQLQLSbYgbp8Ep1qo6zAUDMP2SsK', 0, '2026-02-07 08:11:30', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `verification_codes`
--

CREATE TABLE `verification_codes` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `code` varchar(10) NOT NULL,
  `type` varchar(20) DEFAULT 'verification',
  `expiry` datetime NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `verification_codes`
--

INSERT INTO `verification_codes` (`id`, `user_id`, `code`, `type`, `expiry`, `created_at`) VALUES
(2, 2, '337157', 'verification', '2026-02-07 09:21:30', '2026-02-07 08:11:30');

-- --------------------------------------------------------

--
-- Table structure for table `vision_tests`
--

CREATE TABLE `vision_tests` (
  `test_id` int(11) NOT NULL,
  `test_name` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `category` enum('distance','near','color','astigmatism','contrast','visual_field','alignment') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `vision_tests`
--

INSERT INTO `vision_tests` (`test_id`, `test_name`, `description`, `category`) VALUES
(1, 'Distance Vision Test', 'Measures your ability to see objects clearly at a distance using a Snellen chart.', ''),
(2, 'Near Vision Test', 'Evaluates your ability to see objects clearly at close range, important for reading.', ''),
(3, 'Color Vision Test', 'Screens for color blindness using Ishihara plates with hidden numbers.', 'color'),
(4, 'Astigmatism Test', 'Detects astigmatism by checking if lines appear equally sharp in all directions.', ''),
(5, 'Contrast Sensitivity Test', 'Measures your ability to distinguish between light and dark shades.', ''),
(6, 'Visual Field Test', 'Tests your peripheral vision to detect blind spots or vision loss.', '');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `exercise_logs`
--
ALTER TABLE `exercise_logs`
  ADD PRIMARY KEY (`log_id`),
  ADD KEY `profile_id` (`profile_id`),
  ADD KEY `exercise_id` (`exercise_id`);

--
-- Indexes for table `eye_exercises`
--
ALTER TABLE `eye_exercises`
  ADD PRIMARY KEY (`exercise_id`);

--
-- Indexes for table `games`
--
ALTER TABLE `games`
  ADD PRIMARY KEY (`game_id`);

--
-- Indexes for table `game_logs`
--
ALTER TABLE `game_logs`
  ADD PRIMARY KEY (`log_id`),
  ADD KEY `profile_id` (`profile_id`),
  ADD KEY `game_id` (`game_id`);

--
-- Indexes for table `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`notification_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `profiles`
--
ALTER TABLE `profiles`
  ADD PRIMARY KEY (`profile_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `reports`
--
ALTER TABLE `reports`
  ADD PRIMARY KEY (`report_id`),
  ADD KEY `profile_id` (`profile_id`);

--
-- Indexes for table `report_tests`
--
ALTER TABLE `report_tests`
  ADD PRIMARY KEY (`report_test_id`),
  ADD KEY `report_id` (`report_id`),
  ADD KEY `test_id` (`test_id`);

--
-- Indexes for table `test_results`
--
ALTER TABLE `test_results`
  ADD PRIMARY KEY (`result_id`),
  ADD KEY `session_id` (`session_id`);

--
-- Indexes for table `test_sessions`
--
ALTER TABLE `test_sessions`
  ADD PRIMARY KEY (`session_id`),
  ADD KEY `profile_id` (`profile_id`),
  ADD KEY `test_id` (`test_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `verification_codes`
--
ALTER TABLE `verification_codes`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `vision_tests`
--
ALTER TABLE `vision_tests`
  ADD PRIMARY KEY (`test_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `exercise_logs`
--
ALTER TABLE `exercise_logs`
  MODIFY `log_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `eye_exercises`
--
ALTER TABLE `eye_exercises`
  MODIFY `exercise_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `games`
--
ALTER TABLE `games`
  MODIFY `game_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `game_logs`
--
ALTER TABLE `game_logs`
  MODIFY `log_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=45;

--
-- AUTO_INCREMENT for table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `notification_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `profiles`
--
ALTER TABLE `profiles`
  MODIFY `profile_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `reports`
--
ALTER TABLE `reports`
  MODIFY `report_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT for table `report_tests`
--
ALTER TABLE `report_tests`
  MODIFY `report_test_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=213;

--
-- AUTO_INCREMENT for table `test_results`
--
ALTER TABLE `test_results`
  MODIFY `result_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=263;

--
-- AUTO_INCREMENT for table `test_sessions`
--
ALTER TABLE `test_sessions`
  MODIFY `session_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=132;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `verification_codes`
--
ALTER TABLE `verification_codes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `vision_tests`
--
ALTER TABLE `vision_tests`
  MODIFY `test_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `exercise_logs`
--
ALTER TABLE `exercise_logs`
  ADD CONSTRAINT `exercise_logs_ibfk_1` FOREIGN KEY (`profile_id`) REFERENCES `profiles` (`profile_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `exercise_logs_ibfk_2` FOREIGN KEY (`exercise_id`) REFERENCES `eye_exercises` (`exercise_id`);

--
-- Constraints for table `game_logs`
--
ALTER TABLE `game_logs`
  ADD CONSTRAINT `game_logs_ibfk_1` FOREIGN KEY (`profile_id`) REFERENCES `profiles` (`profile_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `game_logs_ibfk_2` FOREIGN KEY (`game_id`) REFERENCES `games` (`game_id`);

--
-- Constraints for table `notifications`
--
ALTER TABLE `notifications`
  ADD CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `profiles`
--
ALTER TABLE `profiles`
  ADD CONSTRAINT `profiles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `reports`
--
ALTER TABLE `reports`
  ADD CONSTRAINT `reports_ibfk_1` FOREIGN KEY (`profile_id`) REFERENCES `profiles` (`profile_id`) ON DELETE CASCADE;

--
-- Constraints for table `report_tests`
--
ALTER TABLE `report_tests`
  ADD CONSTRAINT `report_tests_ibfk_1` FOREIGN KEY (`report_id`) REFERENCES `reports` (`report_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `report_tests_ibfk_2` FOREIGN KEY (`test_id`) REFERENCES `vision_tests` (`test_id`);

--
-- Constraints for table `test_results`
--
ALTER TABLE `test_results`
  ADD CONSTRAINT `test_results_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `test_sessions` (`session_id`) ON DELETE CASCADE;

--
-- Constraints for table `test_sessions`
--
ALTER TABLE `test_sessions`
  ADD CONSTRAINT `test_sessions_ibfk_1` FOREIGN KEY (`profile_id`) REFERENCES `profiles` (`profile_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `test_sessions_ibfk_2` FOREIGN KEY (`test_id`) REFERENCES `vision_tests` (`test_id`);

--
-- Constraints for table `verification_codes`
--
ALTER TABLE `verification_codes`
  ADD CONSTRAINT `verification_codes_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

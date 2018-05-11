INSERT INTO `bet_group` (`id`, `name`, `password`, `score`) VALUES (6,'FHNW','$argon2i$v=19$m=65536,t=100,p=2$QfwJbni+Ur3fLYfpEySMSA$Tn0E0ViSFbWRGiX9BtghJNo2Iub8KWMwNPtxv5eUudc',0);
INSERT INTO `game` (`id`, `away_team_goals`, `date_time`, `home_team_goals`, `away_team_id`, `home_team_id`, `location_id`, `phase_id`) VALUES (12,0,'2018-05-10 12:23:45',3,9,8,13,14);
INSERT INTO `location` (`id`, `name`) VALUES (13,'Moskau');
INSERT INTO `phase` (`id`, `name`) VALUES (14,'Gruppenphase');
INSERT INTO `role` (`id`, `name`) VALUES (1,'ROLE_USER'), (2,'ROLE_ADMIN');
INSERT INTO `tournament_group` (`id`, `name`) VALUES (7,'E');
INSERT INTO `tournament_team` (`id`, `name`, `group_id`) VALUES (8,'Schweiz',7), (9,'Brasilien',7), (10,'Costa Rica',7), (11,'Serbien',7);
INSERT INTO `user` (`id`, `daily_results`, `email`, `name`, `password`, `reminders`) VALUES (3,b'1','yanick.schraner@students.fhnw.ch','Yanick','$argon2i$v=19$m=65536,t=100,p=2$QfwJbni+Ur3fLYfpEySMSA$Tn0E0ViSFbWRGiX9BtghJNo2Iub8KWMwNPtxv5eUudc',b'1'), (4,b'1','tom.ohme@students.fhnw.ch','Tom','$argon2i$v=19$m=65536,t=100,p=2$QfwJbni+Ur3fLYfpEySMSA$Tn0E0ViSFbWRGiX9BtghJNo2Iub8KWMwNPtxv5eUudc',b'1'), (5,b'1','benjamin.zumbrunn@students.fhnw.ch','Beni','$argon2i$v=19$m=65536,t=100,p=2$QfwJbni+Ur3fLYfpEySMSA$Tn0E0ViSFbWRGiX9BtghJNo2Iub8KWMwNPtxv5eUudc',b'1');
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES (3,1), (3,2), (4,1), (5,1);
-- Theater 테이블 더미 데이터 삽입
INSERT INTO theater (theater_name) VALUES ('강남 메가박스');
INSERT INTO theater (theater_name) VALUES ('신촌 CGV');
INSERT INTO theater (theater_name) VALUES ('용산 아이맥스');

-- Screen 테이블 더미 데이터 삽입 (상영관은 Theater를 참조)
-- 1번 강남메가박스, 2번 신촌 CGV, 3번 용산 아이맥스;
INSERT INTO screen (theater_id, screen_name, total_seats, remaining_seats) VALUES (1, '1관', 200, 200);
INSERT INTO screen (theater_id, screen_name, total_seats, remaining_seats) VALUES (1, '2관', 150, 150);
INSERT INTO screen (theater_id, screen_name, total_seats, remaining_seats) VALUES (2, '3관', 180, 180);
INSERT INTO screen (theater_id, screen_name, total_seats, remaining_seats) VALUES (3, '아이맥스관', 300, 300);

-- Movie 테이블 더미 데이터 삽입
INSERT INTO movie (movie_name, movie_code, director_name, audit_number, show_time, open_date, genre_name, companys, status, imagename)
VALUES ('인터스텔라', 'MOV001', '크리스토퍼 놀란', '12345', '169분', '2014-11-06', 'SF', '워너 브라더스', 'SHOW', 'interstellar.jpg');

INSERT INTO movie (movie_name, movie_code, director_name, audit_number, show_time, open_date, genre_name, companys, status, imagename)
VALUES ('어벤져스: 엔드게임', 'MOV002', '안소니 루소', '12346', '181분', '2019-04-24', '액션', '마블 스튜디오', 'END', 'endgame.jpg');

INSERT INTO movie (movie_name, movie_code, director_name, audit_number, show_time, open_date, genre_name, companys, status, imagename)
VALUES ('기생충', 'MOV003', '봉준호', '12347', '132분', '2019-05-30', '드라마', 'CJ ENM', 'SHOW', 'parasite.jpg');

-- Showtime 테이블 더미 데이터 삽입 (Movie와 Screen을 참조)
INSERT INTO showtime (screen_id, movie_id, showtime_date, showtime_time) VALUES (1, 1, '2024-12-18', '14:00:00');
INSERT INTO showtime (screen_id, movie_id, showtime_date, showtime_time) VALUES (2, 2, '2024-12-18', '16:00:00');
INSERT INTO showtime (screen_id, movie_id, showtime_date, showtime_time) VALUES (3, 3, '2024-12-19', '18:30:00');
INSERT INTO showtime (screen_id, movie_id, showtime_date, showtime_time) VALUES (4, 1, '2024-12-24', '20:00:00');

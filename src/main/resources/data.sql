-- 좌석 데이터 생성
INSERT INTO seat (hall_id, seat_code) VALUES (1, 'A-1');
INSERT INTO seat (hall_id, seat_code) VALUES (1, 'A-2');
INSERT INTO seat (hall_id, seat_code) VALUES (1, 'A-3');
INSERT INTO seat (hall_id, seat_code) VALUES (1, 'B-1');
INSERT INTO seat (hall_id, seat_code) VALUES (1, 'B-2');

-- 예약 데이터 생성 (테스트용)
-- A-2 좌석은 유저 100번이 잡고 있음 (HELD)
INSERT INTO reservation (seat_id, user_id, status, expired_at)
VALUES (2, 100, 'HELD', DATEADD('MINUTE', 5, CURRENT_TIMESTAMP()));

-- B-1 좌석은 유저 200번이 결제 완료함 (CONFIRMED)
INSERT INTO reservation (seat_id, user_id, status, expired_at)
VALUES (4, 200, 'CONFIRMED', DATEADD('DAY', 1, CURRENT_TIMESTAMP()));
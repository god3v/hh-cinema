# 성능 테스트 보고서

## 전제 조건
- **DAU**: N명
- **1명당 1일 평균 접속 수**: 2번
- **피크 시간대의 집중률**: 평소 트래픽의 10배
- **Throughput 계산**:
    - **1일 총 접속 수** = DAU × 1명당 1일 평균 접속 수 = N × 2 = **2N** (1일 총 접속 수)
    - **1일 평균 RPS** = 1일 총 접속 수 ÷ 86,400 (초/일)= 2N ÷ 86,400 ≈ **X** **RPS**
    - **1일 최대 RPS** = 1일 평균 RPS × (최대 트래픽 / 평소 트래픽)= X × 10 = **10X RPS**
- VU: N명
- optional
    - thresholds
        - e.g p(95) 의 응답 소요 시간 200ms 이하
        - 실패율 1% 이하
- **더미데이터**:
  - 영화 테이블: 총 500건
  - 상영 일정 테이블: 10만건

## Index 적용 전

- 실제 동작 쿼리
```
select
        m.movie_id,
        m.title,
        m.movie_rating,
        m.release_date,
        m.thumbnail_url,
        m.running_time,
        m.genre,
        s.screen_name,
        ss.started_at,
        ss.ended_at 
    from
        screening_schedule ss 
    left join
        movie m 
            on m.movie_id=ss.movie_id 
    left join
        screen s 
            on s.screen_id=ss.screen_id
    where
        ss.started_at > '2025-03-23 00:00:00'
        and m.title like '%the%'
        and m.genre= 'ACTION'
    order by
        m.release_date desc,
        ss.started_at;
```

- 실행 계획

|id |select_type|table|partitions|type  |possible_keys|key    |key_len|ref                   |rows  |filtered|Extra                                       |
|---|-----------|-----|----------|------|-------------|-------|-------|----------------------|------|--------|--------------------------------------------|
|1  |SIMPLE     |ss   |          |ALL   |             |       |       |                      |99,722|33.33   |Using where; Using temporary; Using filesort|
|1  |SIMPLE     |s    |          |eq_ref|PRIMARY      |PRIMARY|8      |cinema_db.ss.screen_id|1     |100     |                                            |
|1  |SIMPLE     |m    |          |eq_ref|PRIMARY      |PRIMARY|8      |cinema_db.ss.movie_id |1     |5       |Using where                                 |

- 부하 테스트 결과
```text
     ✓ is status 200

     checks.........................: 100.00% 11079 out of 11079
     data_received..................: 2.6 GB  8.8 MB/s
     data_sent......................: 1.3 MB  4.3 kB/s
     http_req_blocked...............: avg=34.53µs min=1µs     med=7µs    max=3.73ms  p(90)=27µs   p(95)=254µs 
     http_req_connecting............: avg=21.63µs min=0s      med=0s     max=3.68ms  p(90)=0s     p(95)=189µs 
   ✗ http_req_duration..............: avg=11.91s  min=59.48ms med=9.61s  max=34.63s  p(90)=27.22s p(95)=28.77s
       { expected_response:true }...: avg=11.91s  min=59.48ms med=9.61s  max=34.63s  p(90)=27.22s p(95)=28.77s
   ✓ http_req_failed................: 0.00%   0 out of 11079
     http_req_receiving.............: avg=2.27ms  min=102µs   med=2.09ms max=26.46ms p(90)=3.03ms p(95)=3.54ms
     http_req_sending...............: avg=23.75µs min=3µs     med=20µs   max=2.26ms  p(90)=39µs   p(95)=49µs  
     http_req_tls_handshaking.......: avg=0s      min=0s      med=0s     max=0s      p(90)=0s     p(95)=0s    
     http_req_waiting...............: avg=11.91s  min=57.76ms med=9.61s  max=34.63s  p(90)=27.22s p(95)=28.76s
     http_reqs......................: 11079   36.796478/s
     iteration_duration.............: avg=12.9s   min=1.05s   med=10.61s max=35.63s  p(90)=28.2s  p(95)=29.75s
     iterations.....................: 11074   36.779871/s
     vus............................: 25      min=2              max=1000
     vus_max........................: 1000    min=1000           max=1000
```

## Index 적용 후

### 적용한 인덱스
```
CREATE INDEX idx_movie_genre ON movie (genre);
CREATE FULLTEXT INDEX idx_movie_title ON movie(title);
CREATE INDEX idx_schedule_movie_id ON screening_schedule (movie_id);
CREATE INDEX idx_schedule_started_at ON screening_schedule (started_at);
```

### Like 연산
- 실행 계획

|id |select_type|table|partitions|type  |possible_keys                                |key                  |key_len|ref                   |rows|filtered|Extra                                                              |
|---|-----------|-----|----------|------|---------------------------------------------|---------------------|-------|----------------------|----|--------|-------------------------------------------------------------------|
|1  |SIMPLE     |m    |          |ref   |PRIMARY,idx_movie_genre                      |idx_movie_genre      |1      |const                 |96  |11.11   |Using index condition; Using where; Using temporary; Using filesort|
|1  |SIMPLE     |ss   |          |ref   |idx_schedule_started_at,idx_schedule_movie_id|idx_schedule_movie_id|8      |cinema_db.m.movie_id  |242 |50      |Using where                                                        |
|1  |SIMPLE     |s    |          |eq_ref|PRIMARY                                      |PRIMARY              |8      |cinema_db.ss.screen_id|1   |100     |                                                                   |

```text
-> Sort: m.release_date DESC, ss.started_at  (actual time=32.6..32.9 rows=2787 loops=1)
    -> Stream results  (cost=1361 rows=1291) (actual time=5.73..26.6 rows=2787 loops=1)
        -> Nested loop left join  (cost=1361 rows=1291) (actual time=5.7..24.1 rows=2787 loops=1)
            -> Nested loop inner join  (cost=909 rows=1291) (actual time=5.66..21.8 rows=2787 loops=1)
                -> Filter: (m.title like '%the%')  (cost=5.57 rows=10.7) (actual time=1.52..1.7 rows=34 loops=1)
                    -> Index lookup on m using idx_movie_genre (genre = 'ACTION'), with index condition: (m.movie_id is not null)  (cost=5.57 rows=96) (actual time=1.43..1.58 rows=96 loops=1)
                -> Filter: (ss.started_at > TIMESTAMP'2025-03-24 00:00:00')  (cost=61.6 rows=121) (actual time=0.36..0.582 rows=82 loops=34)
                    -> Index lookup on ss using idx_schedule_movie_id (movie_id = m.movie_id)  (cost=61.6 rows=242) (actual time=0.359..0.559 rows=200 loops=34)
            -> Single-row index lookup on s using PRIMARY (screen_id = ss.screen_id)  (cost=0.25 rows=1) (actual time=632e-6..665e-6 rows=1 loops=2787)
```

- 부하 테스트 결과
```text
     ✓ is status 200

     checks.........................: 100.00% 37358 out of 37358
     data_received..................: 8.9 GB  30 MB/s
     data_sent......................: 4.3 MB  14 kB/s
     http_req_blocked...............: avg=17.58µs min=1µs     med=6µs    max=6.93ms   p(90)=9µs    p(95)=24µs  
     http_req_connecting............: avg=8.8µs   min=0s      med=0s     max=6.88ms   p(90)=0s     p(95)=0s    
   ✗ http_req_duration..............: avg=2.6s    min=17.09ms med=2.06s  max=18.22s   p(90)=6s     p(95)=7.21s 
       { expected_response:true }...: avg=2.6s    min=17.09ms med=2.06s  max=18.22s   p(90)=6s     p(95)=7.21s 
   ✓ http_req_failed................: 0.00%   0 out of 37358
     http_req_receiving.............: avg=3.02ms  min=67µs    med=2.46ms max=127.55ms p(90)=4.24ms p(95)=5.56ms
     http_req_sending...............: avg=20.3µs  min=2µs     med=15µs   max=8.1ms    p(90)=30µs   p(95)=44µs  
     http_req_tls_handshaking.......: avg=0s      min=0s      med=0s     max=0s       p(90)=0s     p(95)=0s    
     http_req_waiting...............: avg=2.59s   min=15.48ms med=2.06s  max=18.22s   p(90)=6s     p(95)=7.21s 
     http_reqs......................: 37358   124.241266/s
     iteration_duration.............: avg=3.6s    min=1.01s   med=3.06s  max=19.22s   p(90)=7s     p(95)=8.22s 
     iterations.....................: 37358   124.241266/s
     vus............................: 8       min=2              max=1000
     vus_max........................: 1000    min=1000           max=1000

```

### MATCH AGAINST 연산

- 실제 동작 쿼리
```text
select
        ... 생략
    from
        movie me1_0 
    join
        screening_schedule sse1_0 
            on sse1_0.movie_id=me1_0.movie_id 
    join
        screen se1_0 
            on se1_0.screen_id=sse1_0.screen_id 
    where
        sse1_0.started_at>now()
        and MATCH(me1_0.title) AGAINST ('Parked' IN NATURAL LANGUAGE MODE)>0 
        and me1_0.genre='ACTION'
    order by
        me1_0.release_date desc,
        sse1_0.started_at
```

-  실행 계획

|id |select_type|table|partitions|type    |possible_keys                                |key                  |key_len|ref                   |rows|filtered|Extra                                                           |
|---|-----------|-----|----------|--------|---------------------------------------------|---------------------|-------|----------------------|----|--------|----------------------------------------------------------------|
|1  |SIMPLE     |m    |          |fulltext|PRIMARY,idx_movie_genre,idx_movie_title      |idx_movie_title      |0      |const                 |1   |19.2    |Using where; Ft_hints: rank > 0; Using temporary; Using filesort|
|1  |SIMPLE     |ss   |          |ref     |idx_schedule_started_at,idx_schedule_movie_id|idx_schedule_movie_id|8      |cinema_db.m.movie_id  |242 |50      |Using where                                                     |
|1  |SIMPLE     |s    |          |eq_ref  |PRIMARY                                      |PRIMARY              |8      |cinema_db.ss.screen_id|1   |100     |                                                                |

-  부하 테스트 결과
```text
     ✓ is status 200

     checks.........................: 100.00% 30132 out of 30132
     data_received..................: 6.9 GB  23 MB/s
     data_sent......................: 3.5 MB  12 kB/s
     http_req_blocked...............: avg=26.72µs min=1µs     med=6µs    max=18.28ms  p(90)=11µs   p(95)=31µs  
     http_req_connecting............: avg=15.47µs min=0s      med=0s     max=16.38ms  p(90)=0s     p(95)=0s    
   ✗ http_req_duration..............: avg=3.48s   min=16.7ms  med=1.45s  max=23.28s   p(90)=9.97s  p(95)=10.6s 
       { expected_response:true }...: avg=3.48s   min=16.7ms  med=1.45s  max=23.28s   p(90)=9.97s  p(95)=10.6s 
   ✓ http_req_failed................: 0.00%   0 out of 30132
     http_req_receiving.............: avg=3.38ms  min=64µs    med=2.75ms max=101.07ms p(90)=4.88ms p(95)=6.85ms
     http_req_sending...............: avg=23.21µs min=2µs     med=16µs   max=16.7ms   p(90)=34µs   p(95)=49µs  
     http_req_tls_handshaking.......: avg=0s      min=0s      med=0s     max=0s       p(90)=0s     p(95)=0s    
     http_req_waiting...............: avg=3.47s   min=15.02ms med=1.45s  max=23.28s   p(90)=9.96s  p(95)=10.59s
     http_reqs......................: 30132   100.170953/s
     iteration_duration.............: avg=4.48s   min=1.01s   med=2.45s  max=24.28s   p(90)=10.97s p(95)=11.6s 
     iterations.....................: 30132   100.170953/s
     vus............................: 7       min=2              max=1000
     vus_max........................: 1000    min=1000           max=1000
```

## 로컬 Caching 적용 후

### 캐싱한 데이터의 종류
- caffeine 캐시 사용

### 부하 테스트 결과
```text
     ✓ is status 200

     checks.........................: 100.00% 131715 out of 131715
     data_received..................: 30 GB   101 MB/s
     data_sent......................: 15 MB   51 kB/s
     http_req_blocked...............: avg=8.96µs  min=0s     med=2µs    max=14.87ms  p(90)=6µs    p(95)=10µs   
     http_req_connecting............: avg=4.12µs  min=0s     med=0s     max=14.83ms  p(90)=0s     p(95)=0s     
   ✓ http_req_duration..............: avg=4.62ms  min=1.14ms med=2.71ms max=1.19s    p(90)=7.18ms p(95)=10.26ms
       { expected_response:true }...: avg=4.62ms  min=1.14ms med=2.71ms max=1.19s    p(90)=7.18ms p(95)=10.26ms
   ✓ http_req_failed................: 0.00%   0 out of 131715
     http_req_receiving.............: avg=1.93ms  min=50µs   med=1.64ms max=107.61ms p(90)=3.04ms p(95)=4.09ms 
     http_req_sending...............: avg=14.36µs min=1µs    med=5µs    max=39.26ms  p(90)=18µs   p(95)=32µs   
     http_req_tls_handshaking.......: avg=0s      min=0s     med=0s     max=0s       p(90)=0s     p(95)=0s     
     http_req_waiting...............: avg=2.68ms  min=200µs  med=807µs  max=1.18s    p(90)=4.62ms p(95)=7.31ms 
     http_reqs......................: 131715  438.249849/s
     iteration_duration.............: avg=1s      min=1s     med=1s     max=2.19s    p(90)=1s     p(95)=1.01s  
     iterations.....................: 131715  438.249849/s
     vus............................: 11      min=2                max=999 
     vus_max........................: 1000    min=1000             max=1000
```

## 분산 Caching 적용 후

### 부하 테스트 결과
```text
     ✓ is status 200

     checks.........................: 100.00% 63278 out of 63278
     data_received..................: 15 GB   48 MB/s
     data_sent......................: 7.3 MB  24 kB/s
     http_req_blocked...............: avg=55.65µs min=0s     med=4µs     max=149.03ms p(90)=9µs     p(95)=15µs   
     http_req_connecting............: avg=30.03µs min=0s     med=0s      max=110.62ms p(90)=0s      p(95)=0s     
   ✗ http_req_duration..............: avg=1.09s   min=3.46ms med=11.18ms max=7.81s    p(90)=3.81s   p(95)=5.22s  
       { expected_response:true }...: avg=1.09s   min=3.46ms med=11.18ms max=7.81s    p(90)=3.81s   p(95)=5.22s  
   ✓ http_req_failed................: 0.00%   0 out of 63278
     http_req_receiving.............: avg=6.03ms  min=55µs   med=2.15ms  max=593.88ms p(90)=12.73ms p(95)=26.27ms
     http_req_sending...............: avg=81.68µs min=2µs    med=9µs     max=145.69ms p(90)=26µs    p(95)=44µs   
     http_req_tls_handshaking.......: avg=0s      min=0s     med=0s      max=0s       p(90)=0s      p(95)=0s     
     http_req_waiting...............: avg=1.09s   min=2.31ms med=8.72ms  max=7.81s    p(90)=3.8s    p(95)=5.2s   
     http_reqs......................: 63278   210.319441/s
     iteration_duration.............: avg=2.1s    min=1s     med=1.01s   max=8.81s    p(90)=4.82s   p(95)=6.23s  
     iterations.....................: 63278   210.319441/s
     vus............................: 9       min=2              max=1000
     vus_max........................: 1000    min=1000           max=1000
```
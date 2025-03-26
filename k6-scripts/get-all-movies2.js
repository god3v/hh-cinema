import http from 'k6/http';
import { sleep, check } from 'k6';

export const options = {
    stages: [
        {duration: '1m', target: 50},
        {duration: '3m', target: 1000},
        {duration: '1m', target: 0},
    ],
    thresholds: {
                http_req_duration: ['p(95)<200'], // 95% 요청의 응답 시간이 200ms 이하
                http_req_failed: ['rate<0.01'], // 실패율이 1% 미만
            },
};

export default function () {
    let res = http.get('http://localhost:8080/api/v1/movies?genre=ACTION&title=A');

    check(res, {
        'is status 200': (r) => r.status === 200
    });

    sleep(1);
}
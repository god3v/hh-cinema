-- KEYS[1] = rate:<ip>
-- ARGV[1] = limit
-- ARGV[2] = expire(sec)

-- 요청 제한
local key = KEYS[1] -- 요청 카운트 키 (rate_limit: IP)
local limit = tonumber(ARGV[1]) -- 최대 요청 횟수 (초)
local expireTime = tonumber(ARGV[2])  -- 요청 카운트 만료 시간 (초)

local current = tonumber(redis.call("GET", key)) or 0

if current + 1 > tonumber(limit) then
    return 0
else
    redis.call("INCR", key)
    if current == 0 then
        redis.call("EXPIRE", key, tonumber(expireTime))
    end
    return 1
end


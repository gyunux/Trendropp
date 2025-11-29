package com.celebstyle.api.x;

public record XMeta(
        int result_count,
        String newest_id,
        String oldest_id,
        String next_token
) {
}
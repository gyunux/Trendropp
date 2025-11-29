package com.celebstyle.api.x;

import java.util.List;

public record XSearchResponse(
        List<XTweet> data,
        XMeta meta
) {
}


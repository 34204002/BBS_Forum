package com.jiang.bbs_forum.dto.response;

import lombok.Data;
import java.util.Map;

@Data
public class LikeBatchVO {
    private Boolean postLiked;
    private Map<Integer, Boolean> commentLikeMap;
}

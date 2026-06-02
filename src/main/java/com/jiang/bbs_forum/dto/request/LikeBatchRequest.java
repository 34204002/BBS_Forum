package com.jiang.bbs_forum.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class LikeBatchRequest {
    private Integer postId;
    private List<Integer> commentIds;
}
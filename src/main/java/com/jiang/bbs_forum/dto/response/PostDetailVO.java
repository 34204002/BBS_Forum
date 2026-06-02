package com.jiang.bbs_forum.dto.response;

import com.jiang.bbs_forum.common.PageResponse;
import lombok.Data;

@Data
public class PostDetailVO {

    private PostVO post;

    private PageResponse<CommentVO> comments;
}
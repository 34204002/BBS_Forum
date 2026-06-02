package com.jiang.bbs_forum.service.user;

import com.jiang.bbs_forum.common.PageResponse;
import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.response.PointRecordVO;
import com.jiang.bbs_forum.dto.response.RankItemVO;

import java.util.List;

public interface PointService {
    void addPoints(int userId, int points, String reason);

    void consumePoints(int userId, int points, String reason);

    Response<PageResponse<PointRecordVO>> getPointRecords(int userId, int page, int size);

    Response<List<RankItemVO>> getPointsRank(int size);
}

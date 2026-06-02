package com.jiang.bbs_forum.service.user.impl;

import com.jiang.bbs_forum.common.PageResponse;
import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.response.PointRecordVO;
import com.jiang.bbs_forum.dto.response.RankItemVO;
import com.jiang.bbs_forum.entity.PointRecord;
import com.jiang.bbs_forum.entity.User;
import com.jiang.bbs_forum.mapper.PointRecordMapper;
import com.jiang.bbs_forum.mapper.UserMapper;
import com.jiang.bbs_forum.service.user.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PointServiceImpl implements PointService {

    @Autowired
    private PointRecordMapper pointRecordMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public void addPoints(int userId, int points, String reason) {
        User user = userMapper.selectById(userId);
        int balance = user.getPoints() + points;

        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setPoints(balance);
        userMapper.updateById(updateUser);

        PointRecord record = new PointRecord();
        record.setUserId(userId);
        record.setType(1);
        record.setReason(reason);
        record.setPoints(points);
        record.setBalance(balance);
        pointRecordMapper.insert(record);
    }

    @Override
    @Transactional
    public void consumePoints(int userId, int points, String reason) {
        User user = userMapper.selectById(userId);
        int balance = user.getPoints() - points;

        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setPoints(balance);
        userMapper.updateById(updateUser);

        PointRecord record = new PointRecord();
        record.setUserId(userId);
        record.setType(2);
        record.setReason(reason);
        record.setPoints(-points);
        record.setBalance(balance);
        pointRecordMapper.insert(record);
    }

    @Override
    public Response<PageResponse<PointRecordVO>> getPointRecords(int userId, int page, int size) {
        // TODO: 分页查询 point_records 表，按时间倒序
        return null;
    }

    @Override
    public Response<List<RankItemVO>> getPointsRank(int size) {
        // TODO: 按积分降序取前N名
        return null;
    }
}

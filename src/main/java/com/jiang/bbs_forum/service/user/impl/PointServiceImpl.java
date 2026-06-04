package com.jiang.bbs_forum.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiang.bbs_forum.common.PageResponse;
import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.response.PointRecordVO;
import com.jiang.bbs_forum.dto.response.RankItemVO;
import com.jiang.bbs_forum.entity.PointRecord;
import com.jiang.bbs_forum.entity.User;
import com.jiang.bbs_forum.entity.UserProfile;
import com.jiang.bbs_forum.mapper.PointRecordMapper;
import com.jiang.bbs_forum.mapper.UserMapper;
import com.jiang.bbs_forum.mapper.UserProfileMapper;
import com.jiang.bbs_forum.service.user.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PointServiceImpl implements PointService {

    @Autowired
    private PointRecordMapper pointRecordMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserProfileMapper userProfileMapper;

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
        Page<PointRecord> p = new Page<>(page, size);
        IPage<PointRecord> result = pointRecordMapper.selectPage(p,
                new LambdaQueryWrapper<PointRecord>()
                        .eq(PointRecord::getUserId, userId)
                        .orderByDesc(PointRecord::getCreateTime));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<PointRecordVO> list = result.getRecords().stream().map(r -> PointRecordVO.builder()
                .id(r.getId())
                .type(r.getType())
                .reason(r.getReason())
                .points(r.getPoints())
                .balance(r.getBalance())
                .createTime(r.getCreateTime() != null ? r.getCreateTime().format(dtf) : null)
                .build()).toList();

        return Response.success(new PageResponse<>(result.getTotal(), list, page, size));
    }

    @Override
    public Response<List<RankItemVO>> getPointsRank(int size) {
        Page<User> p = new Page<>(1, size);
        IPage<User> result = userMapper.selectPage(p,
                new LambdaQueryWrapper<User>()
                        .eq(User::getStatus, 1)
                        .orderByDesc(User::getPoints));

        List<Integer> userIds = result.getRecords().stream().map(User::getId).toList();
        Map<Integer, UserProfile> profileMap = userIds.isEmpty() ? Map.of() :
                userProfileMapper.selectList(
                        new LambdaQueryWrapper<UserProfile>().in(UserProfile::getUserId, userIds))
                        .stream().collect(Collectors.toMap(UserProfile::getUserId, up -> up));

        List<RankItemVO> list = new ArrayList<>();
        int rank = 1;
        for (User u : result.getRecords()) {
            UserProfile up = profileMap.get(u.getId());
            list.add(RankItemVO.builder()
                    .rank(rank++)
                    .userId(u.getId())
                    .username(u.getUsername())
                    .nickname(up != null ? up.getNickname() : null)
                    .avatar(up != null ? up.getAvatar() : null)
                    .points(u.getPoints())
                    .build());
        }

        return Response.success(list);
    }
}

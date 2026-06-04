package com.jiang.bbs_forum.service.user.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jiang.bbs_forum.common.Response;
import com.jiang.bbs_forum.dto.request.CreateBoardRequest;
import com.jiang.bbs_forum.dto.request.UpdateBoardRequest;
import com.jiang.bbs_forum.dto.response.BoardVO;
import com.jiang.bbs_forum.entity.Board;
import com.jiang.bbs_forum.entity.Post;
import com.jiang.bbs_forum.entity.SystemLog;
import com.jiang.bbs_forum.mapper.BoardMapper;
import com.jiang.bbs_forum.mapper.PostMapper;
import com.jiang.bbs_forum.mapper.SystemLogMapper;
import com.jiang.bbs_forum.service.user.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    private BoardMapper boardMapper;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private SystemLogMapper systemLogMapper;

    @Override
    public Response<List<BoardVO>> listBoards() {
        List<Board> boards = boardMapper.selectList(
                new LambdaQueryWrapper<Board>()
                        .eq(Board::getIsDeleted, 0)
                        .orderByAsc(Board::getSort)
        );

        List<BoardVO> list = boards.stream().map(b -> {
            BoardVO vo = new BoardVO();
            vo.setId(b.getId());
            vo.setName(b.getName());
            vo.setDescription(b.getDescription());
            vo.setSort(b.getSort());
            vo.setPostCount(b.getPostCount());
            return vo;
        }).toList();

        return Response.success(list);
    }

    @Override
    public Response<BoardVO> getBoardById(int id) {
        Board b = boardMapper.selectById(id);
        if (b == null || b.getIsDeleted() == 1) {
            return Response.error(404, "板块不存在");
        }

        BoardVO vo = new BoardVO();
        vo.setId(b.getId());
        vo.setName(b.getName());
        vo.setDescription(b.getDescription());
        vo.setSort(b.getSort());
        vo.setPostCount(b.getPostCount());

        return Response.success(vo);
    }

    @Override
    public Response<BoardVO> createBoard(CreateBoardRequest request, int adminId, String ip) {
        Board exist = boardMapper.selectOne(
                new LambdaQueryWrapper<Board>().eq(Board::getName, request.getName()));
        if (exist != null) {
            return Response.error(400, "板块名称已存在");
        }

        Board board = new Board();
        board.setName(request.getName());
        board.setDescription(request.getDescription());
        board.setSort(request.getSort() != null ? request.getSort() : 0);
        board.setPostCount(0);
        boardMapper.insert(board);

        SystemLog log = new SystemLog();
        log.setAdminId(adminId);
        log.setOperation("新增板块：" + board.getName());
        log.setIp(ip);
        systemLogMapper.insert(log);

        BoardVO vo = new BoardVO();
        vo.setId(board.getId());
        vo.setName(board.getName());
        vo.setDescription(board.getDescription());
        vo.setSort(board.getSort());
        vo.setPostCount(0);

        return Response.success("创建成功", vo);
    }

    @Override
    public Response<BoardVO> updateBoard(int boardId, UpdateBoardRequest request, int adminId, String ip) {
        Board board = boardMapper.selectById(boardId);
        if (board == null || board.getIsDeleted() == 1) {
            return Response.error(404, "板块不存在");
        }

        if (request.getName() != null) board.setName(request.getName());
        if (request.getDescription() != null) board.setDescription(request.getDescription());
        if (request.getSort() != null) board.setSort(request.getSort());
        boardMapper.updateById(board);

        SystemLog log = new SystemLog();
        log.setAdminId(adminId);
        log.setOperation("修改板块：" + board.getName());
        log.setIp(ip);
        systemLogMapper.insert(log);

        BoardVO vo = new BoardVO();
        vo.setId(board.getId());
        vo.setName(board.getName());
        vo.setDescription(board.getDescription());
        vo.setSort(board.getSort());
        vo.setPostCount(board.getPostCount());

        return Response.success("修改成功", vo);
    }

    @Override
    public Response<Void> deleteBoard(int boardId, int adminId, String ip) {
        Board board = boardMapper.selectById(boardId);
        if (board == null || board.getIsDeleted() == 1) {
            return Response.error(404, "板块不存在");
        }

        long postCount = postMapper.selectCount(
                new LambdaQueryWrapper<Post>()
                        .eq(Post::getBoardId, boardId)
                        .eq(Post::getIsDeleted, 0)
        );
        if (postCount > 0) {
            return Response.error(400, "板块下还有帖子，无法删除");
        }

        boardMapper.deleteById(boardId);

        SystemLog log = new SystemLog();
        log.setAdminId(adminId);
        log.setOperation("删除板块：" + board.getName());
        log.setIp(ip);
        systemLogMapper.insert(log);

        return Response.success("删除成功", null);
    }
}

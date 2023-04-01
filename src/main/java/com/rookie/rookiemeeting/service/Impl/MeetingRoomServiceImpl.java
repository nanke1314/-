package com.rookie.rookiemeeting.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rookie.rookiemeeting.entity.Meeting;
import com.rookie.rookiemeeting.entity.MeetingRoom;
import com.rookie.rookiemeeting.mapper.MeetingMapper;
import com.rookie.rookiemeeting.mapper.MeetingRoomMapper;
import com.rookie.rookiemeeting.service.IMeetingRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MeetingRoomServiceImpl extends ServiceImpl<MeetingRoomMapper, MeetingRoom> implements IMeetingRoomService {

    @Autowired
    private MeetingRoomMapper meetingRoomMapper;
    @Autowired
    private MeetingMapper meetingMapper;
    //查询可用的会议室
    @Override
    public List<MeetingRoom> queryAll() {
        return meetingRoomMapper.queryAll();
    }

    @Override
    public IPage<MeetingRoom> mrPage(Integer pageNum, Integer pageSize, String roomname, String roomnum) {
        Page<MeetingRoom> meetingRoomPage = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<MeetingRoom> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(MeetingRoom::getRoomname, roomname);
        queryWrapper.like(MeetingRoom::getRoomnum, roomnum);
        return meetingRoomMapper.selectPage(meetingRoomPage, queryWrapper);
    }

    @Override
    public Integer removeMeetingRoom(Integer roomid) {
        LambdaQueryWrapper<Meeting> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Meeting::getRoomid,roomid);
        List<Meeting> meetings = meetingMapper.selectList(lambdaQueryWrapper);
        if (!meetings.isEmpty()){
            return -1;
        }
        return meetingRoomMapper.deleteById(roomid);
    }


}

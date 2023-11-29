package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author jinwang
 * @Date 2023/11/29 9:18
 * @Version 1.0 （版本号）
 */

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 统计指定时间内的营业额
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        // dateList用来存放时间
        List<LocalDate> dateList = date2List(begin, end);

        // 查询数据库 获得营业额
        String collect = dateList.stream().map(x -> {
            LocalDateTime beginTime = LocalDateTime.of(x, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(x, LocalTime.MAX);
            return Double.toString(orderMapper.statisticsAmount(beginTime, endTime, Orders.COMPLETED));
        }).collect(Collectors.joining(","));
        String date = StringUtils.join(dateList, ",");
        return TurnoverReportVO.builder().dateList(date).turnoverList(collect).build();
    }

    @Override
    public UserReportVO getuserStatistic(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = date2List(begin, end);
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totUserList = new ArrayList<>();
        dateList.forEach(x ->{
            LocalDateTime beginTime = LocalDateTime.of(x, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(x, LocalTime.MAX);
            Map map = new HashMap();
            map.put("end", endTime);
            totUserList.add(userMapper.getUserByTime(map));
            map.put("begin", beginTime);
            newUserList.add(userMapper.getUserByTime(map));
        });
        return UserReportVO.builder().dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totUserList, ",")).build();
    }

    private List<LocalDate> date2List(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        for (LocalDate date = begin; !date.equals(end); date = date.plusDays(1)) {
            dateList.add(date);
        }
        dateList.add(end);
        return dateList;
    }
}

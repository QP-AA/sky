package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
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

    @Autowired
    private OrderDetailMapper orderDetailMapper;

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

    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        OrderReportVO orderReportVO = new OrderReportVO();
        List<LocalDate> dateList = date2List(begin, end);
        List<Integer> validOrderListRes = new ArrayList<>();
        List<Integer> totOrderListRes = new ArrayList<>();
        Map totOrderList = new HashMap<>();
        Map validOrderList = new HashMap<>();
        validOrderList.put("status", Orders.COMPLETED);
        validOrderList.put("begin", LocalDateTime.of(begin, LocalTime.MIN));
        validOrderList.put("end", LocalDateTime.of(end, LocalTime.MAX));
        totOrderList.put("begin", LocalDateTime.of(begin, LocalTime.MIN));
        totOrderList.put("end", LocalDateTime.of(end, LocalTime.MAX));
        Integer validOrder = orderMapper.getOrder(validOrderList);
        Integer totOrder = orderMapper.getOrder(totOrderList);
        dateList.forEach(x -> {
            validOrderList.put("begin", LocalDateTime.of(x, LocalTime.MIN));
            validOrderList.put("end", LocalDateTime.of(x, LocalTime.MAX));
            totOrderList.put("begin", LocalDateTime.of(x, LocalTime.MIN));
            totOrderList.put("end", LocalDateTime.of(x, LocalTime.MAX));
            validOrderListRes.add(orderMapper.getOrder(validOrderList));
            totOrderListRes.add(orderMapper.getOrder(totOrderList));
        });

        orderReportVO.setDateList(StringUtils.join(dateList));
        orderReportVO.setTotalOrderCount(totOrder);
        orderReportVO.setValidOrderCount(validOrder);
        orderReportVO.setOrderCompletionRate(totOrder != 0 ? 1.0 * validOrder / totOrder : 0.0);
        orderReportVO.setOrderCountList(StringUtils.join(totOrderListRes, ","));
        orderReportVO.setValidOrderCountList(StringUtils.join(totOrderListRes, ","));
        return orderReportVO;
    }

    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {
        List<GoodsSalesDTO> goodsSalesDTOS = orderDetailMapper.getTop10(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));

        String nameList = goodsSalesDTOS.stream().map(GoodsSalesDTO::getName).collect(Collectors.joining(","));
        String numberList = goodsSalesDTOS.stream().map(x -> {
            return Integer.toString(x.getNumber());
        }).collect(Collectors.joining(","));
        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList).build();
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

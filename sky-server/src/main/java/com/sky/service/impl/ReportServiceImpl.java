package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
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

    @Autowired
    private WorkspaceService workspaceService;

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

    /**
     * 导出运营数据
     * @param response
     */
    @Override
    public void exportBussinessDate(HttpServletResponse response) {
        // 查询数据库获取最近30天数据
        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.now().minusDays(30), LocalDateTime.now().minusDays(1));
        // 数据写入文件
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            XSSFWorkbook excel = new XSSFWorkbook(in);

            // 填充数据
            XSSFSheet sheet = excel.getSheet("sheet1");
            sheet.getRow(1).getCell(1).setCellValue("时间：" + LocalDateTime.now());
            sheet.getRow(3).getCell(2).setCellValue(businessData.getTurnover());
            sheet.getRow(3).getCell(4).setCellValue(businessData.getOrderCompletionRate());
            sheet.getRow(3).getCell(6).setCellValue(businessData.getNewUsers());
            XSSFRow row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());

            LocalDate begin = LocalDate.now().minusDays(30);
            for (int i = 0; i < 30; i++) {
                LocalDate date = begin.plusDays(i);
                businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                row = sheet.getRow(i + 7);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }

            // 通过输出流将Excel文件下载到客户端浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);

            // 关闭资源
            outputStream.close();
            excel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

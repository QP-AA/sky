package com.sky.service;

import com.sky.vo.TurnoverReportVO;

import java.time.LocalDate;

/**
 * @Author jinwang
 * @Date 2023/11/29 9:18
 * @Version 1.0 （版本号）
 */
public interface ReportService {
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);
}

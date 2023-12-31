package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    public void save(EmployeeDTO emp);

    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    public void startOrStop(Integer status, Long id);

    public void update(EmployeeDTO emp);

    public Employee getById(Long id);
}

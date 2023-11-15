package com.sky.mapper;

import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    @Insert("INSERT INTO employee(id_number, name, phone, sex, username, update_time, create_time, status, password, create_user, update_user)" +
            "VALUES(#{idNumber}, #{name}, #{phone}, #{sex}, #{username}, #{updateTime}, #{createTime}, #{status}, #{password}, #{createUser}, #{updateUser})")
    public void save(Employee emp);
}

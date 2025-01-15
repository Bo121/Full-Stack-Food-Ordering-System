package com.app.quickbite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.app.quickbite.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee>{
}

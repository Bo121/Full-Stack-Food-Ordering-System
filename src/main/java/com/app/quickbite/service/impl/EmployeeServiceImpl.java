package com.app.quickbite.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.app.quickbite.entity.Employee;
import com.app.quickbite.mapper.EmployeeMapper;
import com.app.quickbite.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService{
}

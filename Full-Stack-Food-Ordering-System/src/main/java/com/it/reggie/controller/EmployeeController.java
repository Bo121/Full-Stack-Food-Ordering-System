package com.it.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.reggie.common.R;
import com.it.reggie.entity.Employee;
import com.it.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * Employee login
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        // 1. MD5 encryption for the password submitted from the page
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2. Query the database by the username submitted from the page
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // 3. If no result is found, return login failure
        if(emp == null){
            return R.error("Login failed");
        }

        // 4. Compare the password, if not matched, return login failure
        if(!emp.getPassword().equals(password)){
            return R.error("Login failed");
        }

        // 5. Check the employee status, if disabled, return the employee is disabled
        if(emp.getStatus() == 0){
            return R.error("Account is disabled");
        }

        // 6. Login successful, store the employee ID in the Session and return login success
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * Employee logout
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        // Clear the current logged-in employee ID stored in the Session
        request.getSession().removeAttribute("employee");
        return R.success("Logout successful");
    }

    /**
     * Add new employee
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){
        log.info("Add new employee, employee information: {}", employee.toString());

        // Set initial password to "123456" and encrypt it with MD5
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        // employee.setCreateTime(LocalDateTime.now());
        // employee.setUpdateTime(LocalDateTime.now());

        // Get the ID of the currently logged-in user
        // Long empId = (Long) request.getSession().getAttribute("employee");

        // employee.setCreateUser(empId);
        // employee.setUpdateUser(empId);

        employeeService.save(employee);

        return R.success("New employee added successfully");
    }

    /**
     * Employee information pagination query
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);

        // Construct pagination constructor
        Page pageInfo = new Page(page, pageSize);

        // Construct condition constructor
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        // Add filter condition
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        // Add sorting condition
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        // Execute query
        employeeService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * Update employee information by ID
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){
        log.info(employee.toString());

        long id = Thread.currentThread().getId();
        log.info("Thread ID: {}", id);
        // Long empId = (Long) request.getSession().getAttribute("employee");
        // employee.setUpdateTime(LocalDateTime.now());
        // employee.setUpdateUser(empId);
        employeeService.updateById(employee);

        return R.success("Employee information updated successfully");
    }

    /**
     * Query employee information by ID
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("Query employee information by ID...");
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
        return R.error("No corresponding employee information found");
    }
}

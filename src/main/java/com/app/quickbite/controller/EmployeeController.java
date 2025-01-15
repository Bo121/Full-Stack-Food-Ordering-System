package com.app.quickbite.controller;

import com.app.quickbite.common.R;
import com.app.quickbite.entity.Employee;
import com.app.quickbite.service.EmployeeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
     * <h2>Login Validation</h2>
     * <p>From the frontend, it is known that the login request is made to `/employee/login`, 
     * along with JSON-formatted `username` and `password`.
     *
     * <p>Business logic:<br>
     * 1. Encrypt the password submitted from the page using MD5.<br>
     * 2. Query the database using the username.<br>
     * 3. Check if the query result is null. If it is, the username does not exist, and an error is returned.<br>
     * 4. If the username exists, check if the password matches. If it doesn't, return an error.<br>
     * 5. Check the status of the employee. If the status is 0, the employee is disabled, and an error is returned.<br>
     * 6. If everything is correct, store the employee ID in the session and return a success message.<br>
     * </p>
     *
     * @param request  The request object used to record session information for account validation
     * @param employee Annotated with @RequestBody to receive JSON data. The `Employee` object must have 
     *                 member variables corresponding to the keys in the JSON data.
     * @return Generic response object
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

        // 1. Encrypt the password using MD5
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes()); // DigestUtils is a Spring utility class for encryption.

        // 2. Query the database using the username (using MyBatis-Plus)
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>(); // MyBatis-Plus query wrapper
        queryWrapper.eq(Employee::getUsername, employee.getUsername()); // Add query condition: username = input username
        Employee emp = employeeService.getOne(queryWrapper); // Query for one record since the username is unique

        // 3. Check if the username exists
        if (emp == null) {
            return R.error("Invalid username or password!");
        }

        // 4. Check if the password matches
        if (!emp.getPassword().equals(password)) {
            return R.error("Invalid username or password!");
        }

        // 5. Check the employee's status
        if (emp.getStatus() == 0) {
            return R.error("Invalid username or password!"); // Return generic error for security
        }

        // 6. Store the employee ID in the session and return success
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * <h2>Logout</h2>
     * Clears the session containing the current logged-in employee's ID.
     *
     * @param request The request object to be destroyed
     * @return Generic response object
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        // Clear the session containing the employee's ID
        request.getSession().removeAttribute("employee");
        return R.success("Logout successful");
    }

    /**
     * <h2>Add New Employee</h2>
     * <p>New employees are assigned a default password, which is the last six digits of their ID number.
     *
     * @param request  The current request object to get the operator's ID
     * @param employee Employee information sent from the frontend, received as JSON using @RequestBody
     * @return Generic response object
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("Adding new employee, employee info: {}", employee.toString());
        
        // Set default password as the last six digits of the ID number, encrypted with MD5
        String password = employee.getIdNumber().substring(employee.getIdNumber().length() - 6);
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        employee.setPassword(password);

        // Save employee information
        employeeService.save(employee);

        return R.success("Employee added successfully");
    }

    /**
     * <h2>Paginate and Query Employees by Name</h2>
     * <p>If `name` is null, all employees are queried.
     *
     * @param page     Current page number sent from the frontend
     * @param pageSize Number of items per page sent from the frontend
     * @param name     Query condition, employee name. If null, all employees are queried.
     * @return A Page object containing pagination information and query results
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("Paginate query, page: {}, pageSize: {}, name: {}", page, pageSize, name);

        // Construct a pagination object
        Page<Employee> pageParam = new Page<>(page, pageSize);

        // Construct a query wrapper
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name); // Add filter condition
        queryWrapper.orderByDesc(Employee::getUpdateTime); // Sort by update time in descending order

        // Query employee information
        employeeService.page(pageParam, queryWrapper);

        return R.success(pageParam);
    }

    /**
     * <h2>Update Employee Information by ID</h2>
     *
     * @param employee JSON data sent from the frontend
     * @return Update result
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("Updating employee info, parameters: {}", employee);

        // Update employee information by ID
        employeeService.updateById(employee);

        return R.success("Employee information updated successfully");
    }

    /**
     * <h2>Query Employee Information by ID</h2>
     *
     * @param id Employee ID sent from the frontend. @PathVariable indicates the parameter is retrieved from the URL path.
     * @return Query result
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("Querying employee by ID, parameter: {}", id);
        Employee employee = employeeService.getById(id);

        if (employee != null) {
            return R.success(employee);
        }
        return R.error("Employee information not found");
    }
}

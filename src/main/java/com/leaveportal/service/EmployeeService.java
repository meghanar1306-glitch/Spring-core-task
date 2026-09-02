package com.leaveportal.service;

import com.leaveportal.entity.Employee;
import com.leaveportal.service.exception.InvalidLoginException;

public interface EmployeeService {

    Employee authenticate(String employeeId, String password) throws InvalidLoginException;

    Employee getEmployeeById(String employeeId);
}

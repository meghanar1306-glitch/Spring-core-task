package com.leaveportal.service;

import com.leaveportal.entity.Employee;
import com.leaveportal.repository.EmployeeRepository;
import com.leaveportal.service.exception.InvalidLoginException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Employee authenticate(String employeeId, String password) throws InvalidLoginException {
        // Note: employeeId/password values themselves are never printed below,
        // only the outcome, so credentials never end up in the console output.
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);

        if (!employeeOpt.isPresent() || !employeeOpt.get().getPassword().equals(password)) {
            System.out.println("Failed login attempt for an unrecognized or mismatched employee ID");
            throw new InvalidLoginException("Invalid employee ID or password. Please try again.");
        }

        System.out.println("Employee " + employeeId + " logged in successfully");
        return employeeOpt.get();
    }

    @Override
    @Transactional(readOnly = true)
    public Employee getEmployeeById(String employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> {
                    System.out.println("Expected employee " + employeeId + " was not found while loading dashboard data");
                    return new IllegalStateException("Employee not found");
                });
    }
}

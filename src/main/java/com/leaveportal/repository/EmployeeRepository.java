package com.leaveportal.repository;

import com.leaveportal.entity.Employee;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;


@Repository
public class EmployeeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Employee> findById(String employeeId) {
        Employee employee = entityManager.find(Employee.class, employeeId);
        return Optional.ofNullable(employee);
    }

    public void update(Employee employee) {
        entityManager.merge(employee);
    }
}

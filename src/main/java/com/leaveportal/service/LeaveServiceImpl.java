package com.leaveportal.service;

import com.leaveportal.entity.Employee;
import com.leaveportal.entity.LeaveRequest;
import com.leaveportal.entity.LeaveStatus;
import com.leaveportal.entity.LeaveType;
import com.leaveportal.repository.EmployeeRepository;
import com.leaveportal.repository.LeaveRequestRepository;
import com.leaveportal.service.exception.InsufficientLeaveBalanceException;
import com.leaveportal.service.exception.InvalidLeaveRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Service
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;

    public LeaveServiceImpl(LeaveRequestRepository leaveRequestRepository, EmployeeRepository employeeRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    public LeaveRequest applyLeave(String employeeId, String leaveTypeRaw, LocalDate fromDate, LocalDate toDate, String reason)
            throws InvalidLeaveRequestException, InsufficientLeaveBalanceException {

        // 1. Basic input validation
        if (leaveTypeRaw == null || leaveTypeRaw.trim().isEmpty()) {
            throw new InvalidLeaveRequestException("Please select a leave type.");
        }
        if (fromDate == null || toDate == null) {
            throw new InvalidLeaveRequestException("Please provide both a from date and a to date.");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new InvalidLeaveRequestException("Please provide a reason for the leave.");
        }

        LeaveType leaveType;
        try {
            leaveType = LeaveType.valueOf(leaveTypeRaw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new InvalidLeaveRequestException("The selected leave type is not supported.");
        }

        // 2. Date-range validation
        if (toDate.isBefore(fromDate)) {
            throw new InvalidLeaveRequestException("The 'to date' cannot be before the 'from date'.");
        }
        if (fromDate.isBefore(LocalDate.now())) {
            throw new InvalidLeaveRequestException("The 'from date' cannot be in the past.");
        }

        int numberOfDays = (int) (ChronoUnit.DAYS.between(fromDate, toDate) + 1);

        // 3. Load employee and check leave balance
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalStateException("Employee not found: " + employeeId));

        if (numberOfDays > employee.getLeaveBalance()) {
            throw new InsufficientLeaveBalanceException(
                    "Insufficient leave balance. Available: " + employee.getLeaveBalance()
                            + " day(s), requested: " + numberOfDays + " day(s).");
        }

        // 4. Build and persist the leave request
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setFromDate(fromDate);
        leaveRequest.setToDate(toDate);
        leaveRequest.setNumberOfDays(numberOfDays);
        leaveRequest.setReason(reason.trim());
        leaveRequest.setStatus(LeaveStatus.PENDING);
        leaveRequest.setCreatedDate(java.time.LocalDateTime.now());

        leaveRequestRepository.save(leaveRequest);

        // 5. Update the employee's leave balance (same transaction as the save above)
        employee.setLeaveBalance(employee.getLeaveBalance() - numberOfDays);
        employeeRepository.update(employee);

        System.out.println("Leave request submitted for employee " + employeeId + " : " + numberOfDays + " day(s) of " + leaveType + " leave");

        return leaveRequest;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequest> getLeaveHistory(String employeeId) {
        return leaveRequestRepository.findByEmployeeId(employeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(String employeeId, LeaveStatus status) {
        return leaveRequestRepository.countByEmployeeIdAndStatus(employeeId, status);
    }
}

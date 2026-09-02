package com.leaveportal.service;

import com.leaveportal.entity.LeaveRequest;
import com.leaveportal.entity.LeaveStatus;
import com.leaveportal.service.exception.InsufficientLeaveBalanceException;
import com.leaveportal.service.exception.InvalidLeaveRequestException;

import java.time.LocalDate;
import java.util.List;

public interface LeaveService {


    LeaveRequest applyLeave(String employeeId, String leaveType, LocalDate fromDate, LocalDate toDate, String reason)
            throws InvalidLeaveRequestException, InsufficientLeaveBalanceException;

    List<LeaveRequest> getLeaveHistory(String employeeId);

    long countByStatus(String employeeId, LeaveStatus status);
}

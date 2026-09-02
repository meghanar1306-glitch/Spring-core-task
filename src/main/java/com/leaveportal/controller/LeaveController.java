package com.leaveportal.controller;

import com.leaveportal.entity.LeaveRequest;
import com.leaveportal.entity.LeaveType;
import com.leaveportal.filter.AuthenticationFilter;
import com.leaveportal.service.LeaveService;
import com.leaveportal.service.exception.InsufficientLeaveBalanceException;
import com.leaveportal.service.exception.InvalidLeaveRequestException;
import org.springframework.stereotype.Controller;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;

@Controller
public class LeaveController {

    private final LeaveService leaveService;

    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @GetMapping("/leave/apply")
    public String showApplyLeaveForm(Model model) {
        model.addAttribute("leaveTypes", LeaveType.values());
        return "applyLeave";
    }

    @PostMapping("/leave/apply")
    public String submitLeaveApplication(HttpSession session,
                                          @RequestParam("leaveType") String leaveType,
                                          @RequestParam("fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                          @RequestParam("toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                          @RequestParam("reason") String reason,
                                          Model model) {

        String employeeId = (String) session.getAttribute(AuthenticationFilter.SESSION_ATTR_EMPLOYEE_ID);

        try {
            LeaveRequest saved = leaveService.applyLeave(employeeId, leaveType, fromDate, toDate, reason);
            model.addAttribute("successMessage",
                    "Leave request #" + saved.getRequestId() + " submitted successfully and is pending approval.");
            model.addAttribute("leaveTypes", LeaveType.values());
            return "applyLeave";

        } catch (InvalidLeaveRequestException | InsufficientLeaveBalanceException ex) {
            // Both are expected, user-facing validation failures - show the message, no stack trace.
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("leaveTypes", LeaveType.values());
            return "applyLeave";

        } catch (Exception ex) {
            // Anything unexpected: print full details for troubleshooting, show a generic safe message to the user.
            System.out.println("Unexpected error while submitting leave request for employee " + employeeId);
            ex.printStackTrace();
            model.addAttribute("errorMessage", "Something went wrong while submitting your leave request. Please try again.");
            model.addAttribute("leaveTypes", LeaveType.values());
            return "applyLeave";
        }
    }

    @GetMapping("/leave/history")
    public String leaveHistory(HttpSession session, Model model) {
        String employeeId = (String) session.getAttribute(AuthenticationFilter.SESSION_ATTR_EMPLOYEE_ID);
        List<LeaveRequest> requests = leaveService.getLeaveHistory(employeeId);
        model.addAttribute("leaveRequests", requests);
        return "leaveHistory";
    }
}

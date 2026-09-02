package com.leaveportal.controller;

import com.leaveportal.entity.Employee;
import com.leaveportal.entity.LeaveStatus;
import com.leaveportal.filter.AuthenticationFilter;
import com.leaveportal.service.EmployeeService;
import com.leaveportal.service.LeaveService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {

    private final EmployeeService employeeService;
    private final LeaveService leaveService;

    public DashboardController(EmployeeService employeeService, LeaveService leaveService) {
        this.employeeService = employeeService;
        this.leaveService = leaveService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session,@CookieValue(value = "dashboardViewPreference", defaultValue = "detailed") String viewPreference, Model model) {

        String employeeId = (String) session.getAttribute(AuthenticationFilter.SESSION_ATTR_EMPLOYEE_ID);

        Employee employee = employeeService.getEmployeeById(employeeId);
        long pendingCount = leaveService.countByStatus(employeeId, LeaveStatus.PENDING);
        long approvedCount = leaveService.countByStatus(employeeId, LeaveStatus.APPROVED);

        model.addAttribute("employee", employee);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("approvedCount", approvedCount);
        model.addAttribute("viewPreference", viewPreference);

        return "dashboard";
    }
}

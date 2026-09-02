package com.leaveportal.controller;

import com.leaveportal.entity.Employee;
import com.leaveportal.filter.AuthenticationFilter;
import com.leaveportal.service.EmployeeService;
import com.leaveportal.service.exception.InvalidLoginException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    private static final String VIEW_PREF_COOKIE = "dashboardViewPreference";

    private final EmployeeService employeeService;

    public LoginController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/login")
    public String showLoginForm(HttpSession session) {
        // If already logged in, skip the login page.
        if (session.getAttribute(AuthenticationFilter.SESSION_ATTR_EMPLOYEE_ID) != null) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("employeeId") String employeeId,
                                @RequestParam("password") String password,
                                HttpServletRequest request,
                                Model model) {
        try {
            Employee employee = employeeService.authenticate(employeeId.trim(), password);

            // Session: remember which employee is logged in. No password is stored here.
            HttpSession session = request.getSession(true);
            session.setAttribute(AuthenticationFilter.SESSION_ATTR_EMPLOYEE_ID, employee.getEmployeeId());

            return "redirect:/dashboard";

        } catch (InvalidLoginException ex) {
            // Meaningful, safe message shown back on the login page - no stack trace, no credentials.
            model.addAttribute("errorMessage", ex.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String employeeId = (String) session.getAttribute(AuthenticationFilter.SESSION_ATTR_EMPLOYEE_ID);
            session.invalidate();
            System.out.println("Employee " + employeeId + " logged out");
        }
        return "redirect:/login";
    }


    @PostMapping("/preferences/dashboard-view")
    public String setDashboardViewPreference(@RequestParam("view") String view,
                                              HttpServletResponse response) {
        String safeView = "compact".equalsIgnoreCase(view) ? "compact" : "detailed";

        Cookie cookie = new Cookie(VIEW_PREF_COOKIE, safeView);
        cookie.setPath("/");
        cookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
        response.addCookie(cookie);

        return "redirect:/dashboard";
    }
}

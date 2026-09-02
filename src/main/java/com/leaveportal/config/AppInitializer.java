package com.leaveportal.config;

import com.leaveportal.filter.AuthenticationFilter;
import com.leaveportal.filter.RequestTracingFilter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.EnumSet;

import static jakarta.servlet.DispatcherType.REQUEST;

public class AppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext sc) throws ServletException {
        registerRootContext(sc);
        registerDispatcherServlet(sc);
        registerFilters(sc);

        // Same 30 minute session timeout as <session-config> in web.xml.
        sc.setSessionTimeout(30);
    }

    private void registerRootContext(ServletContext sc) {
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(RootConfig.class);
        sc.addListener(new ContextLoaderListener(rootContext));
    }


    private void registerDispatcherServlet(ServletContext sc) {
        AnnotationConfigWebApplicationContext webContext = new AnnotationConfigWebApplicationContext();
        webContext.register(WebConfig.class);

        DispatcherServlet dispatcherServlet = new DispatcherServlet(webContext);

        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);

        ServletRegistration.Dynamic registration = sc.addServlet("mvc-dispatcher", dispatcherServlet);
        registration.setLoadOnStartup(1);
        registration.addMapping("/");
    }


    private void registerFilters(ServletContext sc) {
        FilterRegistration.Dynamic requestTracingFilter =
                sc.addFilter("requestTracingFilter", RequestTracingFilter.class);
        requestTracingFilter.addMappingForUrlPatterns(EnumSet.of(REQUEST), false, "/*");

        FilterRegistration.Dynamic authenticationFilter =
                sc.addFilter("authenticationFilter", AuthenticationFilter.class);
        authenticationFilter.addMappingForUrlPatterns(EnumSet.of(REQUEST), false,
                "/dashboard", "/leave/*", "/preferences/*");
    }
}

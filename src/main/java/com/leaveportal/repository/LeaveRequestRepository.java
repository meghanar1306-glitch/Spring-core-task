package com.leaveportal.repository;

import com.leaveportal.entity.LeaveRequest;
import com.leaveportal.entity.LeaveStatus;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Repository
public class LeaveRequestRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public LeaveRequest save(LeaveRequest leaveRequest) {
        entityManager.persist(leaveRequest);
        return leaveRequest;
    }

    public List<LeaveRequest> findByEmployeeId(String employeeId) {
        TypedQuery<LeaveRequest> query = entityManager.createQuery(
                "SELECT lr FROM LeaveRequest lr WHERE lr.employee.employeeId = :employeeId " +
                        "ORDER BY lr.createdDate DESC", LeaveRequest.class);
        query.setParameter("employeeId", employeeId);
        return query.getResultList();
    }

    public long countByEmployeeIdAndStatus(String employeeId, LeaveStatus status) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(lr) FROM LeaveRequest lr WHERE lr.employee.employeeId = :employeeId " +
                        "AND lr.status = :status", Long.class);
        query.setParameter("employeeId", employeeId);
        query.setParameter("status", status);
        return query.getSingleResult();
    }
}

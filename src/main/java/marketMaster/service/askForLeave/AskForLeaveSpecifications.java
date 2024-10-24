package marketMaster.service.askForLeave;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import marketMaster.bean.askForLeave.AskForLeaveBean;

public class AskForLeaveSpecifications {

    public static Specification<AskForLeaveBean> filterByEmpId(String employeeId) {
        return (root, query, criteriaBuilder) -> {
            if (employeeId == null || employeeId.isEmpty()) {
                return criteriaBuilder.conjunction(); 
            }
            return criteriaBuilder.like(root.get("empBean").get("employeeId"), "%" + employeeId + "%");
        };
    }
    
    

    public static Specification<AskForLeaveBean> filterByStartTime(LocalDateTime startTime) {
        return (root, query, criteriaBuilder) -> {
            if (startTime == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("starTime"), startTime);
        };
    }

    public static Specification<AskForLeaveBean> filterByEndTime(LocalDateTime endTime) {
        return (root, query, criteriaBuilder) -> {
            if (endTime == null) {
                return criteriaBuilder.conjunction(); 
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("endTime"), endTime); 
        };
    }

    public static Specification<AskForLeaveBean> filterByLeaveCategory(String leaveCategory) {
        return (root, query, criteriaBuilder) -> {
            if (leaveCategory == null || leaveCategory.isEmpty()) {
                return criteriaBuilder.conjunction(); 
            }
            return criteriaBuilder.like(root.get("leaveCategory").get("leaveType"), "%" + leaveCategory + "%");
        };
    }

    public static Specification<AskForLeaveBean> filterByApprovedStatus(String approvedStatus) {
        return (root, query, criteriaBuilder) -> {
            if (approvedStatus == null || approvedStatus.isEmpty()) {
                return criteriaBuilder.conjunction(); 
            }
            return criteriaBuilder.like(root.get("approvedStatus"), "%" + approvedStatus + "%");
        };
    }
}

package marketMaster.controller.askForLeave;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import marketMaster.bean.askForLeave.LeaveRecordBean;
import marketMaster.bean.employee.EmpBean;
import marketMaster.service.askForLeave.LeaveRecordService;
import marketMaster.service.employee.EmployeeServiceImpl;

@Controller
public class LeaveRecordController {

	@Autowired
	private LeaveRecordService leaveRecordService;
	
	@Autowired
	private EmployeeServiceImpl employeeServiceImpl;
	
	@GetMapping("/askForLeave/findLeaveByEmpId")
	public String getLeaveRecordsByEmployeeId(
	        @RequestParam String employeeId, Model model) {
	    List<LeaveRecordBean> leaveRecords = leaveRecordService.findLeaveRecordsByEmployeeIdWithinDateRange(employeeId);
	   
	    
	    EmpBean employee = employeeServiceImpl.getEmployee(employeeId);
	    String employeeName = employee.getEmployeeName();
	    model.addAttribute("employeeId", employeeId);
	    model.addAttribute("employeeName", employeeName);
	    model.addAttribute("leaveRecords", leaveRecords);
	    
	    return "askForLeave/leave";
	}

}

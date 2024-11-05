package marketMaster.service.askForLeave;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import marketMaster.bean.askForLeave.AskForLeaveBean;
import marketMaster.bean.askForLeave.LeaveCategoryBean;
import marketMaster.bean.askForLeave.LeaveRecordBean;
import marketMaster.bean.employee.EmpBean;
import marketMaster.service.employee.EmployeeRepository;

@Service
public class LeaveRecordService {

	@Autowired
	private LeaveRecordRepository leaveRecordRepo;

	@Autowired
	private LeaveCategoryRepository leaveCategoryRepo;

	@Autowired
	private EmployeeRepository empRepo;

	@Autowired
	private AskForLeaveRepository askForLeaveRepo;

	public LeaveRecordBean checkLeaveRecord(String employeeId, Integer categoryId, LocalDateTime endtime) {
		LocalDate endDate = endtime.toLocalDate();
		LocalDate expirationDate = endDate.plusYears(1);
		Optional<LeaveRecordBean> existingRecordOptional = leaveRecordRepo
				.findByEmpBean_EmployeeIdAndLeaveCategory_CategoryIdAndExpirationDateBetween(employeeId, categoryId,
						endDate, expirationDate);
		if (existingRecordOptional.isPresent()) {
			LeaveRecordBean existingRecord = existingRecordOptional.get();
			return existingRecord;
		} else {
			LeaveRecordBean newLeaveRecord = new LeaveRecordBean();
			EmpBean emp = new EmpBean();
			emp.setEmployeeId(employeeId);
			newLeaveRecord.setEmpBean(emp);
			LeaveCategoryBean category = leaveCategoryRepo.findById(categoryId)
					.orElseThrow(() -> new RuntimeException("請假類別未找到"));
			newLeaveRecord.setLeaveCategory(category);
			Optional<EmpBean> empBean = empRepo.findByEmployeeId(employeeId);
			LocalDate selectDate = empBean.get().getHiredate();
			LocalDate nowDate = LocalDate.now();
			int date = nowDate.getYear() - selectDate.getYear();
			LocalDate plusDate = selectDate.plusYears(date + 1);
			newLeaveRecord.setExpirationDate(plusDate);
			newLeaveRecord.setActualHours(0);
			if (categoryId == 3) {
				newLeaveRecord.setLimitHours(24);
			} else {
				newLeaveRecord.setLimitHours(category.getMaxHours());
			}

			return leaveRecordRepo.save(newLeaveRecord);
		}
	}

	public LeaveRecordBean addLeaveHours(String employeeId, Integer categoryId, LocalDateTime endtime,
			int totalScheduleHours) {

		LocalDate localDate = endtime.toLocalDate();
		LocalDate expirationDate = localDate.plusYears(1);
		LeaveRecordBean existingRecord = leaveRecordRepo
				.findByEmpBean_EmployeeIdAndLeaveCategory_CategoryIdAndExpirationDateBetween(employeeId, categoryId,
						localDate, expirationDate)
				.orElseThrow(() -> new RuntimeException("找不到現有的請假紀錄"));

		existingRecord.setActualHours(existingRecord.getActualHours() + totalScheduleHours);

		return leaveRecordRepo.save(existingRecord);
	}

	public LeaveRecordBean minusLeaveHours(String leaveId, String employeeId, Integer categoryId, LocalDateTime endtime,
			int totalScheduleHours) {

		LocalDate localDate = endtime.toLocalDate();
		LocalDate expirationDate = localDate.plusYears(1);

		LeaveRecordBean existingRecord = leaveRecordRepo
				.findByEmpBean_EmployeeIdAndLeaveCategory_CategoryIdAndExpirationDateBetween(employeeId, categoryId,
						localDate, expirationDate)
				.orElseThrow(() -> new RuntimeException("找不到現有的請假紀錄"));

		Optional<AskForLeaveBean> asl = askForLeaveRepo.findById(leaveId);
		String approvedStatus = asl.get().getApprovedStatus();
		if ("待審核".equals(approvedStatus)) {
			return leaveRecordRepo.save(existingRecord);
		}
		existingRecord.setActualHours(existingRecord.getActualHours() - totalScheduleHours);

		return leaveRecordRepo.save(existingRecord);

	}

	public List<LeaveRecordBean> findLeaveRecordsByEmployeeIdWithinDateRange(String employeeId) {
		LocalDate currentDate = LocalDate.now();
		LocalDate oneYearFromNow = currentDate.plusYears(1);

		List<LeaveRecordBean> leaveRecords = leaveRecordRepo
				.findByEmpBean_EmployeeIdAndExpirationDateBetween(employeeId, currentDate, oneYearFromNow);

		for (LeaveRecordBean record : leaveRecords) {

			Integer limitHours = record.getLimitHours();
			if (limitHours == null || limitHours == 0) {
				LeaveCategoryBean category = record.getLeaveCategory();
				if (category != null) {
					record.setLimitHours(category.getMaxHours());
				} else {
					record.setLimitHours(0);
				}
			}
		}
		return leaveRecords;
	}

}

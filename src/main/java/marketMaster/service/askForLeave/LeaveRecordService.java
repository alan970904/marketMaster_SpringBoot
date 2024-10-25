package marketMaster.service.askForLeave;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	public LeaveRecordBean checkLeaveRecord(String employeeId, Integer categoryId, LocalDateTime endtime) {

		LocalDate localDate = endtime.toLocalDate();
		LocalDate expirationDate = localDate.plusYears(1);

		Optional<LeaveRecordBean> existingRecordOptional = leaveRecordRepo
				.findByEmpBean_EmployeeIdAndLeaveCategory_CategoryIdAndExpirationDateBetween(employeeId, categoryId,
						localDate, expirationDate);
		LocalDate selectDate = existingRecordOptional.get().getExpirationDate();
		LocalDate plusYears = selectDate.plusYears(1);
		if (existingRecordOptional.isPresent()) {
			return existingRecordOptional.get();
		} else {
			LeaveRecordBean newLeaveRecord = new LeaveRecordBean();
			EmpBean emp = new EmpBean();
			emp.setEmployeeId(employeeId);
			newLeaveRecord.setEmpBean(emp);

			LeaveCategoryBean category = leaveCategoryRepo.findById(categoryId)
					.orElseThrow(() -> new RuntimeException("請假類別未找到"));
			newLeaveRecord.setLeaveCategory(category);

			newLeaveRecord.setExpirationDate(plusYears);
			newLeaveRecord.setLimitHours(24); 

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

	public LeaveRecordBean minusLeaveHours(String employeeId, Integer categoryId, LocalDateTime endtime,
			int totalScheduleHours) {

		LocalDate localDate = endtime.toLocalDate();
		LocalDate expirationDate = localDate.plusYears(1);

		LeaveRecordBean existingRecord = leaveRecordRepo
				.findByEmpBean_EmployeeIdAndLeaveCategory_CategoryIdAndExpirationDateBetween(employeeId, categoryId,
						localDate, expirationDate)
				.orElseThrow(() -> new RuntimeException("找不到現有的請假紀錄"));

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

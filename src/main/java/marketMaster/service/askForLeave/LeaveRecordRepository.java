package marketMaster.service.askForLeave;


import org.springframework.data.jpa.repository.JpaRepository;

import marketMaster.bean.askForLeave.LeaveRecord;

public interface LeaveRecordRepository extends JpaRepository<LeaveRecord, Integer> {

}

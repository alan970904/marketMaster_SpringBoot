package marketMaster.DTO.employee;

// 用於員工圖表，封裝所需數據
public class MonthlyStatisticsDTO {

	private int month;
	private long activeCount; // COUNT函數返回 long 類型
	private long resignedCount;
	
	public MonthlyStatisticsDTO() {
	}

	public MonthlyStatisticsDTO(int month, long activeCount, long resignedCount) {
		this.month = month;
		this.activeCount = activeCount;
		this.resignedCount = resignedCount;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public long getActiveCount() {
		return activeCount;
	}

	public void setActiveCount(int activeCount) {
		this.activeCount = activeCount;
	}

	public long getResignedCount() {
		return resignedCount;
	}

	public void setResignedCount(int resignedCount) {
		this.resignedCount = resignedCount;
	}
	
}

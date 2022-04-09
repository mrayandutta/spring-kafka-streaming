package kafkastreaming.model.employee;

public class Employee {
	private String name;
	private String department;
	private String salary;
	
	
	private int retryCount;
	private int retryThreshold;
	private int retryOn;
	
	private Employee() {}
	
	public Employee(String name, String department, String salary) {
		super();
		this.name = name;
		this.department = department;
		this.salary = salary;
		this.retryCount = 0;
		this.retryThreshold = 0;
		this.retryOn = 0;
	}
	
	
	public Employee(String name, String department, String salary, int retryCount, int retryThreshold, int retryOn) {
		super();
		this.name = name;
		this.department = department;
		this.salary = salary;
		this.retryCount = retryCount;
		this.retryThreshold = retryThreshold;
		this.retryOn = retryOn;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getSalary() {
		return salary;
	}
	public void setSalary(String salary) {
		this.salary = salary;
	}
	
	public int getRetryCount() {
		return retryCount;
	}
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
	public int getRetryThreshold() {
		return retryThreshold;
	}
	public void setRetryThreshold(int retryThreshold) {
		this.retryThreshold = retryThreshold;
	}
	public int getRetryOn() {
		return retryOn;
	}
	public void setRetryOn(int retryOn) {
		this.retryOn = retryOn;
	}
	@Override
	public String toString() {
		return "Employee [name=" + name + ", department=" + department + ", salary=" + salary + ", retryCount="
				+ retryCount + ", retryThreshold=" + retryThreshold + ", retryOn=" + retryOn + "]";
	}
	
}

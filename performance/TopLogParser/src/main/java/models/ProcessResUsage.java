package models;

public class ProcessResUsage {

	private String time;
	private float cpuUsage;
	private float memoryUsage;
	private float res;
	private float virtualSpace;

	public ProcessResUsage() {

	}

	public String getTime() {
		return time;
	}

	public float getRes() {
		return res;
	}

	public void setRes(float res) {
		this.res = res;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public float getCpuUsage() {
		return cpuUsage;
	}

	public void setCpuUsage(float cpuUsage) {
		this.cpuUsage = cpuUsage;
	}

	public float getMemoryUsage() {
		return memoryUsage;
	}

	public void setMemoryUsage(float memoryUsage) {
		this.memoryUsage = memoryUsage;
	}

	public float getVirtualSpace() {
		return virtualSpace;
	}

	public void setVirtualSpace(float virtualSpace) {
		this.virtualSpace = virtualSpace;
	}

}

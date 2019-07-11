/**
 * 
 */
package models;

/**
 * @author M1030608
 *
 */
public class SystemMemory {

	private String time;
	private Float total;
	private Float free;
	private Float used;
	private Float buffer;

	/**
	 * 
	 */
	public SystemMemory() {

	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Float getTotal() {
		return total;
	}

	public void setTotal(Float total) {
		this.total = total;
	}

	public Float getFree() {
		return free;
	}

	public void setFree(Float free) {
		this.free = free;
	}

	public Float getUsed() {
		return used;
	}

	public void setUsed(Float used) {
		this.used = used;
	}

	public Float getBuffer() {
		return buffer;
	}

	public void setBuffer(Float buffer) {
		this.buffer = buffer;
	}

}

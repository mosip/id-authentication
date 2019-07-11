package models;

public class SystemCPU {

	private String time;
	private float user;
	private float system;

	public SystemCPU() {

	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public float getUser() {
		return user;
	}

	public void setUser(float user) {
		this.user = user;
	}

	public float getSystem() {
		return system;
	}

	public void setSystem(float system) {
		this.system = system;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(system);
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + Float.floatToIntBits(user);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SystemCPU other = (SystemCPU) obj;
		if (Float.floatToIntBits(system) != Float.floatToIntBits(other.system))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (Float.floatToIntBits(user) != Float.floatToIntBits(other.user))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SystemCPU [time=" + time + ", user=" + user + ", system=" + system + "]";
	}

}

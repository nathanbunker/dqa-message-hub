package org.immregistries.dqa.hub.persistence;

import java.util.Date;

public class MessageCounts {
	private Date day;
	private long count;
	/**
	 * @return the day
	 */
	public Date getDay() {
		return day;
	}
	/**
	 * @param day the day to set
	 */
	public void setDay(Date day) {
		this.day = day;
	}
	/**
	 * @return the count
	 */
	public long getCount() {
		return count;
	}
	/**
	 * @param count the count to set
	 */
	public void setCount(long count) {
		this.count = count;
	}
	
}

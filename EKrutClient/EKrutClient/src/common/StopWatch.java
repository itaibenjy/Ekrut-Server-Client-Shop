package common;


/**
 * This class is a simple stop watch , holds a start time and calculate time passed 
 * in milliseconds, seconds, minutes;
 */
public class StopWatch {
	
	long start;
	long end;
	public StopWatch() {
	}
	
	/**
	 * This method "start" the stop watch by holding the time this method was called in start
	 */
	public void start() {
		start = System.currentTimeMillis();
	}
	
	/**
	 * This method calculate the time passed from start attribute to the time this method is called
	 * in milliseconds
	 * @return the time passed in milliseconds
	 */
	public long getTimeMillis() {
		end = System.currentTimeMillis();
		return end - start;
	}
	
	/**
	 * This method calculate the time passed from start attribute to the time this method is called
	 * in seconds
	 * @return the time passed in seconds
	 */
	public long getTimeSeconds() {
		return  getTimeMillis() / 1000;
	}
	
	/**
	 * This method calculate the time passed from start attribute to the time this method is called
	 * in minutes
	 * @return the time passed in minutes
	 */
	public long getTimeMinuts() {
		return  getTimeSeconds() / 60;
	}
	
	/**
	 *  This method reset the timer by simply calling start again
	 */
	public void reset() {
		start();
	}
	
}

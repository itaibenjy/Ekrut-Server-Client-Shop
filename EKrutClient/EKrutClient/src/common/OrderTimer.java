package common;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class uses the timer object and an instance from the StopWatch class to run a 
 * the runnable RefreshTimer every second in a thread to be able to keep a timer running 
 * without effecting the UI responsiveness, also this class hold an array of observes that
 * way it is used as an observable and update the observers of the current time every second
 */
public class OrderTimer {
	ArrayList<Observer> observers = new  ArrayList<>(); 
	private Timer timer;
	private volatile String orderTimeLeft;
	private StopWatch stopWatch;
	private RefreshTimer refreshTimer;
	
	
	/**
	 * This class extend TimerTask to be able to run from the Timer
	 * its run method calculate the time left and update the observes
	 */
	public class RefreshTimer extends TimerTask{
		
		public void run() {
			calculateOrderTimeLeft();
			notifyObservers();
		}

	}
	
	// Constructor
	
	/**
	 *  This constructor initiate the attributes and call the start method
	 */
	public OrderTimer() {
		timer = new Timer();
		stopWatch = new StopWatch();
		start();
	}
	
	// Methods
	
	/**
	 * This method start the StopWath and schedule the timer to run every second
	 */
	public void start() {
		stopWatch.start();
		refreshTimer = new RefreshTimer();
		timer.schedule(refreshTimer, 1000, 1000);
	}
	
	/**
	 * This method calculate the time left in the correct format to display and saves it in orderTimerLeft
	 */
	private void calculateOrderTimeLeft() {
		orderTimeLeft = String.format("%02d:%02d",(0 - stopWatch.getTimeMinuts()),(59-stopWatch.getTimeSeconds()%60));
	}
	
	// getter
	public String getOrderTimeLeft() {
		return orderTimeLeft;
	}
	
	/**
	 * This method adds an observer to the observers list to be notify norifyObservers called
	 * @param observer the observer to be notify
	 */
	public void add(Observer observer) {
		observers.add(observer);
	}
	
	/**
	 * This method removes an observer from the observes list 
	 * @param observer the observer to remove
	 */
	public void remove(Observer observer) {
		observers.remove(observer);
	}
	
	
	/**
	 * This method cancel and clear the timer
	 */
	public void stop() {
		timer.cancel();
		timer.purge();
	}
	
	/**
	 * This method notify all observers of the orderTimerLeft 
	 */
	public void notifyObservers() {
		for(Observer o: observers) {
			o.update(orderTimeLeft);
		}
	}
	
	
}

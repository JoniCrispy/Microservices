package bgu.spl.mics.application.services;
import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CloseAllBrodcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.GPU;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
    private int TickTime;      //The time we get from json file, every ticktime its 1 tick.
	private int timePassed;    //The ticks that passed until the current time.
	private int duration;	   //The end time of the program.

	public TimeService(int TickTimeFromMain,int durationFromMain) {
		super("timeService");
		TickTime = TickTimeFromMain;
		duration = durationFromMain;
		timePassed = 0;
	}



	@Override
	protected void initialize() { //This method will count the passed time.
		Timer timer = new Timer(); //Timer class.
		TimerTask updateTime = new TimerTask(){ //TimerTask its the task its will do every specified time.
			public void run(){
				timePassed ++;
				if (timePassed >= duration){   //If time passed equals the time limit we want to end the program.
					System.out.println("the duration is over");
					CloseAllBrodcast closeAllBrodcast = new CloseAllBrodcast();
					sendBroadcast(closeAllBrodcast);
					timer.cancel();
				}
				//We need to send Broadcast.
				else {
					timePassed ++;
					TickBroadcast tickToCast = new TickBroadcast();
					sendBroadcast(tickToCast);
				}
			}
		};
		timer.schedule(updateTime,0,TickTime);  //thats will run the updateTime.
		this.terminate();
	}


}

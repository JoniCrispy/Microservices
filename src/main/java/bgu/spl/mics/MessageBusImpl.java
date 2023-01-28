package bgu.spl.mics;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl extends MessageBus {

	private static MessageBusImpl instance = null;
//	private ConcurrentHashMap<Class<? extends Message>, LinkedBlockingQueue<MicroService>> SubscribedMap;
	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> MicroServiceMessageQueues;
	private ConcurrentHashMap <Class<? extends Broadcast> , LinkedBlockingQueue <MicroService>> broadCastMap;
	private ConcurrentHashMap<Class<? extends Event> , LinkedBlockingQueue<MicroService>> eventMap;
	private HashMap<Event,Future> ResultMap;
	/**
	 *
	 * @param type The type to subscribe to,
	 * @param m    The subscribing micro-service.
	 * @param <T>
	 * @pre
	 */

	private MessageBusImpl(){
//		SubscribedMap = new ConcurrentHashMap<Class<? extends Message>,LinkedBlockingQueue<MicroService>>();
		MicroServiceMessageQueues = new ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>>();
		ResultMap = new HashMap<Event,Future>();
		eventMap = new ConcurrentHashMap<>();
		broadCastMap = new ConcurrentHashMap<>();

	}
	public synchronized  static MessageBusImpl getMessageBusImlInstance() {return messageBusHolder.instance;}

	public static class messageBusHolder {
		private static volatile MessageBusImpl instance = new MessageBusImpl();
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if(!isRegister(m)){
			throw new IllegalStateException(m.getName()+" is not register");
		}
		if(eventMap.containsKey(type)&&eventMap.get(type).contains(m)){
			throw new IllegalStateException(m.getName()+" is already subscribed");
		}
		if(!eventMap.containsKey(type)){
			eventMap.putIfAbsent(type,new LinkedBlockingQueue<MicroService>());

		}
		eventMap.get(type).add(m);
	}

	@Override
	public synchronized void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if(!isRegister(m)){
			throw new IllegalStateException(m.getName()+" is not register");
		}
		if(broadCastMap.containsKey(type)&&broadCastMap.get(type).contains(m)){
			throw new IllegalStateException(m.getName()+" is already subscribed");
		}
		if(!broadCastMap.containsKey(type)){
			broadCastMap.putIfAbsent(type,new LinkedBlockingQueue<MicroService>());
		}
		broadCastMap.get(type).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		if(ResultMap.get(e)!=null){
			ResultMap.get(e).resolve(result);
	}}

	@Override
	public void sendBroadcast(Broadcast b) {
		Class broadcastType = b.getClass();
		if(broadCastMap.containsKey(broadcastType)){
			for(MicroService m : broadCastMap.get(broadcastType)){
				MicroServiceMessageQueues.get(m).add(b);
			}
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		if(ResultMap.containsKey(e.getClass())) {
			throw new IllegalArgumentException("messageBus already contains such an event");
		}
		LinkedBlockingQueue<MicroService> MicroServiceEventQueue = eventMap.get(e.getClass());
		Future<T> f = new Future<>();
		ResultMap.put(e,f);
		if (!eventMap.containsKey(e.getClass()))
			throw new IllegalStateException("No service registered for handle such an event!"); //maybe return null
		if(eventMap.contains(e))
			throw new IllegalStateException("eventMap already contain that event");
		synchronized (MicroServiceEventQueue) {
			if(MicroServiceEventQueue.isEmpty())
				throw new IllegalStateException("No service registered for handle such an event!"); //maybe return null
			boolean eventSent = false;
			while (!eventSent) {
				try {
					MicroService m = MicroServiceEventQueue.poll(); //pops the first microService in the queue
					MicroServiceMessageQueues.get(m).add(e); //waking up the thread if necessary
					MicroServiceEventQueue.add(m); //returns the microService to the end of the queue
					eventSent = true;
				}
				catch (NullPointerException ex) { }
			}
		}
		return f;
	}




	@Override
	public void register(MicroService m) {
		if(isRegister(m)){
			throw new IllegalStateException(m.getName()+" is not register");
		}
		MicroServiceMessageQueues.put(m,new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		if(isRegister(m)){
			MicroServiceMessageQueues.remove(m);
			for(Class<? extends Message> key: broadCastMap.keySet()){
				broadCastMap.get(key).remove(m);}
			for(Class<? extends Message> key: eventMap.keySet()){
				eventMap.get(key).remove(m);}

		}
	}

	@Override
	public  Message awaitMessage(MicroService m) throws InterruptedException {

		if(!isRegister(m)){
			throw new IllegalStateException(m.getName()+" is not registered");
		}
		return MicroServiceMessageQueues.get(m).take();
	}

	public boolean isRegister(MicroService m){
		return MicroServiceMessageQueues.containsKey(m);
	}
}

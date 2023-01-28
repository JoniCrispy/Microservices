package bgu.spl.mics.application.objects;


import bgu.spl.mics.MessageBusImpl;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
	private LinkedList<GPU> listOfGPU;
	public int i ;
	private LinkedList<CPU> listOfCPU;
	private LinkedBlockingQueue<DataBatch> UnprocessedDB;
	private static Cluster instance = null;


	/**
     * Retrieves the single instance of this class.
     */
	public Cluster(){
		listOfGPU = new LinkedList<GPU>();
		listOfCPU = new LinkedList<CPU>();
		UnprocessedDB = new LinkedBlockingQueue<DataBatch>();
		i = 0;

	}

	public synchronized  static Cluster getClusterInstance() {return Cluster.ClusterHolder.instance;}
	public static class ClusterHolder {
		private static volatile Cluster instance = new Cluster();
	}

	public int numOfTicks(){
		int sum = 0;
		for (CPU cpu : listOfCPU){
			sum += cpu.getTicksOfCPU();
		}
		return sum;
	}

	public int numOfDBs(){
		int sum = 0;
		for (CPU cpu : listOfCPU){
			sum += cpu.getSumOfProcssedDataBatch();
		}
		return sum;
	}
	public int numOfTicksInGPU(){
		int sum = 0;
		for (GPU gpu : listOfGPU){
			sum += gpu.getTrainingTicks();
		}
		return sum;
	}

	public synchronized LinkedList<GPU> getGPUlist (){
		return listOfGPU;
	}
	public LinkedBlockingQueue<DataBatch> getUnprocessedDB() {
		return UnprocessedDB;
	}
	public synchronized void sendUnProcssedDB(DataBatch DB){  //int GPUID
//		System.out.println("data batch arrived to the cluster ");
//		if(listOfCPU.size()>0){
//			CPU cpu = listOfCPU.pop();
//			cpu.getUnProcssedDB().add(DB);
//			listOfCPU.add(cpu);
//		}
		UnprocessedDB.add(DB);

	}

	public synchronized LinkedList<CPU> getListOfCPU() {
		return listOfCPU;
	}
}

package bgu.spl.mics.application.objects;

import java.util.Collection;
import java.util.LinkedList;
import bgu.spl.mics.application.objects.Cluster;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    private int numberOfCore;
    private static int time =0 ;
    private int originalTime;
    private DataBatch currentunProcssedDB;
    private Cluster cluster;
    public enum Status {Available,unAvailable};
    private Status status ;
    private int TicksOfCPU;
    private int sumOfProcssedDataBatch;
    public CPU (int coresNum){
        numberOfCore = coresNum;
        cluster = Cluster.getClusterInstance();
        status = Status.Available;
        TicksOfCPU = 0;
        sumOfProcssedDataBatch = 0;
    }

    @Override
    public String toString() {
        return "CPU{" +
                "numberOfCore=" + numberOfCore +
                '}';
    }

    public void Tick(){
        time +=1;
    }

    /** @pre
     * @post the Data Batch that been sent from the cluster added to the unProcessed list
     */
    public int TickTime(int numberOfCore, Data.Type type){
        if(type == Data.Type.Images){
            return ((32/numberOfCore)*4);
        }
        else if(type == Data.Type.Tabular){
            return ((32/numberOfCore)*2);
        }
        else
            return (32/numberOfCore);
    }

    public void ProcessDB(){
        int tickTime = TickTime(numberOfCore,currentunProcssedDB.getType());
        TicksOfCPU += 1;
        if (time >= originalTime + tickTime){
            sumOfProcssedDataBatch++;
            currentunProcssedDB.setStatus(DataBatch.Status.Procssed);
            currentunProcssedDB.getFatherGPU().getProcssedDB().add(currentunProcssedDB);
            currentunProcssedDB = null;
        }
    }

    public DataBatch getcurrentUnProcssedDB() {
        return currentunProcssedDB;
    }
    public int getTicksOfCPU() {
        return TicksOfCPU;
    }

    public int getSumOfProcssedDataBatch() {
        return sumOfProcssedDataBatch;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCurrentunProcssedDB(DataBatch currentunProcssedDB) {
        this.currentunProcssedDB = currentunProcssedDB;
        originalTime = time;

    }
/** @pre getting unprocessed Data Batch
     * @post sending to the Cluster as a processed Data Batch
     */




}

package bgu.spl.mics.application.objects;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;
import bgu.spl.mics.application.objects.Cluster;
/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    private static int id = 0;
    public enum Type {RTX3090, RTX2080, GTX1080};
    private Model model;
    private Type type;
    private Cluster cluster = Cluster.getClusterInstance();
    private LinkedBlockingDeque<DataBatch> unProcssedDB;
    private LinkedBlockingDeque<DataBatch> ProcssedDB;
    private int time = 0;
    private int originalTime;
    private int VRAMsize;
    private static int ID ;
    private int GPUid;
    private int avaiablePlace;
    private  int tickNumber;
    private int amountToTrain;
    private int dataSize;
    private int finishtime;
    private DataBatch currentDataBatch;
    private boolean isItTraining ;
    public int TrainingTicks;
    public GPU (Type typeOfGpu, int id){
      if(typeOfGpu==Type.GTX1080){
          type = Type.GTX1080;
          VRAMsize = 8;
          tickNumber = 4;
      } else if(typeOfGpu==Type.RTX2080){
          type = Type.RTX2080;
          VRAMsize = 16;
          tickNumber = 2;
      }
      else{
          type = Type.RTX3090;
          VRAMsize = 32;
          tickNumber = 1;
      }
        ID = id;
        cluster = Cluster.getClusterInstance();
        avaiablePlace = VRAMsize;
        isItTraining = false;
        TrainingTicks = 0;
    }

    /** @pre
     * @post divide the data into Data Batches which each of them represent amount of 1000 data
     */
    public void Divide (Data dataToDivide){
        dataToDivide.toString();
        for(int i =0;i<dataToDivide.getSize();i+=1000){
            unProcssedDB.add(new DataBatch(dataToDivide,i,this));
        }
        currentDataBatch = unProcssedDB.peek();
        amountToTrain = unProcssedDB.size();      //amount of DataBatch to train
    }

    public void setModel(Model model) {
        this.model = model;
        this.dataSize = model.getData().getSize();
        unProcssedDB = new LinkedBlockingDeque<>();
        ProcssedDB = new LinkedBlockingDeque<>();
        }

    public LinkedBlockingDeque<DataBatch> getProcssedDB() {
        return ProcssedDB;
    }

    /** @pre getting a processed status databatch
     * @post "train" the model using the databatch
     */
    public void trainDB(){
        if(ProcssedDB.size()!=0){
           model.setModelStatus(Model.status.Training);
            currentDataBatch = getProcssedDB().pop();
            if (model.getModelStatus()== Model.status.Trained) {
                finishtime = time + tickNumber;
            }
            TrainingTicks += tickNumber;
            isItTraining = true;
            model.setModelStatus(Model.status.Training);
        }
    }

    public int getTickNumber() {
        return tickNumber;
    }



    public boolean itIsTrained(){
        isItTraining = false;
        avaiablePlace++;

        amountToTrain--;
        if(amountToTrain==0&unProcssedDB.size()==0){
            return true;}
        else{
            return false;}
    }

    public int getTrainingTicks() {
        return TrainingTicks;
    }

    public void setTrainingTicks(int trainingTicks) {
        TrainingTicks = trainingTicks;
    }

    public int getFinishtime() {
        return finishtime;
    }

    public boolean isItTraining() {
        return isItTraining;
    }

    public DataBatch getCurrentDataBatch() {
        return currentDataBatch;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public synchronized  void sendUnProcssedDB(){   //I deleted int GPUID
        while(avaiablePlace!=0&unProcssedDB.size()>0){
            DataBatch DB = unProcssedDB.pop();
            avaiablePlace--;
            cluster.sendUnProcssedDB(DB);  //I changed GPUID to this.
        }
    }

    @Override
    public String toString() {
        return "GPU{" +
                "type=" + type +
                '}';
    }

    public static int getId() {
        return id;
    }

    public int getProcssedDBsize (){
        return ProcssedDB.size();
    }

    public int getUnProcssedDBsize (){
        return unProcssedDB.size();
    }

    public Model getModel(){ return model;}

    public LinkedBlockingDeque<DataBatch> getUnProcssedDB (){
        return unProcssedDB;
    }

    public LinkedBlockingDeque<DataBatch> getProcssedDBlist (){
        return ProcssedDB;
    }

    /** @pre the GPU unprocssed data is in the Cluster
     * @inv the GPU is available to get procssed data
     * @post the GPU gets a unprocssed Data Batch from the Cluster
     */
    public void getProcssedDB(DataBatch DB){}

    public boolean getisItTraining(){
        return isItTraining;
    }

    /** @pre
     * @post adding the GPU data into the list of GPUs the Cluster holding
     */
    public void signToCluster(){}


}

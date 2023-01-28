package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    public enum Status {UnProcessed, Procssed, Trained}
    private Status status;
    private int index;
    private GPU FatherGPU;
    private Data data;
    private Data.Type type;
    public DataBatch (Data d,int i,GPU FatherGPU){
        index = i;
        status=Status.UnProcessed;
        data = d;
        this.FatherGPU = FatherGPU;
        type = d.getType();
    }

    public int getIndex() {
        return index;
    }

    public Data GetData (){
        return data;}

    public Status getStatus() {
        return status;
    }

    public Data.Type getType() {
        return type;
    }

    public void setStatus(Status newStatus){
        status = newStatus;
    }

    public GPU getFatherGPU() {
        return FatherGPU;
    }
}

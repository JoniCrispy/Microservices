package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
    private String name ;
    private Data data ;
    private Student student;
    public enum status {PreTrained,Training,Trained,Tested};
    public enum result {None,Good,Bad};
    status modelStatus;
    result modelResult;

    public Model(String mName, Data mData, Student mStudent){
        name = mName;
        data = mData;
        student = mStudent;
        modelStatus = status.PreTrained;
        modelResult = result.None;
    }

    public String getName() {
        return name;
    }

    public status getModelStatus() {
        return modelStatus;
    }

    public Student getStudent() {
        return student;
    }

    public result getModelResult() {
        return modelResult;
    }

    public void setModelStatus(status modelStatus) {
        this.modelStatus = modelStatus;
    }

    public void setModelResult(result modelResult) {
        this.modelResult = modelResult;
    }

    public Data getData(){
        return data;
    }
}

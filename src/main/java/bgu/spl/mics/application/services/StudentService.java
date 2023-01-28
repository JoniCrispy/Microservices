package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {

    private Student student;
    private ArrayList<TrainModelEvent> trainModelEvents;
    private ArrayList<TestModelEvent> testModelEvents;
    private ArrayList<PublishResultsEvent> publishResultsEvents;
    private enum result {None,Good,Bad};
    private boolean terminated;

    public StudentService(String name,Student student) {
        super(name);
        this.student = student;
        terminated = false;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(PublishConferenceBroadcast.class, (message) -> {
            LinkedBlockingQueue<Model> goodModels = message.getGoodModels();   //This lambda experssion update the paper read counter.
            for (Model m : goodModels) {
                if (m.getStudent().getName() != this.student.getName())
                    this.student.addPaperRead();
            }
        });

        subscribeBroadcast(CloseAllBrodcast.class, c -> {
            if(student.getFuture()!=null){
                student.getFuture().resolve(null);
            }
            this.terminate();
        });

        Thread thread = new Thread(){
            public void run() {
                ArrayList<Model> models = student.getModels();
                //Insert this for to thread and let it run, use future to know when to stop it(fet in future doing .wait() use it.
                for (Model m : models) {
                    TrainModelEvent trainModelEvent = new TrainModelEvent(m);                                       //sending model event to train
                    Future<TrainModelEvent> trainedModel = sendEvent(trainModelEvent);
                    if(trainedModel!=null){
                        student.setFuture(trainedModel);
                    }
                    trainedModel.get();
                    TestModelEvent testModelEvent = new TestModelEvent(m);                                        //sending model event to test
                    Future<TrainModelEvent> testModel = sendEvent(testModelEvent);
                    if(testModel!=null){
                        student.setFuture(testModel);
                    }
                    testModel.get();
                    //sending model event of result
                    if (m.getModelResult() == Model.result.Good) {
                        PublishResultsEvent publishResultsEvent = new PublishResultsEvent(m);
                        Future<PublishResultsEvent> ResultModel = sendEvent(publishResultsEvent);
                        student.getPublications();
                        if(ResultModel!=null){
                            student.setFuture(ResultModel);
                        }
                    }
                }
            }};
        thread.start();
    }
}

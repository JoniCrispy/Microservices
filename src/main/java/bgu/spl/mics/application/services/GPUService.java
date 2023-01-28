package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;
//import com.sun.org.apache.xpath.internal.operations.Mod;


import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the {@link }.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {

    public GPU gpu;
    public TrainModelEvent trainModelEvent;
    private ArrayList<Message> awaitingMessages;
    private boolean isAvailable ;
    public GPUService(String name, GPU gpu) {
        super(name);
        this.gpu = gpu;
        awaitingMessages = new ArrayList<>();
        isAvailable = true;
    }

    @Override
    protected void initialize() {


        subscribeEvent(TrainModelEvent.class, (TrainModelEvent trainM) -> {
            if(isAvailable) {
                isAvailable = false;
                trainModelEvent = trainM;
                Model model = trainM.getModel();
                gpu.setModel(model);
                model.setModelStatus(Model.status.Training);
                Data modelData = model.getData();
                gpu.Divide(modelData);
                gpu.sendUnProcssedDB(); //I changed gpu.getID to nothing.
            }else{
                awaitingMessages.add(trainM);
            }

        });
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tickBroadcastM) -> {
            if(isAvailable&!awaitingMessages.isEmpty()){
                Message message = awaitingMessages.remove(0);
                callbacks.get(message.getClass()).call(message);
            }
            else {
                gpu.setTime(gpu.getTime() + 1);                                                   //updating the time in the gpu
                if (gpu.getModel() != null) {
                    if (!gpu.isItTraining()) {
                        if (gpu.getProcssedDB().size() > 0) {
                            gpu.trainDB();
                        }
                    }
                    gpu.sendUnProcssedDB();
                    if (gpu.getModel().getModelStatus() == Model.status.Training & gpu.getFinishtime() <= gpu.getTime()) {                                       //checking if we finish the training for the DataBatch
                        boolean isFinishedTrain = gpu.itIsTrained();
                        if (!isFinishedTrain) {
                            gpu.getCurrentDataBatch().setStatus(DataBatch.Status.Trained);
                            gpu.trainDB();

                        }
                        else {
                            gpu.getModel().setModelStatus(Model.status.Trained);
                            System.out.println(gpu.getModel().getName() + " is trained");
                            isAvailable = true;
                            this.complete(trainModelEvent, trainModelEvent);
                        }
                    }
                }

            }


          });
        subscribeEvent(TestModelEvent.class, (TestModelEvent testM) -> {
            if(isAvailable) {
                if (gpu.getModel() != null) {
                    Model model = testM.getModel();
                    if (model.getStudent().getDegree() == Student.Degree.MSc) {
                        if (new Random().nextDouble() <= 0.6) {
                            model.setModelResult(Model.result.Good);
                        } else {
                            model.setModelResult(Model.result.Bad);
                        }
                    } else {
                        if (new Random().nextDouble() <= 0.8) {
                            model.setModelResult(Model.result.Good);
                        } else {
                            model.setModelResult(Model.result.Bad);
                        }
                    }
                    this.complete(testM, testM);
                    if (model.getModelResult() == Model.result.Good) {
                        PublishResultsEvent publishResultsEvent = new PublishResultsEvent(model);
                        Future<PublishResultsEvent> ResultModel = sendEvent(publishResultsEvent);
                    }
                }
            else{
                    awaitingMessages.add(testM);
                }
            }

            });
        subscribeBroadcast(CloseAllBrodcast.class, c -> {
            this.terminate();
        });


    }
}
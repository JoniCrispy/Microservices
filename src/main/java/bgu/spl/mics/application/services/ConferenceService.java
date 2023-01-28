package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CloseAllBrodcast;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConfrenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {

//    LinkedBlockingQueue<Model> goodModels  = new LinkedBlockingQueue<>();
    MessageBusImpl messageBus = MessageBusImpl.getMessageBusImlInstance();
    int currTime ;
    int publishTime;
    int tickTime;
    ConfrenceInformation confrenceInformation;
    LinkedBlockingQueue<Model> goodModels;


    //queue of conferences

    public ConferenceService(String name, int publishTime , int tickTime, ConfrenceInformation confrenceInformation) {
        super(name);
        this.publishTime = publishTime;
        currTime = 0;
        this.tickTime = tickTime;
        this.confrenceInformation = confrenceInformation;
        goodModels = confrenceInformation.getGoodModels();
    }
    public void updateTime() {
        //currTime+=tickTime;
        currTime++;
        if (currTime >= publishTime){
            sendBroadcast(new PublishConferenceBroadcast(goodModels));
            goodModels.clear();
            this.terminate();
        }
    }

    @Override
    protected void initialize() {
     //   messageBus.register(this);
        subscribeEvent(PublishResultsEvent.class,c -> {
            goodModels.add(c.getModel());    //adding the good models that we got from the  event.
        });
        subscribeBroadcast(TickBroadcast.class, c->{
             updateTime();
         });
        subscribeBroadcast(CloseAllBrodcast.class,c -> {
            this.terminate();
        });


    }
}

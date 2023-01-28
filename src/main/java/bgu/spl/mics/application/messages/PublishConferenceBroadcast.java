package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class PublishConferenceBroadcast implements Broadcast {
    private LinkedBlockingQueue<Model> goodModels;

    public PublishConferenceBroadcast(LinkedBlockingQueue<Model> goodModels) {
        this.goodModels = goodModels;
    }

    public LinkedBlockingQueue<Model> getGoodModels() {
        return goodModels;
    }
}

package bgu.spl.mics.application.objects;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;
    private int date;
    LinkedBlockingQueue<Model> goodModels;


    public ConfrenceInformation(String Cname,int time){
        name = Cname;
        date = time;
        goodModels  = new LinkedBlockingQueue<>();

    }

    public LinkedBlockingQueue<Model> getGoodModels() {
        return goodModels;
    }

    public String getName() {
        return name;
    }

    public int getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "ConfrenceInformation{" +
                "name='" + name + '\'' +
                ", date=" + date +
                '}';
    }
}

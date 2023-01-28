package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CloseAllBrodcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.DataBatch;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * CPU service is responsible for handling the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {

    CPU cpu;
    public CPUService(String name,CPU cpu) {
       super(name);
       this.cpu = cpu;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class,c -> {

            if (cpu.getcurrentUnProcssedDB() == null) {
                LinkedBlockingQueue<DataBatch> ClusterUnprocessedDB = cpu.getCluster().getUnprocessedDB();
                if (ClusterUnprocessedDB.size() > 0){
                    cpu.setCurrentunProcssedDB(ClusterUnprocessedDB.poll());
                }
            }
            else {
                cpu.ProcessDB();
            }
            cpu.Tick();
        });

        subscribeBroadcast(CloseAllBrodcast.class, c -> {
            this.terminate();

        });
    }
}

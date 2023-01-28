package bgu.spl.mics.application.objects;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CPUTest {
    private CPU cpu;
    @Before
    public void setUp() throws Exception {
       Cluster cluster = Cluster.getClusterInstance();
        cpu = new CPU(10);
    }

    @Test
    public void getcurrentUnProcssedDB() {
        Data data = new Data(Data.Type.Images,1000);
        assertTrue(cpu.getcurrentUnProcssedDB() == null);
        DataBatch DB = new DataBatch(data,0,new GPU(GPU.Type.GTX1080,0));;
        cpu.setCurrentunProcssedDB(DB);
        assertTrue(cpu.getcurrentUnProcssedDB() != null);
    }



    @Test
    public void ProcessDB() {
        Data data = new Data(Data.Type.Images,1000);
        GPU testGPU = new GPU(GPU.Type.GTX1080,0);
        testGPU.setModel(new Model("model", data, new Student("yahav" , "Computer Science",Student.Degree.PhD)));
        DataBatch DB = new DataBatch(data,0,testGPU);
        cpu.setCurrentunProcssedDB(DB);
        assertTrue(DB.getStatus()== DataBatch.Status.UnProcessed);
        for(int i = 0; i < cpu.TickTime(10 , data.getType()); i++){
            cpu.Tick();
            cpu.ProcessDB();
        }
        assertTrue(DB.getStatus() == DataBatch.Status.Procssed); //when DB Processed the cpu get null.
    }




}
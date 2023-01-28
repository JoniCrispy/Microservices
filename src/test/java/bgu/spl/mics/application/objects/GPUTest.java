package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.objects.Data;
import bgu.spl.mics.application.objects.DataBatch;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;
import org.junit.Test;
import org.junit.Before;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GPUTest {
    private Model model;
    private Student student;
    private GPU gpu;
    private Data expData;
    private Cluster cluster;
    @Before
    public void setUp() throws Exception {
        student = new Student("Yahav","CS", Student.Degree.MSc);
        gpu = new GPU(GPU.Type.GTX1080,0);
        expData = new Data(Data.Type.Images,10000);
        model = new Model("studentName",expData,student);
        cluster = Cluster.getClusterInstance();
    }

    @Test
    public void divide() {
        Data data = new Data(Data.Type.Images, 1000);
        gpu.setModel(new Model("model", data, new Student("yahav" , "Computer Science",Student.Degree.PhD)));
        gpu.Divide(data);
        assertTrue(gpu.getUnProcssedDBsize()==data.getSize()/1000);
    }

    @Test
    public void TrainDB(){
        Data data = new Data(Data.Type.Images , 1000);
        DataBatch DB = new DataBatch(data,0,gpu);
        DB.setStatus(DataBatch.Status.Procssed);
        gpu.setModel(new Model("model", data, new Student("yahav" , "Computer Science",Student.Degree.PhD)));
        gpu.getProcssedDB().add(DB);
        assertFalse(this.gpu.getisItTraining());
        this.gpu.trainDB();
        assertTrue(this.gpu.getisItTraining());
    }


    @Test
    public void getProcssedDBsize(){
        gpu.setModel(model);
        int originalSize = gpu.getProcssedDBsize();
        Data data = new Data(Data.Type.Images , 1000);
        DataBatch DB = new DataBatch(data,0,gpu);
        gpu.getProcssedDB().add(DB);
        assertTrue(gpu.getProcssedDBsize() == originalSize+1);
    }

}
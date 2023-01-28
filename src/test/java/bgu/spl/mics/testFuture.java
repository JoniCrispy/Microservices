package bgu.spl.mics;

import org.junit.Test;
import org.junit.Before;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class testFuture {
    private Future<String> future;
    private String resultSTR;
    @Before
    public void setUp() throws Exception {
        future = new Future<String>();
        resultSTR = "result";
    }



    @Test
    public void get() {
        Thread t1 = new Thread(()->{
            future.get();
        });
        t1.start();
        future.resolve("good");
        assertTrue(t1.isAlive());
        assertFalse(resultSTR.equals(future.get()));
        future.resolve(resultSTR);
        assertTrue(resultSTR.equals(future.get()));
    }

    @Test
    public void resolve() {
        assertFalse("the object should not resolved",future.isDone());
        String result= "result";
        future.resolve(result);
        assertTrue(result.equals(future.get()));
    }

    @Test
    public void isDone() {
    assertFalse(future.isDone());
    future.resolve(resultSTR);
    assertTrue(future.isDone());
    }

}

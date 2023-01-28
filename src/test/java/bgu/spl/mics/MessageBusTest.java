package bgu.spl.mics;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.services.StudentService;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class MessageBusTest {
    private  static MessageBusImpl bus;
    private  MicroService service;
    private ExampleEvent event;
    ExampleBroadcast broadcast;


    @Before
    public void setUp() throws Exception{
        service = new StudentService("yahav", new Student("yahav" , "Computer Science",Student.Degree.PhD));
        event = new ExampleEvent("event");
        bus = MessageBusImpl.getMessageBusImlInstance();
        broadcast = new ExampleBroadcast("name");
    }

    @Test
    public void subscribeEventTest() throws Exception{
         try {
             bus.subscribeEvent(event.getClass(), service);
             fail("SubscribeEvent failed");
         }
        catch (Exception e){
             bus.register(service);
             bus.subscribeEvent(event.getClass(),service);
             bus.unregister(service);
         }
    }


    @Test
    public void subscribeBroadcastTest() throws Exception{
        try{
            bus.subscribeBroadcast(broadcast.getClass(),service);
            fail("SubscribeBroadcast failed");
        }catch (Exception e){
            bus.register(service);
            bus.subscribeBroadcast(broadcast.getClass(),service);
            bus.unregister(service);
        }
    }


    @Test
    public void completeTest() throws Exception{
        String str = "someResult";
        try{
            bus.complete(event,str);
        }catch(Exception e){
            bus.register(service);
            bus.subscribeEvent(event.getClass(),service);
            bus.sendEvent(event);
            bus.complete(event,str);
            bus.unregister(service);
        }
    }


    @Test
    public void registerTest() throws Exception{
        bus.register(service);
        try{
            bus.register(service);
            fail("register failed");
        }catch (Exception e){}
        bus.unregister(service);
    }

    @Test
    public void awaitMessegeTest() throws Exception {
        bus.register(service);
        bus.subscribeEvent(event.getClass(), service);
        bus.sendEvent(event);
        Message e = bus.awaitMessage(service);
        assertTrue(e == event);
        bus.unregister(service);
    }



}
package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.services.StudentService;
import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.application.services.CPUService;
import bgu.spl.mics.application.services.ConferenceService;
import bgu.spl.mics.application.services.TimeService;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;

//imports of Json:
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

//imports of File:
import java.io.*;
import java.util.ArrayList;


/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) throws InterruptedException {
        File input = new File(args[0]);

        try {
            //Creating Array of students:
            ArrayList <Student> studentsList = new ArrayList<>();
                JsonElement fileElement = JsonParser.parseReader(new FileReader(input)); /// TODO TRY
                JsonObject fileObject = fileElement.getAsJsonObject();

            //Extracting the basic fields
            int TickTime = fileObject.get("TickTime").getAsInt();
            int Duration = fileObject.get("Duration").getAsInt();
            TimeService timeService = new TimeService(TickTime,Duration);
            Thread ThreadTimeService = new Thread(timeService);
            try {
                ThreadTimeService.sleep(200);
            }catch (Exception e){}
            ThreadTimeService.start();


            //Create the Cpus:
            ArrayList<CPU> CPUs = new ArrayList<>();
            ArrayList<Thread> cpuServicesListOfThreads = new ArrayList<>();          //list of the CPU threads
            JsonArray jsonArrayOfCpus = fileObject.get("CPUS").getAsJsonArray();
            int cpuId = 1;
            for (JsonElement CPUElement : jsonArrayOfCpus){
                CPU cpu = new CPU((CPUElement.getAsInt()));
                CPUs.add(cpu);
                Thread cpuThread = new Thread(new CPUService("CpuService"+cpuId,cpu));
                Cluster.getClusterInstance().getListOfCPU().add(cpu);
                cpuServicesListOfThreads.add(cpuThread);
                cpuThread.start();
                cpuId++;
            }
            //           cpuServicesListOfThreads.get(0).start(); //just a TEST delete and write back 2 lines above.
            //Create the Gpus:
            ArrayList<GPU> GPUs = new ArrayList<>();
            ArrayList<Thread> gpuServicesListOfThreads = new ArrayList<>();         //list of the GPU threads
            JsonArray jsonArrayOfGpus = fileObject.get("GPUS").getAsJsonArray();
            int gpuId = 0;
            for (JsonElement GPUElement : jsonArrayOfGpus){
                GPU.Type gpuType = GPU.Type.valueOf(GPUElement.toString().substring(1 , GPUElement.toString().length() - 1)); //Delete the "" that we got from the json file.
                GPU gpu = new GPU(gpuType,gpuId);
                GPUs.add(gpu);
                Cluster.getClusterInstance().getGPUlist().add(gpu);
                gpuId++;
                Thread gpuThred = new Thread(new GPUService("GpuService"+gpuId,gpu));
                gpuServicesListOfThreads.add(gpuThred);
                gpuThred.start();
            }
            //           gpuServicesListOfThreads.get(0).start(); //just a TEST delete and write back 2 lines above.

            //Create The Conferences:
            ArrayList<ConfrenceInformation> conferenceList = new ArrayList<>();
            ArrayList<Thread> conferenceServicesListOfThreads = new ArrayList<>();          //list of the CPU threads
            JsonArray jsonArrayOfConferences = fileObject.get("Conferences").getAsJsonArray();
            int conferenceId =0;

            for (JsonElement conferenceElement : jsonArrayOfConferences){
                JsonObject conferenceObject = conferenceElement.getAsJsonObject();
                String conferenceName = conferenceObject.get("name").getAsString();
                int conferenceDate = conferenceObject.get("date").getAsInt();
                ConfrenceInformation confrenceInformation = new ConfrenceInformation(conferenceName, conferenceDate);
                conferenceList.add(confrenceInformation);

                Thread conferenceThread = new Thread(new ConferenceService("Conference"+conferenceId,conferenceDate,TickTime,confrenceInformation));
                conferenceThread.start();
                conferenceServicesListOfThreads.add(conferenceThread);
                conferenceId++;
            }

            //Process all students:

            JsonArray jsonArrayOfStudents = fileObject.getAsJsonArray("Students").getAsJsonArray();
            ArrayList<Thread> studentServicesListOfThreads = new ArrayList<>();         //list of the GPU threads
            for (JsonElement StudentElement : jsonArrayOfStudents){
                //Get the JsonStudentObject:
                JsonObject studentObject = StudentElement.getAsJsonObject();

                //Create the students:
                String nameOfStudent = studentObject.get("name").getAsString();
                String departmentOfStudent = studentObject.get("department").getAsString();
                Student.Degree degreeOfStudent = Student.Degree.valueOf(studentObject.get("status").getAsString());
                Student currentStudent = new Student(nameOfStudent, departmentOfStudent, degreeOfStudent);

                //Get the models array for each student:
                JsonArray jsonArrayOfModels = studentObject.getAsJsonArray("models").getAsJsonArray();
                ArrayList <Model> modelsList = new ArrayList<>();
                for (JsonElement modelElement : jsonArrayOfModels){
                    //Get the JsonModelObject:
                    JsonObject modelObject = modelElement.getAsJsonObject();
                    String modelName = modelObject.get("name").getAsString();
                    Data.Type modelType = Data.Type.valueOf(modelObject.get("type").getAsString());
                    int modelSize = modelObject.get("size").getAsInt();
                    Data modelData = new Data(modelType,modelSize);
                    modelsList.add(new Model(modelName, modelData, currentStudent));
                }
                currentStudent.setModels(modelsList);
                Thread studentThread = new Thread(new StudentService("StudentService",currentStudent));
                studentsList.add(currentStudent);
                studentServicesListOfThreads.add(studentThread);
                studentThread.start();
            }



            //joining the theards
            for (Thread thread:gpuServicesListOfThreads){
                thread.join();
            }
            for (Thread thread:cpuServicesListOfThreads){
                thread.join();
            }
            for (Thread thread:conferenceServicesListOfThreads){
                thread.join();
            }
            for (Thread thread:studentServicesListOfThreads){
                thread.join();
            }
            ThreadTimeService.join();
            try{
                BufferedWriter writer = new BufferedWriter(new FileWriter("output.json"));
                System.out.println("file created");
                writer.write("{ \n");
                //print students
                writer.write("\t\"students\": [");
                int i=0;
                for(Student s: studentsList)
                {
                    i++;
                    writer.write(" \n \t\t{");
                    writer.write("\n \t\t\t \"name\": \""+s.getName()+"\",");
                    writer.write("\n \t\t\t \"department\": \""+s.getDepartment()+"\",");
                    writer.write("\n \t\t\t \"status\": \""+s.getDegree()+"\",");
                    writer.write("\n \t\t\t \"publication:\": \""+s.getPublications()+"\",");
                    writer.write("\n \t\t\t \"papersRead:\": \""+s.getPapersRead()+"\",");
                    writer.write("\n \t\t\t \"trainedModels:\": [");
                    int j=0;
                    if(s.getModels().size()==0)
                        writer.write("]");
                    else// case models empty
                    {
                        for(Model m:s.getModels())
                        {
                            j++;
                            writer.write("\n\t\t\t\t{");
                            writer.write("\n\t\t\t\t\t\"name\": \""+m.getName()+"\",");
                            writer.write("\n\t\t\t\t\t\"data\": {");
                            writer.write("\n\t\t\t\t\t\t\"type\": \""+m.getData().getType()+"\",");
                            writer.write("\n\t\t\t\t\t\t\"size\": "+m.getData().getSize());
                            writer.write("\n\t\t\t\t\t},");
                            writer.write("\n\t\t\t\t\t\"status\": \""+m.getModelStatus()+"\",");
                            writer.write("\n\t\t\t\t\t\"result\": \""+m.getModelResult()+"\"");
                            writer.write("\n\t\t\t\t}");
                            if(j!=s.getModels().size())
                                writer.write(",");

                        }//end of models
                        writer.write("\n \t\t\t]");
                    }
                    writer.write("\n \t\t}");
                    if(i!=studentsList.size())
                        writer.write(",");
                }//end print students
                writer.write("\n\t],");
                //print confrences
                writer.write("\n\t\"conferences\": [");
                i=0;
                for(ConfrenceInformation c:conferenceList)
                {
                    i++;
                    writer.write("\n\t\t{");
                    writer.write("\n\t\t\t\"name\":" +"\""+c.getName()+"\",");
                    writer.write("\n\t\t\t\"date\":" +"\""+c.getDate()+"\",");
                    writer.write("\n\t\t\t\"publications\": [");
                    int j=0;
                    if(c.getGoodModels().size()==0)//case publications empty
                        writer.write("]");
                    else
                    {
                        for(Model m:c.getGoodModels())//publications
                        {
                            j++;
                            writer.write("\n\t\t\t\t{");
                            writer.write("\n\t\t\t\"name\": \""+m.getName()+"\",");
                            writer.write("\n\t\t\t\"data\": {");
                            writer.write("\n\t\t\t\t\"type\": \""+m.getData().getType()+"\",");
                            writer.write("\n\t\t\t\t\"size\": "+m.getData().getSize());
                            writer.write("\n\t\t\t\t},");
                            writer.write("\n\t\t\t\t\t\"status\": \""+m.getModelStatus()+"\",");
                            writer.write("\n\t\t\t\t\t\"result\": \""+m.getModelResult()+"\"");
                            writer.write("\n\t\t\t\t}");
                            if(j!=c.getGoodModels().size())
                                writer.write(",");
                        }//end of publicatoins
                        writer.write("\n\t\t\t]");
                    }

                    writer.write("\n\t\t}");
                    if(i!=conferenceList.size())
                        writer.write(",");
                }
                writer.write("\n\t],");
                //CpuTimeUsed
                writer.write("\n\"cpuTimeUsed\": "+ Cluster.getClusterInstance().numOfTicks()+",");
                //GpuTimeUsed
                writer.write("\n\"gpuTimeUsed\": "+ Cluster.getClusterInstance().numOfTicksInGPU()+",");
                //BatchesProccessed
                writer.write("\n\"batchesProcessed\": "+ Cluster.getClusterInstance().numOfDBs());
                writer.write("\n}");
                writer.close();

            }catch (IOException e) {
                e.printStackTrace();
            }


//            System.out.println("cpu num of ticks:"+Cluster.getClusterInstance().numOfTicks());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }









    }


}

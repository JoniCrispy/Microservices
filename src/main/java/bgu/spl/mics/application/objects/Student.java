package bgu.spl.mics.application.objects;

import bgu.spl.mics.Future;
import bgu.spl.mics.application.services.StudentService;

import java.util.ArrayList;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    public enum Degree {
        MSc, PhD
    }
    private ArrayList<Model> models;
    private StudentService studentService ;
    private String name;
    private String department;
    private Degree degree;
    private int publications;
    private int papersRead;
    private Future future;

    public Student(String Sname, String Sdepratment, Degree Sdegree) {
        name = Sname;
        department = Sdepratment;
        degree = Sdegree;
        publications = 0;
        papersRead = 0;
        studentService = new StudentService(name,this);
        future = null;
    }


    public void setModels(ArrayList<Model> models) {
        this.models = models;
    }

    public Degree getDegree() {
        return degree;
    }

    public StudentService getStudentService() {
        return studentService;
    }

    public Future getFuture() {
        return future;
    }

    public void setFuture(Future future) {
        this.future = future;
    }

    public ArrayList<Model> getModels() {
        return models;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public int getPublications() {
        return publications;
    }

    public int getPapersRead() {
        return papersRead;
    }

    public void addPaperRead(){
        papersRead++;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", department='" + department + '\'' +
                ", degree=" + degree +
                '}';
    }
}

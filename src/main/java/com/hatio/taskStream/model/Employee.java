package com.hatio.taskStream.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedEntityGraph;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class Employee {


    private  int id;
    private  String employeeName;


    public void setId(int id) {
        this.id = id;
    }

    public void setEmployeeName (String name){
        this.employeeName= name;
    }

    public  int getId(){
       return  this.id;

    }
    public  String getEmployeeName() {
          return  this.employeeName;
    }
}




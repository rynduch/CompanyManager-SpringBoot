package com.example.CompanyManager;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import org.hibernate.Session;

@Entity
@Table(name = "employees")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "employee_id")
public class Employee implements Comparable<Employee> {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  int employee_id;

  String name;

  String lastname;
  @Column(name = "date_of_birth")
  LocalDate dob;

  private double salary;
  @Column(name = "e_condition")
  String condition;
@ManyToMany(mappedBy = "employees")
@JsonBackReference
private Set<ClassEmployee> groups = new HashSet<>();

  public Employee(){}
  public Employee(String e_name, String e_lastname, LocalDate e_dob, double e_salary,  EmployeeCondition e_condition){
    this.name = e_name;
    this.lastname = e_lastname;
    this.dob = e_dob;
    this.salary = e_salary;
    this.condition = e_condition.toString();
  }
  @Override
  public int compareTo(Employee e_employee) { // zwraca 0 gdy takie same
    return this.lastname.compareTo(e_employee.lastname);
    //interface Comparable jest częścią standardowej biblioteki języka Java
  }
  public int compare_full_name(Employee e_employee) {
    if (this.compareTo(e_employee) != 0) { //jesli nazwiska sa rozne
      return 1;
    } else {
      if(this.name.equals(e_employee.name)){ //jesli imiona sa takie same
        return 0;
      }
      else{
        return 1;
      }
    }
  }
  public void print_name(){
    System.out.print(this.name + " " + this.lastname);
  }
  public String getName(){
    return this.name;
  }
  public void setName(String n){
    this.name =n;
  }
  public String getLastname(){
    return this.lastname;
  }
  public void setLastname(String l){
    this.lastname = l;
  }
  public LocalDate getDob(){
    return this.dob;
  }
  public void setDob(LocalDate d){
    this.dob = d;
  }
  public double getSalary(){
    return this.salary;
  }
  public void setSalary(double s){
    this.salary = s;
  }
public String getCondition(){
    return this.condition;
  }
  public void setCondition (EmployeeCondition ec){
    this.condition = ec.toString();
  }
  @Override
  public String toString() {
    return "[" + employee_id +
            ", " + name +
            ", " + lastname +
            ", " + dob +
            ", " + salary +
            ", " + condition +
            ']';
  }
  public void displayGroups() {
    if (groups.isEmpty()) {
      System.out.printf("%20s -  nie należy do żadnej grupy.", name + " " + lastname);
    } else {
      System.out.printf("%20s - ", name + " " +  lastname);
      for (ClassEmployee group : groups) {
        System.out.print(" " + group.getName() + ",");
      }
    }
    System.out.println();
  }
  public void addToDB(Session session){
    session.beginTransaction();
    session.save(this);
    session.getTransaction().commit();
    //session.update(this);
  }
  public void removeFromDB(Session session){
    session.beginTransaction();
    Employee e = session.get(Employee.class, employee_id);
    if(e != null) {
      // Usuń lub zmień wszystkie obiekty odwołujące się do obiektu `e`
      // ...
      session.delete(e);
    } else {
      throw new IllegalArgumentException("Employee with id " + employee_id + " does not exist.");
    }
    session.getTransaction().commit();
  }
  public Set<ClassEmployee> getGroups(){
    return groups;
  }
  public int getEmployee_id(){
    return employee_id;
  }
  public void setEmployee_id(int i){
    this.employee_id = i;
  }
}

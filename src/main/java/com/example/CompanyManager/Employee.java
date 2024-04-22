package com.example.CompanyManager;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;

@Entity
@Table(name = "employees")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "employee_id")
public class Employee {
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

  public Employee() {
  }

  public Employee(String e_name, String e_lastname, LocalDate e_dob, double e_salary, String e_condition) {
    this.name = e_name;
    this.lastname = e_lastname;
    this.dob = e_dob;
    this.salary = e_salary;
    this.condition = e_condition;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String n) {
    this.name = n;
  }

  public String getLastname() {
    return this.lastname;
  }

  public void setLastname(String l) {
    this.lastname = l;
  }

  public LocalDate getDob() {
    return this.dob;
  }

  public void setDob(LocalDate d) {
    this.dob = d;
  }

  public double getSalary() {
    return this.salary;
  }

  public void setSalary(double s) {
    this.salary = s;
  }

  public String getCondition() {
    return this.condition;
  }

  public void setCondition(String ec) {
    this.condition = ec;
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

  public Set<ClassEmployee> getGroups() {
    return groups;
  }

  public int getEmployee_id() {
    return employee_id;
  }

}

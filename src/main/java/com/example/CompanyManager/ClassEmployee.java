package com.example.CompanyManager;

import java.util.*;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;

@Entity
@Table(name = "e_groups")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "group_id")
public class ClassEmployee {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  int group_id;
  String name;
  @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Rate> rates = new HashSet<>();
  @ManyToMany
  @JoinTable(
          name = "employee_group",
          joinColumns = @JoinColumn(name = "group_id"),
          inverseJoinColumns = @JoinColumn(name = "employee_id")
  )
  private Set<Employee> employees = new HashSet<>();
  int max;

  public ClassEmployee() {
  }

  public ClassEmployee(String n, int m) {
    setName(n);
    setMax(m);
    this.employees = new HashSet<Employee>();
  }

  public void addEmployee(Employee e_employee) {
    employees.add(e_employee);
  }

  public String getName() {
    return this.name;
  }

  public void setName(String g) {
    this.name = g;
  }

  public int getMax() {
    return this.max;
  }

  public void setMax(int m) {
    this.max = m;
  }

  public String toString() {
    return "[" + group_id +
            ", " + name +
            ", " + max +
            ']';
  }

  public Set<Rate> getRates() {
    return rates;
  }

  public Set<Employee> getEmployees() {
    return employees;
  }

  public double getPercentage() {
    return (double) employees.size() / max;
  }

  public int getGroup_id() {
    return this.group_id;
  }
}

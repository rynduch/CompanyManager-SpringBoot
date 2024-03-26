package com.example.CompanyManager;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import org.hibernate.Session;

@Entity
@Table(name = "e_groups")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "group_id")
public class ClassEmployee{
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
  public ClassEmployee(){}
  public ClassEmployee(String n, int m) {
    setName(n);
    setMax(m);
    this.employees = new HashSet<Employee>();
  }
  public void addEmployee(Employee e_employee, Session session) {
      session.beginTransaction();

      if (employees.size() >= max) {
        System.out.println("Nie dodano pracownika " + e_employee.name + " " + e_employee.lastname + ". Brak miejsca w " + this.name + ".");
      } else {
        employees.add(e_employee);
        e_employee.getGroups().add(this);
      }
      session.update(this);

      session.getTransaction().commit();


  }
  public void addSalary(Employee e_employee, double e_raise) {
    for (Employee e : employees) {
      if (e.compare_full_name(e_employee) == 0) {
        e.setSalary(e.getSalary()+e_raise);
        return;
      }
    }
    System.out.println("Nie znaleziono pracownika w " + this.name + ".");
  }
  public void removeEmployee(Employee e_employee, Session session) {
    session.beginTransaction();

    if (employees.contains(e_employee)) {
      employees.remove(e_employee);
      e_employee.getGroups().remove(this);
      System.out.println("Usunięto pracownika " + e_employee.name + " " + e_employee.lastname + " z " + this.name + ".");
    } else {
      System.out.println("Pracownik " + e_employee.name + " " + e_employee.lastname + " nie należy do " + this.name + ".");
    }

    session.update(this);

    session.getTransaction().commit();
  }
  public void removeEmployee(int e_id, Session session) {
    session.beginTransaction();
    for (Employee e : employees){
      if(e.employee_id == e_id){
        e.getGroups().remove(this); // Assuming you have a getGroups method in Employee class
        System.out.println("Usunięto pracownika " + e.name + " " + e.lastname + " z " + this.name + ".");
      }
    }
    session.update(this);

    session.getTransaction().commit();
  }
public List<Employee> search(String e_lastname, Session session) {
  session.beginTransaction();
  Query query = session.createQuery("FROM Employee WHERE lastname = :lastname");
  query.setParameter("lastname", e_lastname);
  List<Employee> found = query.getResultList();
  session.getTransaction().commit();

  if (found.isEmpty()) {
    System.out.println("Nie znaleziono pracownika w " + this.name + " o nazwisku " + e_lastname + ".");
  }
  return found;
}
  public void searchPartial(String part_of_name, Session session) {
    System.out.println("Wyszukiwanie: " + part_of_name );
    session.beginTransaction();
    Query query = session.createQuery("FROM Employee WHERE name LIKE :name OR lastname LIKE :lastname");
    query.setParameter("name", "%" + part_of_name + "%");
    query.setParameter("lastname", "%" + part_of_name + "%");
    List<Employee> found = query.getResultList();
    session.getTransaction().commit();

    if (found.isEmpty()) {
      System.out.println("Nie znaleziono pracownika w " + this.name + " zawierajacego " + part_of_name + ".");
    }
    for (int i = 0; i < found.size(); i++) {
      System.out.println(found.get(i));
    }
    System.out.println();
  }
  public void sortByName(Session session) {
    System.out.println("Sortowanie po imieniu:");
    session.beginTransaction();
    Query query = session.createQuery("FROM Employee ORDER BY name");
    List<Employee> sorted = query.getResultList();
    session.getTransaction().commit();

    for (Employee employee : sorted) {
      System.out.println(employee.getName());
    }
    System.out.println();
  }

  public void sortBySalary(Session session) {
    System.out.println("Sortowanie po wynagrodzeniu:");
    session.beginTransaction();
    Query query = session.createQuery("FROM Employee ORDER BY salary DESC");
    List<Employee> sorted = query.getResultList();
    session.getTransaction().commit();

    for (Employee employee : sorted) {
      System.out.println(employee.getName() + " " + employee.getSalary());
    }
    System.out.println();
  }
  public Employee max() {
    return Collections.max(employees, Comparator.comparing(Employee::getSalary));
  }

  public String getName(){
    return this.name;
  }
  public void setName(String g){
    this.name =g;
  }
  public int getMax(){
    return this.max;
  }
  public void setMax(int m){
    this.max = m;
  }
  public String toString() {
    return "Grupa{" +
            "id=" + group_id +
            ", name='" + name + '\'' +
            ", max='" + max + '\'' +
            '}';
  }
  public int getGroup_id() {
    return this.group_id;
  }
  public void addToDB(Session session){
    session.beginTransaction();
    session.save(this);
    session.getTransaction().commit();
    //session.update(this);
  }
  public Set<Rate> getRates(){
    return rates;
  }
  public Set<Employee> getEmployees(){
    return employees;
  }
  public void displayEmployes(){
    System.out.println(name + ": ");
    for (Employee e: employees){
      System.out.print(e.getName() + " " + e.getLastname() + ",");
    }
    System.out.println();
  }
  public void displayRates(){
    int number_of_rates = 0;
    double mean_of_rates = 0;
    for (Rate r: rates){
      number_of_rates++;
      mean_of_rates += r.getValue();
    }
    if(number_of_rates != 0){
      mean_of_rates /= number_of_rates;
      System.out.printf("%15s %15d %15f",name,number_of_rates, mean_of_rates);
      System.out.println();
    }
  }
  public String getPercentage(){
    double d_percentage = (double) employees.size()/max;
    return String.valueOf(d_percentage);
  }
  public void removeRate(Rate r){
      rates.remove(r);
  }
}

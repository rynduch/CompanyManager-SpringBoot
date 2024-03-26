package com.example.CompanyManager;

import com.opencsv.CSVWriter;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.hibernate.Session;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.NoResultException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CompanyManagerController {

  private final Session session;

  public CompanyManagerController() {
    session = DatabaseConfig.getSessionFactory().openSession();
  }

  @GetMapping(path = "")
  public ModelAndView displayApiPage() {
    ModelAndView modelAndView = new ModelAndView("api_page");
    return modelAndView;
  }

  @GetMapping(path = "/employee_list")
  public List<Employee> getEmployees() {
    List<Employee> employees = session.createQuery("FROM Employee", Employee.class).getResultList();
    return employees;
  }

  @GetMapping(path = "/employee_table")
  public ModelAndView displayEmployees() {
    ModelAndView modelAndView = new ModelAndView("employee_table_page");
    return modelAndView;
  }

  @PostMapping("/employee")
  public String addEmployee(@ModelAttribute Employee employee) {
    session.beginTransaction();
    session.save(employee);
    session.getTransaction().commit();
    return employee.toString();
  }

  @DeleteMapping("/employee/{id}")
  public ResponseEntity<String> deleteEmployee(@PathVariable("id") String id) {
    Transaction transaction = null;
    try {
      transaction = session.beginTransaction();

      Query query = session.createQuery("FROM Employee WHERE employee_id = :e_id");
      query.setParameter("e_id", id);
      Employee found = (Employee) query.getSingleResult();

      for (ClassEmployee g : found.getGroups()) {
        g.getEmployees().remove(found);
        session.update(g);
      }

      session.remove(found);
      session.flush();
      transaction.commit();

      return ResponseEntity.ok("Employee deleted");
    } catch (NoResultException e) {
      if (transaction != null) {
        transaction.rollback();
      }
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the employee");
    }
  }

  @PostMapping("/employee_condition")
  public ResponseEntity<String> changeEmployeeCondition(String id, String e_condition) {
    Transaction transaction = null;
    try {
      transaction = session.beginTransaction();

      Query query = session.createQuery("FROM Employee WHERE employee_id = :e_id");
      query.setParameter("e_id", id);
      Employee found = (Employee) query.getSingleResult();

      found.condition=e_condition;
      session.flush();
      transaction.commit();

      return ResponseEntity.ok("Condition changed");
    } catch (NoResultException e) {
      if (transaction != null) {
        transaction.rollback();
      }
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee not found");
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while changing employee's condition");
    }
  }


  @GetMapping("/employee/csv")
  public ResponseEntity<byte[]> getEmployeesCsv() {
    List<Employee> employees = session.createQuery("FROM Employee", Employee.class).list();
    try {
      StringWriter writer = new StringWriter();
      CSVWriter csvWriter = new CSVWriter(writer);

      csvWriter.writeNext(new String[] {"ID", "Name", "Lastname", "Date of Birth", "Salary", "Condition"});

      for (Employee employee : employees) {
        csvWriter.writeNext(new String[] {
                String.valueOf(employee.getEmployee_id()),
                employee.getName(),
                employee.getLastname(),
                employee.getDob().toString(),
                String.valueOf(employee.getSalary()),
                employee.getCondition()
        });
      }

      csvWriter.close();

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
      headers.setContentDispositionFormData("attachment", "employees.csv");

      return new ResponseEntity<>(writer.toString().getBytes(), headers, HttpStatus.OK);
    } catch (IOException e) {
      e.printStackTrace();
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/group_list")
  public List<ClassEmployee> getGroups() {
    List<ClassEmployee> groups = session.createQuery("FROM ClassEmployee", ClassEmployee.class).list();
    return groups;
  }

  @GetMapping(path = "/group_table")
  public ModelAndView displayGroups() {
    ModelAndView modelAndView = new ModelAndView("group_table_page");
    return modelAndView;
  }

  @PostMapping("/group")
  public String addGroup(@ModelAttribute ClassEmployee group) {
    session.beginTransaction();
    session.save(group);
    session.getTransaction().commit();
    return group.toString();
  }

  @DeleteMapping("/group/{id}")
  public ResponseEntity<String> deleteGroup(@PathVariable("id") String id) {
    Transaction transaction = null;
    try {
      transaction = session.beginTransaction();

      Query query = session.createQuery("FROM ClassEmployee WHERE group_id = :g_id");
      query.setParameter("g_id", id);
      ClassEmployee found = (ClassEmployee) query.getSingleResult();

      for (Employee e : found.getEmployees()) {
        e.getGroups().remove(found);
        session.update(e);
      }

      for (Rate r : found.getRates()) {
        session.remove(r);
      }

      session.remove(found);
      session.flush();
      transaction.commit();

      return ResponseEntity.ok("Group deleted");
    } catch (NoResultException e) {
      if (transaction != null) {
        transaction.rollback();
      }
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found");
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the group.");
    }
  }

  @GetMapping("/group/{id}/employee")
  public String getEmployeesInGroup(@PathVariable String id) {
    Query<ClassEmployee> query = session.createQuery("FROM ClassEmployee WHERE group_id=:groupId", ClassEmployee.class);
    query.setParameter("groupId", id);
    ClassEmployee group = query.getSingleResult();
    List<Employee> employees_list = new ArrayList<>(group.getEmployees());
    return employees_list.toString();
  }

  @GetMapping("/group/{id}/fill")
  public String getGroupFill(@PathVariable String id) {
    Query<ClassEmployee> query = session.createQuery("FROM ClassEmployee WHERE group_id=:groupId", ClassEmployee.class);
    query.setParameter("groupId", id);
    ClassEmployee group = query.getSingleResult();
    return group.getPercentage();
  }

  @GetMapping("/rate_list")
  public List<Rate> getRates() {
    List<Rate> rates = session.createQuery("FROM Rate", Rate.class).list();
    return rates;
  }

  @GetMapping(path = "/rate_table")
  public ModelAndView displayRates() {
    ModelAndView modelAndView = new ModelAndView("rate_table_page");
    return modelAndView;
  }

  @PostMapping("/rate")
  public String addRating(@ModelAttribute Rate rating, @RequestParam("group_id") int groupId) {
    ClassEmployee group = session.get(ClassEmployee.class, groupId);
    rating.setGroup(group);
    rating.addToDB(session);
    return rating.toString();
  }

}

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
import java.io.*;
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
    return new ModelAndView("api_page");
  }

  @GetMapping(path = "/employee_list")
  public List<Employee> getEmployees() {
    return session.createQuery("FROM Employee", Employee.class).getResultList();
  }

  @GetMapping(path = "/employee_table")
  public ModelAndView displayEmployees() {
    return new ModelAndView("employee_table_page");
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

      found.setCondition(e_condition);
      session.flush();
      transaction.commit();

      return ResponseEntity.ok("Condition changed");
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Employee does not exist");
    }
  }

  @GetMapping("/employee/csv")
  public ResponseEntity<byte[]> getEmployeesCsv() {
    List<Employee> employees = session.createQuery("FROM Employee", Employee.class).list();
    try {
      StringWriter writer = new StringWriter();
      CSVWriter csvWriter = new CSVWriter(writer);
      csvWriter.writeNext(new String[]{"ID", "Name", "Lastname", "Date of Birth", "Salary", "Condition"});

      for (Employee employee : employees) {
        csvWriter.writeNext(new String[]{
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
    return session.createQuery("FROM ClassEmployee", ClassEmployee.class).list();
  }

  @GetMapping(path = "/group_table")
  public ModelAndView displayGroups() {
    return new ModelAndView("group_table_page");
  }

  @PostMapping("/group")
  public String addGroup(@ModelAttribute ClassEmployee group) {
    session.beginTransaction();
    session.save(group);
    session.getTransaction().commit();
    return group.toString();
  }

  @PostMapping("/employee_to_group")
  public ResponseEntity<String> addEmployeeToGroup(@RequestParam("emp_id") int emp_id, @RequestParam("gr_id") int gr_id) {
    Transaction transaction = null;
    try {
      transaction = session.beginTransaction();

      Query query_e = session.createQuery("FROM Employee WHERE employee_id = :e_id");
      query_e.setParameter("e_id", emp_id);
      Employee found_e = (Employee) query_e.getSingleResult();

      Query query_g = session.createQuery("FROM ClassEmployee WHERE group_id = :g_id");
      query_g.setParameter("g_id", gr_id);
      ClassEmployee found_g = (ClassEmployee) query_g.getSingleResult();

      for (ClassEmployee g : found_e.getGroups()) {
        if (g.group_id == found_g.group_id) {
          if (transaction != null) {
            transaction.rollback();
          }
          return ResponseEntity.ok("Employee is already in group");
        }
      }
      if (found_g.getPercentage() == 1) {
        if (transaction != null) {
          transaction.rollback();
        }
        return ResponseEntity.ok("Group is full");
      }
      found_g.addEmployee(found_e);
      found_e.getGroups().add(found_g);
      session.update(found_g);
      session.flush();
      transaction.commit();

      return ResponseEntity.ok("Employee added to group");
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Employee/Group does not exist");
    }
  }

  @DeleteMapping("/employee_from_group/{gr_id}/{emp_id}")
  public ResponseEntity<String> removeEmployeeFromGroup(@PathVariable("emp_id") int emp_id, @PathVariable("gr_id") int gr_id) {
    Transaction transaction = null;
    try {
      transaction = session.beginTransaction();

      Query query_e = session.createQuery("FROM Employee WHERE employee_id = :e_id");
      query_e.setParameter("e_id", emp_id);
      Employee found_e = (Employee) query_e.getSingleResult();

      Query query_g = session.createQuery("FROM ClassEmployee WHERE group_id = :g_id");
      query_g.setParameter("g_id", gr_id);
      ClassEmployee found_g = (ClassEmployee) query_g.getSingleResult();

      int found_in_group = 0;
      for (ClassEmployee g : found_e.getGroups()) {
        if (g.group_id == found_g.group_id) {
          found_in_group = 1;
        }
      }

      if (found_in_group == 0) {
        if (transaction != null) {
          transaction.rollback();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee is not in group");
      }

      found_e.getGroups().remove(found_g);
      found_g.getEmployees().remove(found_e);

      session.flush();
      transaction.commit();

      return ResponseEntity.ok("Employee removed from group");
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while removing employee from group");
    }
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
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the group.");
    }
  }

  @GetMapping("/rate_list")
  public List<Rate> getRates() {
    return session.createQuery("FROM Rate", Rate.class).list();
  }

  @GetMapping(path = "/rate_table")
  public ModelAndView displayRates() {
    return new ModelAndView("rate_table_page");
  }

  @PostMapping("/rate")
  public ResponseEntity<String> addRating(@RequestParam("value") int value, @RequestParam("group_id") int gr_id, @RequestParam("comment") String comment ) {

    Transaction transaction = null;
    try {
      transaction = session.beginTransaction();

      Query query = session.createQuery("FROM ClassEmployee WHERE group_id = :gr_id");
      query.setParameter("gr_id", gr_id);
      ClassEmployee found = (ClassEmployee) query.getSingleResult();

      Rate rate = new Rate(value, found, comment);
      session.save(rate);
      transaction.commit();

      return ResponseEntity.ok("Rate added");
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Group does not exist");
    }
  }

  @DeleteMapping("/rate/{id}")
  public ResponseEntity<String> deleteRate(@PathVariable("id") String id) {
    Transaction transaction = null;
    try {
      transaction = session.beginTransaction();

      Query query = session.createQuery("FROM Rate WHERE rate_id = :r_id");
      query.setParameter("r_id", id);
      Rate found = (Rate) query.getSingleResult();

      session.remove(found);
      session.flush();
      transaction.commit();

      return ResponseEntity.ok("Rate deleted");
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the rate");
    }
  }
}

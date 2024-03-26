package com.example.CompanyManager;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DatabaseConfig {
  private static final SessionFactory sessionFactory;

  static {
    try {
      Configuration configuration = new Configuration();
      configuration.configure("hibernate.cfg.xml");
      configuration.addAnnotatedClass(Employee.class);
      configuration.addAnnotatedClass(ClassEmployee.class);
      configuration.addAnnotatedClass(Rate.class);
      sessionFactory = configuration.buildSessionFactory();
    } catch (Throwable ex) {
      throw new ExceptionInInitializerError(ex);
    }
  }

  public static SessionFactory getSessionFactory() {
    return sessionFactory;
  }
}

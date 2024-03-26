package com.example.CompanyManager;

import jakarta.persistence.*;
import org.hibernate.Session;

import java.time.LocalDate;

@Entity
@Table(name = "rates")
public class Rate {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int rate_id;

  @Column(name = "value", nullable = false)
  private int value;

  @ManyToOne
  @JoinColumn(name = "group_id", nullable = false)
  private ClassEmployee group;

  @Column(name = "date", nullable = false)
  private LocalDate date = LocalDate.now();

  @Column(name = "comment", nullable = false)
  private String comment;

  public Rate(){}
  public Rate(int value, ClassEmployee group, String comment) {
    setValue(value);
    setComment(comment);
    setGroup(group);
  }
  public void setValue(int value) {
    if (value < 0 || value > 6) {
      throw new IllegalArgumentException("Ocena musi byc wartoscia z przedzialu 0 - 6.");
    }
    this.value = value;
  }

  public int getValue() {
    return this.value;
  }

  public ClassEmployee getGroup() {
    return this.group;
  }

  public void setGroup(ClassEmployee group) {
    if (group == null) {
      throw new IllegalArgumentException("Grupa o danym ID nie istnieje w bazie danych.");
    }
    this.group = group;
  }

  public LocalDate getDate() {
    return this.date;
  }

  public String getComment() {
    return this.comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
  public String toString(){
    return "Ocena{" +
            "id=" + rate_id +
            ", value='" + value + '\'' +
            ", group='" + group.getName() + '\'' +
            ", date='" + date + '\'' +
            ", comment='" + comment + '\'' +
            '}';
  }
  public void addToDB(Session session){
    session.beginTransaction();
    session.save(this);
    session.getTransaction().commit();
    //session.update(this);
  }
}
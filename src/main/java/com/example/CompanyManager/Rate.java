package com.example.CompanyManager;

import jakarta.persistence.*;

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

  public Rate() {
  }

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

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String toString() {
    return "[" + rate_id +
            ", " + value +
            ", " + group.getName() +
            ", " + date +
            ", " + comment +
            ']';
  }

  public LocalDate getDate() {
    return this.date;
  }

  public String getComment() {
    return this.comment;
  }

  public int getRate_id() {
    return this.rate_id;
  }
}
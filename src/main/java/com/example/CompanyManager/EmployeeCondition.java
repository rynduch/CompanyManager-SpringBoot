package com.example.CompanyManager;

public enum EmployeeCondition {
  OBECNY{
    @Override
    public String toString(){
      return "OBECNY";
    }
  },
  DELEGACJA{
    @Override
    public String toString(){
      return "DELEGACJA";
    }
  },
  CHORY{
    @Override
    public String toString(){
      return "CHORY";
    }
  },
  NIEOBECNY{
    @Override
    public String toString(){
      return "NIEOBECNY";
    }
  }

}

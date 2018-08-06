package com.sella.facts;

import oracle.jbo.domain.Timestamp;

public class Person
{
  private int age;
  private Timestamp dob;
  private PolicyType policy_type = Person.PolicyType.UNKNOWN;

  public Person()
  {
    super();
  }

  public void setAge(int age)
  {
    this.age = age;
  }

  public int getAge()
  {
    return age;
  }

  public void setDob(Timestamp dob)
  {
    this.dob = dob;
  }

  public Timestamp getDob()
  {
    return dob;
  }

  public void setPolicy_type(PolicyType policy_type)
  {
    this.policy_type = policy_type;
  }

  public PolicyType getPolicy_type()
  {
    return policy_type;
  }
  
  public enum PolicyType
  {
    INDIVIDUAL,
    GROUP,
    UNKNOWN;
  }
}

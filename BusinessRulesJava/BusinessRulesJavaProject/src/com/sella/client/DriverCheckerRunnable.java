package com.sella.client;

import com.sella.facts.Person;

public class DriverCheckerRunnable
  implements Runnable
{
  private final Person m_person;
  private final InsuranceProduct m_insProduct;

  DriverCheckerRunnable(InsuranceProduct insProduct, Person person)
  {
    m_person = person;
    m_insProduct = insProduct;
  }

  @Override
  public void run()
  {
    m_insProduct.checkPerson(m_person);
  }
}

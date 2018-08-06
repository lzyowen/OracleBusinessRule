package com.sella.client;

import com.sella.facts.Person;

import java.util.ArrayList;
import java.util.List;

import oracle.rules.sdk2.repository.DictionaryFQN;


public abstract class InsuranceProduct
{
  protected static final String DICT_LOCATION = "/home/oracle/Downloads/BusinessRules_Java-master/BusinessRulesJava/BusinessRulesJavaProject/oracle/rules/com/sella/products/InsuranceProducts.rules";
    //"C:\\JDeveloper\\mywork\\BusinessRulesJava\\BusinessRulesJavaProject\\oracle\\rules\\com\\sella\\products\\InsuranceProducts.rules";

  protected static final String DICT_PKG = "com.sella.products";
  protected static final String DICT_NAME = "InsuranceProducts";

  protected static final DictionaryFQN DICT_FQN = new DictionaryFQN(DICT_PKG, DICT_NAME);
  protected static final String DF_NAME = "ProductRuleDecisionService";

  protected static List<Person> createPersons()
  {
    return new ArrayList()
    {
      {
        {
          Person person1 = new Person();
          person1.setAge(15);
          person1.setPolicy_type(new Person().getPolicy_type().INDIVIDUAL);
          add(person1);
        }
        {
          Person person2 = new Person();
          person2.setAge(30);
          person2.setPolicy_type(new Person().getPolicy_type().GROUP);
          add(person2);
        }
        {
          Person person2 = new Person();
          person2.setAge(80);
          person2.setPolicy_type(new Person().getPolicy_type().UNKNOWN);
          add(person2);
        }
      }
    };
  }

  /**
   * @description
   * @param person
   */
  public abstract void checkPerson(final Person person);
}

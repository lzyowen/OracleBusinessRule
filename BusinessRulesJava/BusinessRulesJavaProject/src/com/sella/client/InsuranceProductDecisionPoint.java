package com.sella.client;

import com.sella.facts.Person;
import com.sella.facts.Product;

import java.util.ArrayList;
import java.util.List;

import oracle.rules.sdk2.decisionpoint.DecisionPoint;
import oracle.rules.sdk2.decisionpoint.DecisionPointInstance;


public class InsuranceProductDecisionPoint
  extends InsuranceProduct
{
  protected DecisionPoint m_decisionPoint;


  @Override
  public void checkPerson(final Person person)
  {
    try
    {
      DecisionPointInstance instance = m_decisionPoint.getInstance();
      instance.setInputs(new ArrayList<Object>()
        {
          {
            add(person);
          }
        });


      List<Object> outputs = instance.invoke();

      if (outputs.isEmpty())
      {
        System.err.println("No Results!");
      }

      List<Product> products = (ArrayList<Product>) outputs.get(0);

      for (Product product: products)
      {
        System.out.println("Product Name [" + product.getName() + "] for the Person age: "+ person.getAge() + " policy type: " + person.getPolicy_type());
      }

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}

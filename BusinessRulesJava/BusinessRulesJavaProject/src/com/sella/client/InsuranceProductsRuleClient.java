package com.sella.client;

import com.sella.facts.Person;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import oracle.rules.sdk2.decisionpoint.DecisionPointBuilder;
import oracle.rules.sdk2.decisionpoint.DecisionPointDictionaryFinder;
import oracle.rules.sdk2.dictionary.RuleDictionary;
import oracle.rules.sdk2.exception.SDKException;
import oracle.rules.sdk2.exception.SDKWarning;


public class InsuranceProductsRuleClient
  extends InsuranceProductDecisionPoint
{
  private InsuranceProduct insuranceProduct = null;

  public InsuranceProductsRuleClient()
  {
    super();
    try
    {
      // specifying the Decision Function and a pre-loaded
      // RuleDictionary instance
      m_decisionPoint = new DecisionPointBuilder().with(DF_NAME).with(loadRuleDictionary()).build();

    }
    catch (SDKException e)
    {
      System.err.println("Failed to build Decision Point: " + e.getMessage());
      e.printStackTrace();
    }
  }

  public static void main(String[] args)
    throws SDKException
  {
    InsuranceProductsRuleClient client = new InsuranceProductsRuleClient();
    client.runWithPreloadedDictionary();
  }

  public void runWithPreloadedDictionary()
    throws SDKException
  {
    // specifying the Decision Function and a RuleDictionary instance
    m_decisionPoint = new DecisionPointBuilder().with(DF_NAME).with(loadRuleDictionary()).build();

    ExecutorService exec = Executors.newCachedThreadPool();
    List<Person> persons = createPersons();

    for (int i = 0; i < persons.size(); i++)
    {
      Person person = persons.get(i);
      exec.execute(new DriverCheckerRunnable(this, person));
    }

    exec.shutdown();
  }

  private static RuleDictionary loadRuleDictionary()
  {
    RuleDictionary dict = null;
    Reader reader = null;
    try
    {
      reader = new FileReader(new File(DICT_LOCATION));
      dict = RuleDictionary.readDictionary(reader, new DecisionPointDictionaryFinder(null));
      List<SDKWarning> warnings = new ArrayList<SDKWarning>();

      dict.update(warnings);
      if (warnings.size() > 0)
      {
        System.err.println("Validation warnings: " + warnings);
      }
    }
    catch (SDKException e)
    {
      System.err.println(e);
    }
    catch (FileNotFoundException e)
    {
      System.err.println(e);
    }
    catch (IOException e)
    {
      System.err.println(e);
    }
    finally
    {
      if (reader != null)
      {
        try
        {
          reader.close();
        }
        catch (IOException ioe)
        {
          ioe.printStackTrace();
        }
      }
    }

    return dict;
  }
}

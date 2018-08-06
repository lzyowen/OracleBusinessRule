package com.sella.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

import java.util.ArrayList;
import java.util.List;

import oracle.rules.sdk2.datamodel.DataModel;
import oracle.rules.sdk2.datamodel.FactType;
import oracle.rules.sdk2.datamodel.Property;
import oracle.rules.sdk2.decisionpoint.DecisionPointDictionaryFinder;
import oracle.rules.sdk2.decisiontable.BucketSet;
import oracle.rules.sdk2.dictionary.RuleDictionary;
import oracle.rules.sdk2.dictionary.UndoableEdit;
import oracle.rules.sdk2.exception.ConcurrentUpdateException;
import oracle.rules.sdk2.exception.SDKException;
import oracle.rules.sdk2.exception.SDKWarning;


public class RuleUtil
{
  public RuleUtil()
  {
    super();
  }

  /**
   * Creates a new Rules Dictionary in memory
   * @param dictionaryName      A name for the dictionary, used as a className
   * @param dictionaryPackage   A name for the dictionary package, used as a java package name.
   */
  public static RuleDictionary createDictionaryInMemory(String dictionaryName, String dictionaryPackage)
    throws SDKException
  {
    RuleDictionary dictionary = RuleDictionary.createDictionary(dictionaryName, new DecisionPointDictionaryFinder());
    dictionary.setName(dictionaryName);
    dictionary.setPackage(dictionaryPackage);

    return dictionary;
  }

  /**
   * Demonstrates how to add a java fact to a dictionary data model
   * @param dictionary      An existing dictionary
   * @param javaclass       A java bean class
   */
  public static RuleDictionary addFactsToDictionary(RuleDictionary dictionary, Class bean)
    throws SDKException
  {
    DataModel dataModel = dictionary.getDataModel();
    dataModel.addJavaClass(bean, false, null, null, null, null);
    return dictionary;
  }

  /* This method demonstrates how to associate a BucketSet to a dictionary fact attribute
   *
   * @param   bucketset    An existing bucketset
   * @param   factAlias    The alias name of an existing fact
   * @param   factProperty The name of a property contained in the fact
   * @param   model        An existing dictionary data model
   */

  public static void associateBucketSetToFact(BucketSet bucketset, String factAlias, String factProperty, DataModel model)
  {

    // Associate the BucketSet with a prticular fact
    FactType fType = model.getFactTypeTable().getByAlias(factAlias);

    if (fType == null)
    {
      System.out.println("Cannot locate fact type");
    }
    else
    {
      Property prop = fType.getPropertyTable().getByName(factProperty);
      if (prop == null)
      {
        System.out.println("Cannot locate fact " + factAlias + " property " + factProperty);
      }
      else
      {
        prop.setBucketSet(bucketset);
      }
    }
  }

  /**
   * Update the rule dictionary from the specified dictionaryPath
   * @param A rule dictionary object
   * @return boolean true if the update was successful otherwise false.
   * @see See writeDictionary http://docs.oracle.com/cd/E23943_01/apirefs.1111/e10663/toc.htm
   *
   */
  public static boolean updateRuleDictionary(RuleDictionary dictionary)
    throws Exception
  {
    UndoableEdit undo = null;
    List<SDKWarning> warnings = new ArrayList<SDKWarning>();
    boolean rc = false;

    try
    {
      undo = dictionary.update(warnings);
      rc = true;
    }
    catch (ConcurrentUpdateException e)
    {
      dictionary.rollback();
    }
    catch (SDKException e)
    {
      dictionary.rollback();
    }
    return rc;

  }

  public static DataModel getDataModel(RuleDictionary dictionary)
  {
    DataModel dateModel = dictionary.getDataModel();
    return dateModel;
  }


  /**
   * Stores the rule dictionary from the specified dictionaryPath
   * @param A rule dictionary object
   * @param dictionaryLocation The full path to the .rules file.
   * @see See writeDictionary http://docs.oracle.com/cd/E23943_01/apirefs.1111/e10663/toc.htm
   *
   */
  public static void storeRuleDictionary(RuleDictionary dictionary, String dictionaryLocation)
    throws Exception
  {
    List<SDKWarning> warnings = new ArrayList<SDKWarning>();
    List<SDKException> errors = new ArrayList<SDKException>();

    dictionary.validate(errors, warnings);

    if (warnings.size() > 0)
    {
      System.err.println("Validation warnings: " + warnings);
    }
    
    if (errors.size() > 0)
    {
      System.err.println("Validation errors: " + errors);
      System.out.println("Skipping write of rule dictionary");
    }
    else
    {
      StringWriter swriter = new StringWriter();
      dictionary.writeDictionary(swriter);
      Writer writer = null;
      try
      {
        writer = new OutputStreamWriter(new FileOutputStream(new File(dictionaryLocation)), "UTF-8");
        writer.write(swriter.toString());
      }
      finally
      {
        if (writer != null)
          try
          {
            writer.close();
          }
          catch (IOException e)
          {
            System.out.println("Warning: Unable to close dictionary writer.");
          }
      }
    }
  }
}

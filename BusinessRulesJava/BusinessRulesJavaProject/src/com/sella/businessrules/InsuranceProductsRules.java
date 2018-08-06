package com.sella.businessrules;

import com.sella.common.RuleUtil;
import com.sella.facts.Person;
import com.sella.facts.Product;

import oracle.rules.sdk2.datamodel.BucketSetTable;
import oracle.rules.sdk2.datamodel.DataModel;
import oracle.rules.sdk2.datamodel.DecisionFunction;
import oracle.rules.sdk2.decisionpoint.MetadataHelper;
import oracle.rules.sdk2.decisiontable.BucketSet;
import oracle.rules.sdk2.decisiontable.DTAction;
import oracle.rules.sdk2.decisiontable.DTRule;
import oracle.rules.sdk2.decisiontable.Dimension;
import oracle.rules.sdk2.decisiontable.RuleSheet;
import oracle.rules.sdk2.dictionary.RuleDictionary;
import oracle.rules.sdk2.exception.SDKException;
import oracle.rules.sdk2.ruleset.Action;
import oracle.rules.sdk2.ruleset.Expression;
import oracle.rules.sdk2.ruleset.Pattern;
import oracle.rules.sdk2.ruleset.Rule;
import oracle.rules.sdk2.ruleset.RuleSet;
import oracle.rules.sdk2.ruleset.RuleSheetTable;
import oracle.rules.sdk2.ruleset.SimpleTest;

public class InsuranceProductsRules
{
  public InsuranceProductsRules()
  {
    super();
  }

  public static void main(String[] args)
    throws SDKException, Exception
  {
    final String dictionaryLocation = "./oracle/rules/com/sella/products/InsuranceProducts.rules";
    String dictionaryName = "InsuranceProducts";
    String dictionaryPackage = "com.sella.products";
    RuleDictionary dictionary = RuleUtil.createDictionaryInMemory(dictionaryName, dictionaryPackage);

    // Delete the existing model
    dictionary.getDataModel().clear();

    // Add a new Java Fact to the dictionary
    dictionary = RuleUtil.addFactsToDictionary(dictionary, Person.class);
    dictionary = RuleUtil.addFactsToDictionary(dictionary, Person.PolicyType.class);
    dictionary = RuleUtil.addFactsToDictionary(dictionary, Product.class);

    // Add a new bucketset to the dictionary
    BucketSet ageBucketSet = addAgeBucketSet(dictionary);

    // Associate the new BucketSet with new Fact field
    RuleUtil.associateBucketSetToFact(ageBucketSet, "Person", "age", dictionary.getDataModel());

    // update dictionary
    if (!RuleUtil.updateRuleDictionary(dictionary))
    {
      System.out.println("Dictionary update failed.");
    }
    else
    {
      System.out.println("Dictionary Updated.");
    }

    // Create 'Decision Function'
    DecisionFunction decisionFunction = createDecisionFunction(dictionary, "ProductRuleDecisionService", "com.sella.facts.Person", false, false);
    decisionFunction.getDecisionFunctionInputTable().get(0).setList(false);
    decisionFunction.getDecisionFunctionInputTable().get(0).setTree(false);
    
    // Add 'Decision Function' output parameter
    addDecisionFunctionOutput(decisionFunction);
    
    if (!RuleUtil.updateRuleDictionary(dictionary))
    {
      System.out.println("Dictionary update failed.");
    }
    else
    {
      System.out.println("Dictionary Updated.");
    }
    
    // set 'Decision Function' output parameter type
    decisionFunction.getDecisionFunctionOutputTable().get(0).setList(true);
    
    if (!RuleUtil.updateRuleDictionary(dictionary))
    {
      System.out.println("Dictionary update failed.");
    }
    else
    {
      System.out.println("Dictionary Updated.");
    }

    // check for existing ruleset named MyRuleSet
    RuleSet myRuleSet = dictionary.getRuleSet("ProductRuleDecisionService");

    // if it does exist, remove it
    if (myRuleSet != null)
    {
      dictionary.removeRuleSet("ProposalProductRules");
      if (!RuleUtil.updateRuleDictionary(dictionary))
      {
        System.out.println("Dictionary update failed.");
      }
      else
      {
        System.out.println("Removed old ruleset");
      }
    }
    else
    {
      // Add a new Ruleset
      myRuleSet = dictionary.createEmptyRuleSet("ProductRuleDecisionService");
      if (!RuleUtil.updateRuleDictionary(dictionary))
      {
        System.out.println("Dictionary update failed.");
      }
      else
      {
        System.out.println("Dictionary Updated.");
      }
    }

    // Add a decisionTable with rules that use the Bucketset
    //addDecisiontTableRuleToRuleset(dictionary, ageBucketSet, myRuleSet);
    
    //Add IF-THEN rule
    addIfThenRuleToRuleset(myRuleSet);
    // Update and rewrite the dictionary file
    boolean success = RuleUtil.updateRuleDictionary(dictionary);
    if (success)
    {
      RuleUtil.storeRuleDictionary(dictionary, dictionaryLocation);
      System.out.println("Wrote dictionary to filesystem");
    }
    else
    {
      System.out.println("Unable to update dictionary");
    }
  }
  //add insurance rule ,if age<20 product.name = Child Care, if age>20 product.name = People Care
  public static void addIfThenRuleToRuleset(RuleSet myRuleSet) {
      
      Rule AgeLess20Rule = myRuleSet.getRuleByName("AgeLess20Rule");
      if(AgeLess20Rule != null) {
          
          System.out.println("insuranceRule already exists, skipping add");
      }else{
          AgeLess20Rule = myRuleSet.getRuleTable().add();
          AgeLess20Rule.setName("AgeLess20Rule");
                     
          //add pattern to the rule
          Pattern p = AgeLess20Rule.getPatternTable().add();
          p.setForm(Pattern.FORM_FACT_TYPE);
          
          SimpleTest simple = p.getSimpleTestTable().add();
          
          simple.getLeft().setValue("Person.age");
          simple.setOperator(">");
          
          // if using literal
          simple.getRight().setLiteralValue("20");
                
          // Add the Action to the Rule
          
          Action act = AgeLess20Rule.getActionTable().add();
          act.setForm(Action.FORM_ASSERT_NEW);
          act.setTarget("Product");
          
          // The form type of Action will determine what properties need to be set
          
          Expression pname = act.getExpressionByParameterAlias("name");
          pname.setValue("\"Child Care\"");
      }
      
      //rule 2
      Rule AgeExceed20Rule = myRuleSet.getRuleByName("AgeExceed20Rule");
      if(AgeExceed20Rule != null) {
          
          System.out.println("AgeExceed20Rule already exists, skipping add");
      }else{
          AgeExceed20Rule = myRuleSet.getRuleTable().add();
          AgeExceed20Rule.setName("AgeExceed20Rule");
                     
          //add pattern to the rule
          Pattern p = AgeExceed20Rule.getPatternTable().add();
          p.setForm(Pattern.FORM_FACT_TYPE);
          
          SimpleTest simple = p.getSimpleTestTable().add();
          
          simple.getLeft().setValue("Person.age");
          simple.setOperator("<");
          
          // if using literal
          simple.getRight().setLiteralValue("20");
                
          // Add the Action to the Rule
          
          Action act = AgeExceed20Rule.getActionTable().add();
          act.setForm(Action.FORM_ASSERT_NEW);
          act.setTarget("Product");
          
          // The form type of Action will determine what properties need to be set
          
          Expression pname = act.getExpressionByParameterAlias("name");
          pname.setValue("\"People Care\"");
      }
  }

  /* This method demonstrates how to add a Decision Table to a Rule Set
   *
   * @param   dict             An existing rules dictionary object
   * @param   shipBucketSet    An existing bucketset
   * @param   ruleset          An existing ruleset contained in the dictionary
   */

  public static void addDecisiontTableRuleToRuleset(RuleDictionary dict, BucketSet ageBucketSet, RuleSet ruleset)
    throws Exception
  {

    RuleSheetTable sheetTable = ruleset.getRuleSheetTable();
    RuleSheet dt = sheetTable.getByName("Proposal");

    if (dt != null)
    {
      System.out.println("Proposal already exists, skipping add");
    }
    else
    {
      // Add a new Rulesheet aka DecisionTable
      dt = ruleset.getRuleSheetTable().add();
      dt.setName("Proposal");

      // Add two dimensions aka condition rows to table
      Dimension[] conditions = new Dimension[2];

      // First Row
      conditions[0] = dt.getDimensionTable().add();
      conditions[0].setValue("Person.age");
      conditions[0].setSharedBucketSet(ageBucketSet);

      // Second Row
      conditions[1] = dt.getDimensionTable().add();
      conditions[1].setValue("Person.policy_type");
      conditions[1].setSharedBucketSetByAlias("Person$PolicyType");

      System.out.println("RuleTable size is " + dt.getDTRuleTable().size());

      DTAction dtAction = dt.getDTActionTable().add();
      dtAction.setForm(Action.FORM_ASSERT_NEW);
      dtAction.setTarget("Product");
      dtAction.setAlwaysSelected(true);

      Expression assertExp = dtAction.getExpressionByParameterAlias("name");
      assertExp.setDTActionParameterName("name");
      assertExp.setValue("\"PROD_TYPE1\"");

      System.out.println("1. Rules can be added? " + dt.getDTRuleTable().canAdd());
      DTRule dtRuleDef = dt.getDTRuleTable().get(0);

      dtRuleDef.getDimensionNode(0).setValues("Minor");
      dtRuleDef.getDimensionNode(1).setValues("INDIVIDUAL");

      if (!RuleUtil.updateRuleDictionary(dict))
      {
        System.out.println("Dictionary update failed.");
      }
      else
      {
        System.out.println("Updated dictionary");
      }
      
      // 2 ---------------------------------------------
      System.out.println("2. Rules can be added? " + dt.getDTRuleTable().canAdd());
      DTRule dtRuleDef1 = dt.getDTRuleTable().add();
      dtRuleDef1.getDimensionNode(0).setValues("Junior");
      dtRuleDef1.getDimensionNode(1).setValues("GROUP");

      if (!RuleUtil.updateRuleDictionary(dict))
      {
        System.out.println("Dictionary update failed.");
      }
      else
      {
        System.out.println("Updated dictionary");
      }

      Expression expr = dtRuleDef1.getDTActionNode(0).getExpressionByDTActionParameterName("name");
      dtRuleDef1.getDTActionNode(0).setActionSelected(true);
      if (expr != null)
      {
        expr.setValue("\"PROD_TYPE2\"");
      }

      // 3 ---------------------------------------------
      System.out.println("3. Rules can be added? " + dt.getDTRuleTable().canAdd());
      DTRule dtRuleDef3 = dt.getDTRuleTable().add();
      dtRuleDef3.getDimensionNode(0).setValues("Middle");
      dtRuleDef3.getDimensionNode(1).setValues("INDIVIDUAL");

      if (!RuleUtil.updateRuleDictionary(dict))
      {
        System.out.println("UNABLE to update dictionary.");
      }
      else
      {
        System.out.println("Updated dictionary");
      }

      Expression expr3 = dtRuleDef3.getDTActionNode(0).getExpressionByDTActionParameterName("name");
      dtRuleDef3.getDTActionNode(0).setActionSelected(true);
      if (expr3 != null)
      {
        expr3.setValue("\"PROD_TYPE3\"");
      }
      
      // 4 ---------------------------------------------
      System.out.println("4. Rules can be added? " + dt.getDTRuleTable().canAdd());
      DTRule dtRuleDef4 = dt.getDTRuleTable().add();
      dtRuleDef4.getDimensionNode(0).setValues("Senior");
      dtRuleDef4.getDimensionNode(1).setValues("GROUP");

      if (!RuleUtil.updateRuleDictionary(dict))
      {
        System.out.println("Dictionary update failed.");
      }
      else
      {
        System.out.println("Updated dictionary");
      }

      Expression expr4 = dtRuleDef4.getDTActionNode(0).getExpressionByDTActionParameterName("name");
      dtRuleDef4.getDTActionNode(0).setActionSelected(true);
      if (expr4 != null)
      {
        expr4.setValue("\"PROD_TYPE4\"");       
      }
      
      // 5 ---------------------------------------------
      System.out.println("5. Rules can be added? " + dt.getDTRuleTable().canAdd());
      DTRule dtRuleDef5 = dt.getDTRuleTable().add();
      dtRuleDef5.getDimensionNode(0).setValues("Senior");
      dtRuleDef5.getDimensionNode(1).setValues("UNKNOWN");

      if (!RuleUtil.updateRuleDictionary(dict))
      {
        System.out.println("Dictionary update failed.");
      }
      else
      {
        System.out.println("Updated dictionary");
      }

      Expression expr5 = dtRuleDef5.getDTActionNode(0).getExpressionByDTActionParameterName("name");
      dtRuleDef5.getDTActionNode(0).setActionSelected(true);
      if (expr5 != null)
      {
        expr5.setValue("\"PROD_TYPE0\"");       
      }

      System.out.println("Rule table now contains " + dt.getDTRuleTable().size() + " rules");
    }
  }

  public static BucketSet addAgeBucketSet(RuleDictionary dictionary)
    throws Exception
  {
    DataModel model = dictionary.getDataModel();

    // Create a new bucketset holding product weight descriptions
    BucketSetTable bucketSetTable = model.getBucketSetTable();
    BucketSet ageBucketSet = bucketSetTable.getByName("Age");

    if (ageBucketSet != null)
    {
      System.out.println("Age BucketSet already exists, skipping add");
    }
    else
    {
      ageBucketSet = bucketSetTable.add();
      ageBucketSet.setName("Age");
      ageBucketSet.setForm(BucketSet.FORM_RANGE);
      ageBucketSet.setType("int");

      ageBucketSet.add("<0").setAlias("Minor");
      ageBucketSet.add("<18").setAlias("Junior");
      ageBucketSet.add("<50").setAlias("Middle");
      ageBucketSet.add("<75").setAlias("Senior");

    }
    return ageBucketSet;
  }

  public static DecisionFunction createDecisionFunction(RuleDictionary dictionary, String dfName, String ftName, boolean isWebService,
                                                        boolean isPAStyle)
    throws SDKException
  {
    return MetadataHelper.createDecisionFunction(dictionary, dfName, ftName, isWebService, isPAStyle);
  }

  public static void addDecisionFunctionOutput(DecisionFunction df)
    throws SDKException
  {

    MetadataHelper.addDecisionFunctionOutput(df, new MetadataHelper.DFParm(("output"), "com.sella.facts.Product", false, false));
  }
}


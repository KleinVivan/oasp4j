package io.oasp.gastronomy.restaurant.general;

import javax.inject.Named;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

/**
 * TODO VMUSCHTE This type ...
 *
 * @author VMUSCHTE
 */

@Named
public class ProcessMealOrder implements JavaDelegate {

  @Override
  public void execute(DelegateExecution execution) throws Exception {

    System.out.println("Spring Bean invoked.");

    String orderID = (String) execution.getVariable("OrderID");
    String customer = (String) execution.getVariable("Customer");

    String message = "New Order with ID: " + orderID + ". Customer is " + customer + "!";
    // execution.setVariable("message", message);
    System.out.println(message);
  }

}

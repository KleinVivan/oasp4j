package io.oasp.gastronomy.restaurant.general;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * TODO VMUSCHTE This type ...
 *
 * @author VMUSCHTE
 */

@Component
public class ProcessMealOrder implements JavaDelegate {

  @Override
  public void execute(DelegateExecution execution) throws Exception {

    System.out.println("Spring Bean invoked.");

    // String orderID = (String) execution.getVariable("OrderID");
    // String customer = (String) execution.getVariable("Customer");
    //
    // String message = "New Order with ID: " + orderID + ". Customer is " + customer + "!";
    // // execution.setVariable("message", message);
    // System.out.println(message);
  }

  public String getTableId(DelegateExecution execution) {

    String orderId = (String) execution.getVariable("orderId");

    // get Table where oderId = orderId ...
    String tableId = "102";

    return tableId;
  }

  /**
   * Dummy Method
   *
   * @return
   */
  public String dummyMethod() {

    Mocky mockyDb = new Mocky();
    String daten = mockyDb.getMockyData();
    return daten + "Test";
  }

}

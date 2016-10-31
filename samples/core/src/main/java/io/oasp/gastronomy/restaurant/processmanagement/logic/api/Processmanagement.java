package io.oasp.gastronomy.restaurant.processmanagement.logic.api;

import io.oasp.gastronomy.restaurant.processmanagement.common.api.datatype.ProcessKeyName;

/**
 * TODO VMUSCHTE This type ...
 *
 * @author VMUSCHTE
 * @since dev
 */
public interface Processmanagement {

  public void startProcess(ProcessKeyName processKeyName, Long orderId);

  // public boolean stopProcess(Long orderProcessId);

  // public OrderProcessState getOrderProcessState(Long orderProcessId);

}

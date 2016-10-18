package io.oasp.gastronomy.restaurant.processmanagement.logic.api;

import io.oasp.gastronomy.restaurant.processmanagement.common.api.datatype.OrderProcessState;

/**
 * TODO VMUSCHTE This type ...
 *
 * @author VMUSCHTE
 * @since dev
 */
public interface Processmanagement {

  public boolean startProcess(Long orderProcessId);

  public boolean stopProcess(Long orderProcessId);

  public OrderProcessState getOrderProcessState(Long orderProcessId);

}

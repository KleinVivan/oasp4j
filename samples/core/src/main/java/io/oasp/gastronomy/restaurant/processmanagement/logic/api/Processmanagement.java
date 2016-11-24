package io.oasp.gastronomy.restaurant.processmanagement.logic.api;

import java.util.Map;

import io.oasp.gastronomy.restaurant.processmanagement.common.api.datatype.ProcessKeyName;

/**
 * TODO VMUSCHTE This type ...
 *
 * @author VMUSCHTE
 * @since dev
 */
public interface Processmanagement {

  // public String startProcess(ProcessKeyName processKeyName, Map<String, Object> variables);

  public String startProcess(String processEngineKey, ProcessKeyName processKeyName, Map<String, Object> variables);

  // public void startProcess(ProcessKeyName processKeyName, Long orderId, Long oderPositionId);

  // public boolean stopProcess(Long orderProcessId);

  // public OrderProcessState getOrderProcessState(Long orderProcessId);

  // public void startOrderProcess(OrderEto order, OrderPositionEto orderPosition);

  // public void updateOrderProcessState(OrderPositionState state, Long orderId, Long oderPositionId);

}

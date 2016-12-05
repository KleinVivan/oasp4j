package io.oasp.gastronomy.restaurant.processmanagement.logic.api;

import java.util.Map;

import org.camunda.bpm.engine.runtime.ProcessInstance;

/**
 * TODO VMUSCHTE This type ...
 *
 * @author VMUSCHTE
 * @since dev
 */
public interface Processmanagement {

  // public String startProcess(ProcessKeyName processKeyName, Map<String, Object> variables);

  public ProcessInstance startProcess(String processKeyName, String businessKey, Map<String, Object> variables);

  public void stopProcess(String processInstanceId, String deleteReason);

  public void setAssigneeToCurrentTask(ProcessInstance processInstance, String assignee);

  public void completeCurrentTask(ProcessInstance processInstance, Map<String, Object> variables);

  // public void startProcess(ProcessKeyName processKeyName, Long orderId, Long oderPositionId);

  // public boolean stopProcess(Long orderProcessId);

  // public OrderProcessState getOrderProcessState(Long orderProcessId);

  // public void startOrderProcess(OrderEto order, OrderPositionEto orderPosition);

  // public void updateOrderProcessState(OrderPositionState state, Long orderId, Long oderPositionId);

}

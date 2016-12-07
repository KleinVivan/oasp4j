package io.oasp.gastronomy.restaurant.processmanagement.logic.impl;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Component;

import io.oasp.gastronomy.restaurant.processmanagement.common.api.datatype.ProcessKeyName;
import io.oasp.gastronomy.restaurant.salesmanagement.common.api.datatype.OrderPositionState;
import io.oasp.gastronomy.restaurant.salesmanagement.logic.api.Salesmanagement;

/**
 * TODO VMUSCHTE This type ...
 *
 * @author VMUSCHTE
 * @since dev
 */
@Component
public class OrderProcessmanagementImpl extends ProcessmanagementImpl {

  @Inject
  Salesmanagement salesmanagement;

  Map<String, Object> variables = new HashMap<String, Object>();

  public ProcessInstance startOrderProcess(ProcessKeyName processKeyName, Long orderId, Long orderPositionId) {

    this.variables.put("orderId", orderId);
    this.variables.put("orderPositionId", orderPositionId);
    this.variables.put("orderProcessState", OrderPositionState.ORDERED.name());

    String businessKey = "BK_" + orderId + "_" + orderPositionId;

    ProcessInstance processInstance = startProcess(processKeyName.getKeyName(), businessKey, this.variables);
    return processInstance;
  }

  public void acceptOrder(ProcessInstance processInstance) {

    Map<String, Object> variables = new HashMap();
    variables.put("orderProcessState", OrderPositionState.ACCEPTED.name());
    completeCurrentTask(processInstance, variables);
  }

  public void updateOrderPrepared(ProcessInstance processInstance) {

    // Check if order has been accepted first --> create check method

    Map<String, Object> variables = new HashMap();
    variables.put("orderProcessState", OrderPositionState.PREPARED.name());
    completeCurrentTask(processInstance, variables);
  }

  public ProcessInstance getOrderProcess(Long orderId, Long orderPositionId) {

    // this.variables.put("orderId", orderId);
    // this.variables.put("orderPositionId", orderPositionId);

    ProcessInstance processInstance = this.processEngine.getRuntimeService().createProcessInstanceQuery()
        .variableValueEquals("orderId", orderId).variableValueEquals("orderPositionId", orderPositionId).singleResult();

    // List<ProcessInstance> processInstances;
    // Long processOrderPosition = null;
    // ProcessInstance rightInstance = null;
    // for (int i = 0; i < processInstances.size(); i++) {
    // processOrderPosition = (Long) this.runtimeService.getVariable(processInstances.get(i).getId(),
    // "orderPositionId");
    // if (processOrderPosition == oderPositionId) {
    // rightInstance = processInstances.get(i);
    // }
    // }

    return processInstance;
    // return processInstance = this.processEngine.getRuntimeService().createProcessInstanceQuery()
    // .processInstanceBusinessKey("BK_" + orderId + "_" + orderPositionId).singleResult();
  }

  public void updateOrderProcessState(OrderPositionState state, ProcessInstance processInstance) {

    this.processEngine.getRuntimeService().setVariable(processInstance.getProcessInstanceId(), "orderProcessState",
        state.name());
  }

  // public void completeCurrentTask(Long orderId, Long orderPositionId) {
  //
  // Task task = this.processEngine.getTaskService().createTaskQuery()
  // .processInstanceId(getOrderProcess(orderId, orderPositionId).getProcessInstanceId()).singleResult();
  //
  // OrderPositionState state;
  // String taskName = task.getName();
  //
  // switch (taskName) {
  // case "UserTask_AcceptOrder":
  // state = OrderPositionState.ACCEPTED;
  // break;
  // case "UserTask_UpdatePreparedOrder":
  // state = OrderPositionState.PREPARED;
  // break;
  // case "UserTask_UpdateServedOrder":
  // state = OrderPositionState.DELIVERED;
  // default:
  // state = OrderPositionState.ORDERED;
  // break;
  // }
  //
  // updateOrderProcessState(state, getOrderProcess(orderId, orderPositionId));
  //
  // this.processEngine.getRuntimeService().TaskService().complete(task.getId());
  //
  // }

}

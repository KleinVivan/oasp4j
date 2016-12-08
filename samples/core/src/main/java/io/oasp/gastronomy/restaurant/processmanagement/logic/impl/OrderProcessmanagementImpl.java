package io.oasp.gastronomy.restaurant.processmanagement.logic.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Component;

import io.oasp.gastronomy.restaurant.processmanagement.common.api.datatype.ProcessKeyName;
import io.oasp.gastronomy.restaurant.salesmanagement.common.api.datatype.OrderPositionState;
import io.oasp.gastronomy.restaurant.salesmanagement.logic.api.Salesmanagement;
import io.oasp.gastronomy.restaurant.salesmanagement.logic.api.to.OrderEto;
import io.oasp.gastronomy.restaurant.salesmanagement.logic.api.to.OrderPositionEto;
import io.oasp.gastronomy.restaurant.staffmanagement.logic.api.Staffmanagement;
import io.oasp.gastronomy.restaurant.staffmanagement.logic.api.to.StaffMemberEto;

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

  @Inject
  Staffmanagement staff;

  Map<String, Object> variables = new HashMap<String, Object>();

  public ProcessInstance startOrderProcessAndSave(ProcessKeyName processKeyName, OrderEto order,
      OrderPositionEto orderPosition) {

    OrderEto savedOrder = this.salesmanagement.saveOrder(order);
    Long savedOrderId = savedOrder.getId();
    OrderPositionEto savedOrderPosition = orderPosition;
    savedOrderPosition.setOrderId(savedOrderId);
    savedOrderPosition = this.salesmanagement.saveOrderPosition(orderPosition);
    Long savedOrderPositionId = savedOrderPosition.getId();

    this.variables.put("orderId", savedOrderId);
    this.variables.put("orderPositionId", savedOrderPositionId);
    this.variables.put("orderProcessState", OrderPositionState.ORDERED.name());

    String businessKey = "BK_" + savedOrderId + "_" + savedOrderPositionId;

    ProcessInstance processInstance = startProcess(processKeyName.getKeyName(), businessKey, this.variables);
    return processInstance;
  }

  public ProcessInstance startOrderProcess(ProcessKeyName processKeyName, Long orderId, Long orderPositionId) {

    this.variables.put("orderId", orderId);
    this.variables.put("orderPositionId", orderPositionId);
    this.variables.put("orderProcessState", OrderPositionState.ORDERED.name());

    String businessKey = "BK_" + orderId + "_" + orderPositionId;

    ProcessInstance processInstance = startProcess(processKeyName.getKeyName(), businessKey, this.variables);
    return processInstance;
  }

  public void assignCook(ProcessInstance processInstance, Long cookId) {

    Long orderPositionId =
        (Long) this.processEngine.getRuntimeService().getVariable(processInstance.getId(), "orderPositionId");
    OrderPositionEto orderPosition = this.salesmanagement.findOrderPosition(orderPositionId);
    orderPosition.setCookId(cookId);
    orderPosition.setState(OrderPositionState.ACCEPTED);
    this.salesmanagement.saveOrderPosition(orderPosition);

    // a cook needs to be assigned to the updateOrderPrepared task, after an order has been accepted to be prepared by
    // the cook in the task list
    List<Task> tasks = this.processEngine.getTaskService().createTaskQuery().list();
    int count = tasks.size();

    StaffMemberEto staffMem = this.staff.findStaffMember(cookId);
    staffMem.getName();
    setAssigneeToCurrentTask(processInstance, staffMem.getName());

    Task task = this.processEngine.getTaskService().createTaskQuery()
        .processInstanceId(processInstance.getProcessInstanceId()).singleResult();

  }

  public void acceptOrder(ProcessInstance processInstance) {

    Map<String, Object> variables = new HashMap();
    variables.put("orderProcessState", OrderPositionState.ACCEPTED.name());

    Long orderPositionId =
        (Long) this.processEngine.getRuntimeService().getVariable(processInstance.getId(), "orderPositionId");
    OrderPositionEto orderPosition = this.salesmanagement.findOrderPosition(orderPositionId);
    orderPosition.setState(OrderPositionState.ACCEPTED);
    this.salesmanagement.saveOrderPosition(orderPosition);

    completeCurrentTask(processInstance, variables);
    assignCook(processInstance, 1L);

  }

  public void updateOrderPrepared(ProcessInstance processInstance) {

    // Check if order has been accepted first --> create check method

    Map<String, Object> variables = new HashMap();
    variables.put("orderProcessState", OrderPositionState.PREPARED.name());
    completeCurrentTask(processInstance, variables);

    // modify orderposition

  }

  public void updateOrderServed(ProcessInstance processInstance) {

    // Check if order has been prepared first --> create check method

    Map<String, Object> variables = new HashMap();
    variables.put("orderProcessState", OrderPositionState.DELIVERED.name());
    completeCurrentTask(processInstance, variables);

    // modify orderposition
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

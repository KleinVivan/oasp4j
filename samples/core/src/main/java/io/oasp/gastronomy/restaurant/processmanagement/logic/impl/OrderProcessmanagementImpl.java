package io.oasp.gastronomy.restaurant.processmanagement.logic.impl;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Component;

import io.oasp.gastronomy.restaurant.processmanagement.common.api.datatype.ProcessKeyName;
import io.oasp.gastronomy.restaurant.salesmanagement.common.api.datatype.OrderPositionState;

/**
 * TODO VMUSCHTE This type ...
 *
 * @author VMUSCHTE
 * @since dev
 */
@Component
public class OrderProcessmanagementImpl extends ProcessmanagementImpl {

  Map<String, Object> variables = new HashMap<String, Object>();

  public ProcessInstance startOrderProcess(ProcessKeyName processKeyName, Long orderId, Long orderPositionId) {

    this.variables.put("orderId", orderId);
    this.variables.put("orderPositionId", orderPositionId);
    this.variables.put("orderProcessState", OrderPositionState.ORDERED.name());

    String businessKey = "BK_" + orderId + "_" + orderPositionId;

    ProcessInstance processInstance = startProcess("orderProcessEngine", processKeyName, businessKey, this.variables);
    return processInstance;
  }

  public ProcessInstance getOrderProcess(Long orderId, Long orderPositionId) {

    // List<ProcessInstance> processInstances
    // ProcessInstanceQuery piq = new ProcessInstanceQuery();

    // this.variables.put("orderId", orderId);
    // this.variables.put("orderPositionId", orderPositionId);

    ProcessInstance processInstance = this.processEngine.getRuntimeService().createProcessInstanceQuery()
        .variableValueEquals("orderId", orderId).variableValueEquals("orderPositionId", orderPositionId).singleResult();

    // Long processOrderPosition = null;
    // ProcessInstance rightInstance = null;
    // for (int i = 0; i < processInstances.size(); i++) {
    // processOrderPosition = (Long) this.runtimeService.getVariable(processInstances.get(i).getId(),
    // "orderPositionId");
    // if (processOrderPosition == oderPositionId) {
    // rightInstance = processInstances.get(i);
    // }
    // }

    return processInstance = this.processEngine.getRuntimeService().createProcessInstanceQuery()
        .processInstanceBusinessKey("BK_" + orderId + "_" + orderPositionId).singleResult();
  }

  public void updateOrderProcessState(OrderPositionState state, ProcessInstance processInstance) {

    this.processEngine.getRuntimeService().setVariable(processInstance.getProcessInstanceId(), "orderProcessState",
        state.name());
  }

  public void setAssigneeToTask(String assignee, Long orderId, Long orderPositionId) {

    Task task = this.processEngine.getTaskService().createTaskQuery()
        .processInstanceId(getOrderProcess(orderId, orderPositionId).getProcessInstanceId()).singleResult();
    task.setAssignee(assignee);

    String sign = task.getAssignee();
  }

  // /**
  // * sets the assignee for the current task
  // *
  // * @param taskId the ID of the current task
  // * @param modelInstance the BPMN model instance
  // */
  // public void setAssigneeToTask(String taskId, BpmnModelInstance modelInstance) {
  //
  // }

  public void completeCurrentTask(Long orderId, Long orderPositionId) {

    Task task = this.processEngine.getTaskService().createTaskQuery()
        .processInstanceId(getOrderProcess(orderId, orderPositionId).getProcessInstanceId()).singleResult();

    OrderPositionState state;
    String taskName = task.getName();

    switch (taskName) {
    case "UserTask_AcceptOrder":
      state = OrderPositionState.ACCEPTED;
      break;
    case "UserTask_UpdatePreparedOrder":
      state = OrderPositionState.PREPARED;
      break;
    case "UserTask_UpdateServedOrder":
      state = OrderPositionState.DELIVERED;
    default:
      state = OrderPositionState.ORDERED;
      break;
    }

    updateOrderProcessState(state, getOrderProcess(orderId, orderPositionId));

    this.processEngine.getTaskService().complete(task.getId());

  }

}

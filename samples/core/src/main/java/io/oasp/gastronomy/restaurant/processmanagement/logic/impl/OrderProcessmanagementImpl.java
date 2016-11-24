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

  public String startOrderProcess(ProcessKeyName processKeyName, Long orderId, Long orderPositionId) {

    this.variables.put("orderId", orderId);
    this.variables.put("orderPositionId", orderPositionId);
    this.variables.put("orderProcessState", OrderPositionState.ORDERED);

    String processInstanceId = startProcess("orderProcessEngine", processKeyName, this.variables);
    return processInstanceId;
  }

  public String getOrderProcess(Long orderId, Long orderPositionId) {

    // List<ProcessInstance> processInstances
    // ProcessInstanceQuery piq = new ProcessInstanceQuery();

    ProcessInstance rightInstance = this.processEngine.getRuntimeService().createProcessInstanceQuery()
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

    return rightInstance.getId();
  }

  public void updateOrderProcessState(OrderPositionState state, Long orderId, Long orderPositionId) {

    String processInstanceId = getOrderProcess(orderId, orderPositionId);
    this.processEngine.getRuntimeService().setVariable(processInstanceId, "orderProcessState", state);
  }

  public void setAssigneeToTask(String assignee, Long orderId, Long orderPositionId) {

    Task task = this.processEngine.getTaskService().createTaskQuery()
        .processInstanceId(getOrderProcess(orderId, orderPositionId)).singleResult();
    task.setAssignee(assignee);
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
  //
  // public void completeTask(String variableName, String variableValue) {
  //
  // Map<String, Object> variables = new HashMap<String, Object>();
  // variables.put(variableName, variableValue);
  //
  // this.taskService.complete(this.taskService.createTaskQuery().singleResult().getId(), variables);
  // }

}

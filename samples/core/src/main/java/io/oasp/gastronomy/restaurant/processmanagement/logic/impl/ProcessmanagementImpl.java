package io.oasp.gastronomy.restaurant.processmanagement.logic.impl;

import java.util.Map;

import javax.inject.Inject;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Component;

import io.oasp.gastronomy.restaurant.processmanagement.common.api.datatype.ProcessKeyName;
import io.oasp.gastronomy.restaurant.processmanagement.logic.api.Processmanagement;

/**
 * TODO VMUSCHTE This type ...
 *
 * @author VMUSCHTE
 * @since dev
 */
@Component
abstract class ProcessmanagementImpl implements Processmanagement {

  @Inject
  protected ProcessEngine processEngine;
  // private Map<String, ProcessEngine> processEngines = ProcessEngines.getProcessEngines();

  @Override
  public ProcessInstance startProcess(String processEngineKey, ProcessKeyName processKeyName, String businessKey,
      Map<String, Object> variables) {

    // System.out.println("Prozessengines Anzahl " + this.processEngines.size());
    // ProcessInstance processInstance = this.processEngines.get(processEngineKey).getRuntimeService()
    // .startProcessInstanceByKey(processKeyName.getKeyName(), variables);
    ProcessInstance processInstance = this.processEngine.getRuntimeService()
        .startProcessInstanceByKey(processKeyName.getKeyName(), businessKey, variables);
    // VariableInstance varInst = this.processEngine.getRuntimeService().createVariableInstanceQuery()
    // .variableValueEquals("orderId", variables.get("orderId"))
    // .variableValueEquals("orderPositionId", variables.get("orderPositionId")).singleResult();
    // return processInstance.getProcessInstanceId();
    return processInstance;
  }

  // public ProcessInstance getOrderProcess(Map<String, Object> variables) {
  //
  // ProcessInstance processInstance = this.processEngine.getRuntimeService().createProcessInstanceQuery()
  // .variableValueEquals("orderId", orderId).variableValueEquals("orderPositionId", orderPositionId).singleResult();
  //
  // return processInstance;
  // }

  //
  // /*
  // * save order via salesmanagement and start process afterwards therefore must remove the start of a process from
  // * saveOrderPosition in UcManageOrderPosition!
  // */
  // @Override
  // public void startOrderProcess(OrderEto order, OrderPositionEto orderPosition) {
  //
  // // OrderEto order = null;
  // // OrderPositionEto orderPosition = null;
  // this.salesManagement.saveOrder(order);
  // this.salesManagement.saveOrderPosition(orderPosition);
  //
  // Map<String, Object> variables = new HashMap<String, Object>();
  // variables.put("orderId", order.getId());
  // variables.put("orderPositionId", orderPosition.getId());
  // ProcessInstance processInstance =
  // this.runtimeService.startProcessInstanceByKey(ProcessKeyName.STANDARD_ORDER_PROCESS.getKeyName(), variables);
  // }
  //
  // @Override
  // public void updateOrderProcessState(OrderPositionState state, Long orderId, Long oderPositionId) {
  //
  // // updating OrderPosition happens in saveOrderPositon method ...
  // // then update the state in process variable ...
  //
  // List<ProcessInstance> processInstances =
  // this.runtimeService.createProcessInstanceQuery().variableValueEquals("orderId", orderId).list();
  //
  // Long processOrderPosition = null;
  // ProcessInstance rightInstance = null;
  // for (int i = 0; i < processInstances.size(); i++) {
  // processOrderPosition = (Long) this.runtimeService.getVariable(processInstances.get(i).getId(), "orderPositionId");
  // if (processOrderPosition == oderPositionId) {
  // rightInstance = processInstances.get(i);
  // }
  // }
  //
  // this.runtimeService.setVariable(rightInstance.getProcessInstanceId(), "orderProcessState", state);
  // }
  //
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

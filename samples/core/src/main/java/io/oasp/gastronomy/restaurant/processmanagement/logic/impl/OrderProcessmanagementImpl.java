package io.oasp.gastronomy.restaurant.processmanagement.logic.impl;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.oasp.gastronomy.restaurant.processmanagement.common.api.datatype.ProcessKeyName;
import io.oasp.gastronomy.restaurant.processmanagement.common.api.datatype.ProcessTasks;
import io.oasp.gastronomy.restaurant.salesmanagement.common.api.datatype.OrderPositionState;
import io.oasp.gastronomy.restaurant.salesmanagement.logic.api.Salesmanagement;
import io.oasp.gastronomy.restaurant.salesmanagement.logic.api.to.OrderEto;
import io.oasp.gastronomy.restaurant.salesmanagement.logic.api.to.OrderPositionEto;
import io.oasp.gastronomy.restaurant.staffmanagement.logic.api.Staffmanagement;
import io.oasp.gastronomy.restaurant.staffmanagement.logic.api.to.StaffMemberEto;

/**
 * @author vmuschter
 */
@Component
@Named
public class OrderProcessmanagementImpl extends ProcessmanagementImpl {

  /** Logger instance. */
  private static final Logger LOG = LoggerFactory.getLogger(OrderProcessmanagementImpl.class);

  @Inject
  Salesmanagement salesmanagement;

  @Inject
  Staffmanagement staff;

  Map<String, Object> variables = new HashMap<String, Object>();

  /**
   * Saves a new order with orderposition to the database and starts a new instance of the order process with the
   * returned orderId and orderPositionId afterwards. The process definition (the model) to be executed is selected by
   * the specific key of the order process model.
   *
   * @param processKeyName
   * @param order
   * @param orderPosition
   * @return
   */
  public ProcessInstance startOrderProcess(ProcessKeyName processKeyName, OrderEto order,
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

  /**
   * A cookId is set to an orderposition and is set as assignee for the current user task that has to be done.
   *
   * @param processInstance
   * @param cookId
   */
  public void assignCook(ProcessInstance processInstance, Long cookId) {

    Long orderPositionId =
        (Long) this.processEngine.getRuntimeService().getVariable(processInstance.getId(), "orderPositionId");
    OrderPositionEto orderPosition = this.salesmanagement.findOrderPosition(orderPositionId);
    orderPosition.setCookId(cookId);
    this.salesmanagement.saveOrderPosition(orderPosition);

    // a cook needs to be assigned to the updateOrderPrepared task, after an order has been accepted to be prepared by
    // the cook in the task list
    StaffMemberEto staffMem = this.staff.findStaffMember(cookId);
    staffMem.getName();
    setAssigneeToCurrentTask(processInstance, staffMem.getName());
  }

  /**
   * An order will be accepted for preparation (the user task will be completed), the process state will be updated and
   * the cook will be assigned to the following user task.
   *
   * @param processInstance
   */
  public void acceptOrder(ProcessInstance processInstance) {

    Map<String, Object> variables = new HashMap();
    variables.put("orderProcessState", OrderPositionState.ACCEPTED.name());

    /*
     * should set the new order position state ACCEPTED for order position, but need to modify
     * verifyOrderPositionStateChange and verifyDrinkStateChange in UcManageOrderPositionImpl first - left out by now
     *
     */
    // Long orderPositionId =
    // (Long) this.processEngine.getRuntimeService().getVariable(processInstance.getId(), "orderPositionId");
    // OrderPositionEto orderPosition = this.salesmanagement.findOrderPosition(orderPositionId);
    // orderPosition.setState(OrderPositionState.ACCEPTED);
    // this.salesmanagement.saveOrderPosition(orderPosition);

    completeCurrentTask(processInstance, variables);
    assignCook(processInstance, 1L);

  }

  /**
   * An order will be marked as prepared (the user task will be completed) and the process state will be updated.
   *
   * @param processInstance
   */
  public void updateOrderPrepared(ProcessInstance processInstance) {

    // Check if order has been accepted first
    // (later on with the use of task forms this should not be necessary any longer because
    // the task is only created when the previous one has been completed and the current task is known through the
    // context of the task form that submits the completion-call)
    Task checkTask =
        checkPreviousTaskIsComplete(processInstance.getId(), ProcessTasks.USERTASK_ACCEPTORDER.getTaskName());
    if (checkTask == null) {

      Map<String, Object> variables = new HashMap();
      variables.put("orderProcessState", OrderPositionState.PREPARED.name());
      completeCurrentTask(processInstance, variables);

      // modify order position state
      Long orderPositionId =
          (Long) this.processEngine.getRuntimeService().getVariable(processInstance.getId(), "orderPositionId");
      OrderPositionEto orderPosition = this.salesmanagement.findOrderPosition(orderPositionId);
      orderPosition.setState(OrderPositionState.PREPARED);
      this.salesmanagement.saveOrderPosition(orderPosition);
    } else {
      LOG.error("Unfinished task {}", checkTask);
    }

  }

  /**
   * An order will be marked as served/delivered (the user task will be completed) and the process state will be
   * updated.
   *
   * @param processInstance
   */
  public void updateOrderServed(ProcessInstance processInstance) {

    // Check if order has been prepared first
    Task checkTask =
        checkPreviousTaskIsComplete(processInstance.getId(), ProcessTasks.USERTASK_UPDATEPREPAREDORDER.getTaskName());
    if (checkTask == null) {

      Map<String, Object> variables = new HashMap();
      variables.put("orderProcessState", OrderPositionState.DELIVERED.name());
      completeCurrentTask(processInstance, variables);

      // modify order position state
      Long orderPositionId =
          (Long) this.processEngine.getRuntimeService().getVariable(processInstance.getId(), "orderPositionId");
      OrderPositionEto orderPosition = this.salesmanagement.findOrderPosition(orderPositionId);
      orderPosition.setState(OrderPositionState.DELIVERED);

      this.salesmanagement.saveOrderPosition(orderPosition);
    } else {
      LOG.error("Unfinished task {}", checkTask);
    }
  }

  /**
   * This method is used to handle the incoming message/request for the bill on which the process is waiting on.
   *
   * @param processInstance
   */
  public void handleBillRequest(ProcessInstance processInstance) {

    this.processEngine.getRuntimeService().correlateMessage("Message_Bill", processInstance.getBusinessKey());
  }

  /**
   * This method is called automatically by the corresponding service task in the bpmn model
   *
   * @param delegateExecution
   */
  public void calculateBill(DelegateExecution delegateExecution) {

    // get process execution information via delegateExecution
    Long orderPositionId = (Long) delegateExecution.getVariable("orderPositionId");

    // TODO calls/logic for creating bill
    System.out.println("Calculating and creating bill");
  }

  /**
   * This will return an active task of the given task name for the given process instance. It will return null if the
   * task has already been completed (or not yet been created)
   *
   * @param processInstanceId
   * @param taskName
   * @return
   */
  public Task checkPreviousTaskIsComplete(String processInstanceId, String taskName) {

    Task task = this.processEngine.getTaskService().createTaskQuery().processInstanceId(processInstanceId)
        .taskDefinitionKey(taskName).active().singleResult();
    return task;

  }

}

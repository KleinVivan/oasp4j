package io.oasp.gastronomy.restaurant.processmanagement.logic.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.assertions.ProcessEngineTests;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import io.oasp.gastronomy.restaurant.SpringBootApp;
import io.oasp.gastronomy.restaurant.general.common.DbTestHelper;
import io.oasp.gastronomy.restaurant.general.common.TestUtil;
import io.oasp.gastronomy.restaurant.general.common.api.constants.PermissionConstants;
import io.oasp.gastronomy.restaurant.processmanagement.common.api.datatype.ProcessKeyName;
import io.oasp.gastronomy.restaurant.processmanagement.common.api.datatype.ProcessTasks;
import io.oasp.gastronomy.restaurant.salesmanagement.common.api.datatype.OrderPositionState;
import io.oasp.gastronomy.restaurant.salesmanagement.logic.api.Salesmanagement;
import io.oasp.gastronomy.restaurant.salesmanagement.logic.api.to.OrderEto;
import io.oasp.gastronomy.restaurant.salesmanagement.logic.api.to.OrderPositionEto;
import io.oasp.gastronomy.restaurant.salesmanagement.logic.impl.SalesManagementTest;
import io.oasp.gastronomy.restaurant.staffmanagement.logic.api.Staffmanagement;
import io.oasp.module.test.common.base.ComponentTest;

/**
 * This is the test-case of {@link OrderProcessmanagement}.
 *
 * @author vmuschter
 */
@SpringApplicationConfiguration(classes = { SpringBootApp.class })
@WebAppConfiguration
public class OrderProcessManagementTest extends ComponentTest {

  @Inject
  private Salesmanagement salesManagement;

  @Inject
  private Staffmanagement staffManagement;

  @Inject
  private DbTestHelper dbTestHelper;

  @Inject
  private RuntimeService runtimeService;

  @Inject
  private RepositoryService repositoryService;

  @Inject
  private ProcessEngine processEngine;

  private SalesManagementTest salesmanagementTest;

  private OrderProcessmanagementImpl orderProcessmanagement;

  // private List<ProcessInstance> processInstancesList = new ArrayList<ProcessInstance>();

  /**
   * @param orderProcessmanagement new value of {@link Inject}.
   */
  @Inject
  public void setOrderProcessmanagement(OrderProcessmanagementImpl orderProcessmanagement) {

    this.orderProcessmanagement = orderProcessmanagement;
  }

  /**
   * Initialization for the test.
   */
  @Before
  public void setUp() {

    TestUtil.login("waiter", PermissionConstants.FIND_ORDER, PermissionConstants.FIND_ORDER_POSITION,
        PermissionConstants.SAVE_ORDER_POSITION, PermissionConstants.SAVE_ORDER, PermissionConstants.FIND_OFFER,
        PermissionConstants.FIND_STAFF_MEMBER);
    this.dbTestHelper.setMigrationVersion("0002");
    // this.dbTestHelper.resetDatabase();

    this.salesmanagementTest = new SalesManagementTest();
    this.salesmanagementTest.setSalesManagement(this.salesManagement);
    // start a first instance of a standard_order_process
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("orderId", 2L);
    variables.put("orderPositionId", 999L);
    // ProcessInstance processInstance =
    // this.runtimeService.startProcessInstanceByKey(ProcessKeyName.STANDARD_ORDER_PROCESS.getKeyName(), variables);

    ProcessInstance firstInstance = this.runtimeService.startProcessInstanceByKey(
        ProcessKeyName.STANDARD_ORDER_PROCESS.getKeyName(), "BK_" + 2 + "_" + 999, variables);

  }

  /**
   * Log out utility for the test.
   */
  @After
  public void tearDown() {
    // delete all process instances

    List<ProcessInstance> processInstancesList =
        this.processEngine.getRuntimeService().createProcessInstanceQuery().active().list();

    for (ProcessInstance pi : processInstancesList) {
      this.processEngine.getRuntimeService().deleteProcessInstance(pi.getProcessInstanceId(), null);
    }

    TestUtil.logout();
  }

  @Test
  public void testStartOrderProcessAndSave() {

    OrderEto order = this.salesmanagementTest.prepareNewOrderEto();
    OrderPositionEto orderPosition = this.salesmanagementTest.prepareNewOrderPositionEto(order);

    ProcessInstance processInstance = this.orderProcessmanagement
        .startOrderProcessAndSave(ProcessKeyName.STANDARD_ORDER_PROCESS, order, orderPosition);

    String pId = processInstance.getProcessInstanceId();
    // check if process variables exist with correct value
    Long processOrderId = (Long) this.runtimeService.getVariable(pId, "orderId");
    assertThat(processOrderId).isNotNull();
    Long processOrderPositionId = (Long) this.runtimeService.getVariable(pId, "orderPositionId");
    assertThat(processOrderPositionId).isNotNull();

    // check that an order with the given id exists after the process is started
    OrderEto savedOrder = this.salesManagement.findOrder(processOrderId);
    assertThat(savedOrder).isNotNull();
    // check that an orderPosition with the given id exists after the process is started
    OrderPositionEto savedOrderPosition = this.salesManagement.findOrderPosition(processOrderPositionId);
    assertThat(savedOrder).isNotNull();

    ProcessEngineTests.assertThat(processInstance).isStarted();

  }

  // @Test
  // public void testStartOrderProcess() {
  //
  // try {
  // // given: an already existing (saved) order and orderposition
  // OrderEto order = this.salesmanagementTest.createNewOrderEto();
  // OrderPositionEto orderPosition = this.salesmanagementTest.createNewOrderPositionEto(order);
  // Long orderId = order.getId();
  // Long orderPositionId = orderPosition.getId();
  //
  // // start a new order process
  // ProcessInstance processInstance = this.orderProcessmanagement
  // .startOrderProcess(ProcessKeyName.STANDARD_ORDER_PROCESS, orderId, orderPositionId);
  //
  // String pId = processInstance.getProcessInstanceId();
  //
  // // check if process variables exist with correct value
  // Long processOrderId = (Long) this.runtimeService.getVariable(pId, "orderId");
  // assertThat(processOrderId).isEqualTo(orderId);
  // Long processOrderPositionId = (Long) this.runtimeService.getVariable(pId, "orderPositionId");
  // assertThat(processOrderPositionId).isEqualTo(orderPositionId);
  //
  // } catch (ConstraintViolationException e) {
  // // BV is really painful as you need such code to see the actual error in JUnit.
  // StringBuilder sb = new StringBuilder(64);
  // sb.append("Constraints violated:");
  // for (ConstraintViolation<?> v : e.getConstraintViolations()) {
  // sb.append("\n");
  // sb.append(v.getPropertyPath());
  // sb.append(":");
  // sb.append(v.getMessage());
  // }
  // throw new IllegalStateException(sb.toString(), e);
  // }
  //
  // }

  // @Test
  // public void testGetOrderProcess() {
  //
  // try {
  // // given
  // ProcessInstance processInstance = getNewProcessInstance();
  // Long orderId = (Long) this.runtimeService.getVariable(processInstance.getProcessInstanceId(), "orderId");
  // Long orderPositionId =
  // (Long) this.runtimeService.getVariable(processInstance.getProcessInstanceId(), "orderPositionId");
  //
  // String processInstanceId = processInstance.getProcessInstanceId();
  //
  // // get orderprocess
  // // ProcessInstance getProcessInstance = this.orderProcessmanagement.getOrderProcess(orderId, orderPositionId);
  // ProcessInstance getProcessInstance =
  // this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
  // assertThat(getProcessInstance).isNotNull();
  // String getProcessInstanceId = getProcessInstance.getProcessInstanceId();
  //
  // assertThat(processInstanceId).isEqualTo(getProcessInstanceId);
  //
  // List<Execution> currentExecutions =
  // this.runtimeService.createExecutionQuery().processInstanceId(processInstanceId).list();
  //
  // // assertThat(processInstance).isSameAs(getProcessInstance);
  //
  // // assertThat(this.runtimeService.getVariable(getProcessInstanceId, "orderId")).isNotNull();
  // // assertThat(this.runtimeService.getVariable(getProcessInstanceId, "orderPositionId")).isNotNull();
  //
  // // Variablenwert auslesen
  // Long getOrderId = (Long) this.runtimeService.getVariable(getProcessInstanceId, "orderId");
  // Long getOrderPositionId = (Long) this.runtimeService.getVariable(getProcessInstanceId, "orderPositionId");
  // // assert
  // assertThat(getOrderId).isEqualTo(orderId);
  // assertThat(getOrderPositionId).isEqualTo(orderPositionId);
  //
  // // check orderprocess state
  // assertThat(this.runtimeService.getVariable(getProcessInstanceId, "orderProcessState"))
  // .isEqualTo(OrderPositionState.ORDERED.name());
  //
  // // check if orderprocess is started and waiting at first task
  // ProcessEngineTests
  // .assertThat(
  // this.runtimeService.createProcessInstanceQuery().processInstanceId(getProcessInstanceId).singleResult())
  // .isStarted().isWaitingAt(ProcessTasks.USERTASK_ACCEPTORDER.getTaskName());
  //
  // // this.orderProcessmanagement.setAssigneeToTask("Carl Cook", order.getId(), orderPosition.getId());
  //
  // // ProcessEngineTests
  // // .assertThat(this.runtimeService.createProcessInstanceQuery().processInstanceId(getPI.getProcessInstanceId())
  // // .singleResult())
  // // .isStarted().isWaitingAt(ProcessTasks.USERTASK_ACCEPTORDER.getTaskName()).task().isAssignedTo("Carl Cook");
  //
  // this.orderProcessmanagement.acceptOrder(getProcessInstance);
  // // check orderprocess state
  // assertThat(this.runtimeService.getVariable(getProcessInstanceId, "orderProcessState"))
  // .isEqualTo(OrderPositionState.ACCEPTED.name());
  //
  // ProcessEngineTests
  // .assertThat(
  // this.runtimeService.createProcessInstanceQuery().processInstanceId(getProcessInstanceId).singleResult())
  // .isStarted().hasPassed(ProcessTasks.USERTASK_ACCEPTORDER.getTaskName());
  //
  // ProcessEngineTests
  // .assertThat(
  // this.runtimeService.createProcessInstanceQuery().processInstanceId(getProcessInstanceId).singleResult())
  // .isStarted().isWaitingAt(ProcessTasks.USERTASK_UPDATEPREPAREDORDER.getTaskName());
  //
  // this.orderProcessmanagement.updateOrderPrepared(getProcessInstance);
  //
  // ProcessEngineTests
  // .assertThat(
  // this.runtimeService.createProcessInstanceQuery().processInstanceId(getProcessInstanceId).singleResult())
  // .isStarted().hasPassed(ProcessTasks.USERTASK_UPDATEPREPAREDORDER.getTaskName());
  //
  // this.runtimeService.correlateMessage("Message_Ready", getProcessInstance.getBusinessKey());
  //
  // ProcessEngineTests
  // .assertThat(
  // this.runtimeService.createProcessInstanceQuery().processInstanceId(getProcessInstanceId).singleResult())
  // .isStarted().isWaitingAt(ProcessTasks.USERTASK_UPDATESERVEDORDER.getTaskName());
  //
  // } catch (ConstraintViolationException e) {
  // // BV is really painful as you need such code to see the actual error in JUnit.
  // StringBuilder sb = new StringBuilder(64);
  // sb.append("Constraints violated:");
  // for (ConstraintViolation<?> v : e.getConstraintViolations()) {
  // sb.append("\n");
  // sb.append(v.getPropertyPath());
  // sb.append(":");
  // sb.append(v.getMessage());
  // }
  // throw new IllegalStateException(sb.toString(), e);
  // }
  //
  // }

  @Test
  public void testOrderProcessFlow() {

    try {
      // given
      ProcessInstance processInstance = getNewProcessInstance();
      String processInstanceId = processInstance.getProcessInstanceId();
      Long orderId = (Long) this.runtimeService.getVariable(processInstanceId, "orderId");
      Long orderPositionId = (Long) this.runtimeService.getVariable(processInstanceId, "orderPositionId");

      // check if order process is started and waiting at accept task
      ProcessEngineTests
          .assertThat(
              this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult())
          .isStarted().isWaitingAt(ProcessTasks.USERTASK_ACCEPTORDER.getTaskName());

      // check that order process state is ordered
      assertThat(this.runtimeService.getVariable(processInstanceId, "orderProcessState"))
          .isEqualTo(OrderPositionState.ORDERED.name());

      // check that no cook has been assigned to the orderposition yet (and in that case also not to the next task)
      OrderPositionEto orderPosition = this.salesManagement
          .findOrderPosition((Long) this.runtimeService.getVariable(processInstanceId, "orderPositionId"));
      assertThat(orderPosition.getCookId()).isNull();

      // step on in the process by accepting to prepare the order, that is connected to the process
      this.orderProcessmanagement.acceptOrder(processInstance);

      // check that the user task "Accept Order" is completed
      ProcessEngineTests
          .assertThat(
              this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult())
          .isStarted().hasPassed(ProcessTasks.USERTASK_ACCEPTORDER.getTaskName());

      // check the the new process state is now "accepted" -> means in preparation
      assertThat(this.runtimeService.getVariable(processInstanceId, "orderProcessState"))
          .isEqualTo(OrderPositionState.ACCEPTED.name());

      // check that now a cook is set for the orderposition
      orderPosition = this.salesManagement
          .findOrderPosition((Long) this.runtimeService.getVariable(processInstanceId, "orderPositionId"));
      assertThat(orderPosition.getCookId()).isNotNull();

      // check that the process is now waiting at the next user task
      ProcessEngineTests
          .assertThat(
              this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult())
          .isStarted().isWaitingAt(ProcessTasks.USERTASK_UPDATEPREPAREDORDER.getTaskName());

      // check that the user task "Update Prepared Order" is now assigned to that cook
      ProcessEngineTests
          .assertThat(
              this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult())
          .isWaitingAt(ProcessTasks.USERTASK_UPDATEPREPAREDORDER.getTaskName()).task().isAssignedTo("cook");

      // step on in the process by confirming that order is prepared and ready to be served
      this.orderProcessmanagement.updateOrderPrepared(processInstance);

      // check that the update order is prepared has been made
      ProcessEngineTests
          .assertThat(
              this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult())
          .isStarted().hasPassed(ProcessTasks.USERTASK_UPDATEPREPAREDORDER.getTaskName());

      // this.runtimeService.correlateMessage("Message_Ready", processInstance.getBusinessKey());

      ProcessEngineTests
          .assertThat(
              this.runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult())
          .isStarted().isWaitingAt(ProcessTasks.USERTASK_UPDATESERVEDORDER.getTaskName());

      // complete the serving of the order
      this.orderProcessmanagement.updateOrderServed(processInstance);

    } catch (ConstraintViolationException e) {
      // BV is really painful as you need such code to see the actual error in JUnit.
      StringBuilder sb = new StringBuilder(64);
      sb.append("Constraints violated:");
      for (ConstraintViolation<?> v : e.getConstraintViolations()) {
        sb.append("\n");
        sb.append(v.getPropertyPath());
        sb.append(":");
        sb.append(v.getMessage());
      }
      throw new IllegalStateException(sb.toString(), e);
    }
    // TODO check that restaurant is in a new state
  }

  private ProcessInstance getNewProcessInstance() {

    OrderEto order = this.salesmanagementTest.prepareNewOrderEto();
    OrderPositionEto orderPosition = this.salesmanagementTest.prepareNewOrderPositionEto(order);

    // start order process
    ProcessInstance processInstance = this.orderProcessmanagement
        .startOrderProcessAndSave(ProcessKeyName.STANDARD_ORDER_PROCESS, order, orderPosition);

    // OrderEto order = this.salesmanagementTest.createNewOrderEto();
    // OrderPositionEto orderPosition = this.salesmanagementTest.createNewOrderPositionEto(order);
    // Long orderId = order.getId();
    // Long orderPositionId = orderPosition.getId();
    //
    // // start orderprocess
    // ProcessInstance processInstance =
    // this.orderProcessmanagement.startOrderProcess(ProcessKeyName.STANDARD_ORDER_PROCESS, orderId, orderPositionId);

    return processInstance;
  }

}

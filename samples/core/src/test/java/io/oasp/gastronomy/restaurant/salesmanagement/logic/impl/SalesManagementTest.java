package io.oasp.gastronomy.restaurant.salesmanagement.logic.impl;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import io.oasp.gastronomy.restaurant.SpringBootApp;
import io.oasp.gastronomy.restaurant.common.builders.OrderEtoBuilder;
import io.oasp.gastronomy.restaurant.common.builders.OrderPositionEtoBuilder;
import io.oasp.gastronomy.restaurant.general.common.DbTestHelper;
import io.oasp.gastronomy.restaurant.general.common.TestUtil;
import io.oasp.gastronomy.restaurant.general.common.api.constants.PermissionConstants;
import io.oasp.gastronomy.restaurant.general.common.api.datatype.Money;
import io.oasp.gastronomy.restaurant.processmanagement.logic.impl.OrderProcessmanagementImpl;
import io.oasp.gastronomy.restaurant.salesmanagement.common.api.datatype.OrderPositionState;
import io.oasp.gastronomy.restaurant.salesmanagement.common.api.datatype.ProductOrderState;
import io.oasp.gastronomy.restaurant.salesmanagement.logic.api.Salesmanagement;
import io.oasp.gastronomy.restaurant.salesmanagement.logic.api.to.OrderEto;
import io.oasp.gastronomy.restaurant.salesmanagement.logic.api.to.OrderPositionEto;
import io.oasp.gastronomy.restaurant.staffmanagement.logic.api.Staffmanagement;
import io.oasp.module.test.common.base.ComponentTest;

/**
 * This is the test-case of {@link Salesmanagement}.
 *
 * @author hohwille, sroeger
 */
@SpringApplicationConfiguration(classes = { SpringBootApp.class })
@WebAppConfiguration
public class SalesManagementTest extends ComponentTest {

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

  private OrderProcessmanagementImpl orderProcessmanagement;

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

    TestUtil.login("waiter", PermissionConstants.FIND_ORDER_POSITION, PermissionConstants.SAVE_ORDER_POSITION,
        PermissionConstants.SAVE_ORDER, PermissionConstants.FIND_OFFER);
    this.dbTestHelper.setMigrationVersion("0002");
    // this.dbTestHelper.resetDatabase();

    // // start a first instance of a standard_order_process
    // Map<String, Object> variables = new HashMap<String, Object>();
    // variables.put("orderId", 2L);
    // variables.put("orderPositionId", 999L);
    // // ProcessInstance processInstance =
    // // this.runtimeService.startProcessInstanceByKey(ProcessKeyName.STANDARD_ORDER_PROCESS.getKeyName(), variables);
    //
    // ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(
    // ProcessKeyName.STANDARD_ORDER_PROCESS.getKeyName(), "BK_" + 2 + "_" + 999, variables);
  }

  /**
   * Log out utility for the test.
   */
  @After
  public void tearDown() {

    TestUtil.logout();
  }

  // @Test
  // public void testStartOrderProcess() {
  //
  // try {
  // // given
  // OrderEto order = createNewOrderEto();
  // OrderPositionEto orderPosition = createNewOrderPositionEto(order);
  // assertThat(orderPosition).isNotNull();
  //
  // // start orderprocess
  // ProcessInstance pI = this.orderProcessmanagement.startOrderProcess(ProcessKeyName.STANDARD_ORDER_PROCESS,
  // order.getId(), orderPosition.getId());
  // assertThat(pI).isNotNull();
  //
  // // get orderprocess
  // ProcessInstance getPI = this.orderProcessmanagement.getOrderProcess(order.getId(), orderPosition.getId());
  // assertThat(getPI).isNotNull();
  // String getPiId = getPI.getProcessInstanceId();
  // assertThat(pI.getProcessInstanceId()).isEqualTo(getPI.getProcessInstanceId());
  //
  // assertThat(this.runtimeService.getVariable(getPI.getProcessInstanceId(), "orderId")).isNotNull();
  // assertThat(this.runtimeService.getVariable(getPI.getProcessInstanceId(), "orderPositionId")).isNotNull();
  //
  // // Variablenwert auslesen
  // // assert
  //
  // // check orderprocess state
  // assertThat(this.runtimeService.getVariable(getPI.getProcessInstanceId(), "orderProcessState"))
  // .isEqualTo(OrderPositionState.ORDERED.name());
  //
  // // check if orderprocess is started and waiting at first task
  // ProcessEngineTests.assertThat(this.runtimeService.createProcessInstanceQuery()
  // .processInstanceId(getPI.getProcessInstanceId()).singleResult()).isStarted()
  // .isWaitingAt(ProcessTasks.USERTASK_ACCEPTORDER.getTaskName());
  //
  // // this.orderProcessmanagement.setAssigneeToTask("Carl Cook", order.getId(), orderPosition.getId());
  //
  // // ProcessEngineTests
  // // .assertThat(this.runtimeService.createProcessInstanceQuery().processInstanceId(getPI.getProcessInstanceId())
  // // .singleResult())
  // // .isStarted().isWaitingAt(ProcessTasks.USERTASK_ACCEPTORDER.getTaskName()).task().isAssignedTo("Carl Cook");
  //
  // // this.orderProcessmanagement.completeCurrentTask(order.getId(), orderPosition.getId());
  // // check orderprocess state
  // assertThat(this.runtimeService.getVariable(getPI.getProcessInstanceId(), "orderProcessState"))
  // .isEqualTo(OrderPositionState.ACCEPTED.name());
  //
  // ProcessEngineTests.assertThat(this.runtimeService.createProcessInstanceQuery()
  // .processInstanceId(getPI.getProcessInstanceId()).singleResult()).isStarted()
  // .hasPassed(ProcessTasks.USERTASK_ACCEPTORDER.getTaskName());
  //
  // ProcessEngineTests.assertThat(this.runtimeService.createProcessInstanceQuery()
  // .processInstanceId(getPI.getProcessInstanceId()).singleResult()).isStarted()
  // .isWaitingAt(ProcessTasks.USERTASK_UPDATEPREPAREDORDER.getTaskName());
  //
  // // this.orderProcessmanagement.completeCurrentTask(order.getId(), orderPosition.getId());
  //
  // ProcessEngineTests.assertThat(this.runtimeService.createProcessInstanceQuery()
  // .processInstanceId(getPI.getProcessInstanceId()).singleResult()).isStarted()
  // .hasPassed(ProcessTasks.USERTASK_UPDATEPREPAREDORDER.getTaskName());
  //
  // this.runtimeService.correlateMessage("Message_Ready", getPI.getBusinessKey());
  //
  // ProcessEngineTests.assertThat(this.runtimeService.createProcessInstanceQuery()
  // .processInstanceId(getPI.getProcessInstanceId()).singleResult()).isStarted()
  // .isWaitingAt(ProcessTasks.USERTASK_UPDATESERVEDORDER.getTaskName());
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

  /**
   * @param order
   * @return
   */
  public OrderPositionEto createNewOrderPositionEto(OrderEto order) {

    OrderPositionEto orderPosition = new OrderPositionEtoBuilder().offerId(5L).orderId(order.getId()).offerName("Cola")
        .price(new Money(1.2)).createNew();
    orderPosition = this.salesManagement.saveOrderPosition(orderPosition);
    return orderPosition;
  }

  /**
   * @param order
   * @return
   */
  public OrderPositionEto prepareNewOrderPositionEto(OrderEto order) {

    OrderPositionEto orderPosition = new OrderPositionEtoBuilder().offerId(5L).orderId(order.getId()).offerName("Cola")
        .price(new Money(1.2)).createNew();
    return orderPosition;
  }

  /**
   * @return
   */
  public OrderEto createNewOrderEto() {

    OrderEto order = new OrderEtoBuilder().tableId(1L).createNew();
    order = this.salesManagement.saveOrder(order);
    return order;
  }

  /**
   * @return
   */
  public OrderEto prepareNewOrderEto() {

    OrderEto order = new OrderEtoBuilder().tableId(1L).createNew();
    return order;
  }

  /**
   * Tests if the {@link OrderPositionState} is persisted correctly. The test modifies the {@link OrderPositionState} as
   * well as the drinkState {@link ProductOrderState}. The test focuses on saving {@link OrderPositionEto} saving and
   * verification of state change. Test data is created using Cobigen generated builders.
   */
  @Test
  public void testOrderPositionStateChange() {

    try {
      OrderEto order = createNewOrderEto();
      Long orderId = order.getId();
      OrderPositionEto orderPosition = createNewOrderPositionEto(order);
      assertThat(orderPosition).isNotNull();
      orderPosition.setState(OrderPositionState.ORDERED);
      orderPosition.setDrinkState(ProductOrderState.ORDERED);

      OrderPositionEto updatedOrderPosition = this.salesManagement.saveOrderPosition(orderPosition);
      assertThat(updatedOrderPosition.getState()).isEqualTo(OrderPositionState.ORDERED);

      // when
      updatedOrderPosition.setState(OrderPositionState.PREPARED);
      updatedOrderPosition.setDrinkState(ProductOrderState.PREPARED);
      updatedOrderPosition = this.salesManagement.saveOrderPosition(updatedOrderPosition);

      // then
      assertThat(updatedOrderPosition.getState()).isEqualTo(OrderPositionState.PREPARED);

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

  }

  /**
   * @param salesManagement new value of {@link #getsalesManagement}.
   */
  public void setSalesManagement(Salesmanagement salesManagement) {

    this.salesManagement = salesManagement;
  }

}

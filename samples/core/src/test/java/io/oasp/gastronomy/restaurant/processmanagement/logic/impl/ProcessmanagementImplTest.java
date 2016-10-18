package io.oasp.gastronomy.restaurant.processmanagement.logic.impl;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.oasp.gastronomy.restaurant.processmanagement.common.api.OrderProcess;
import io.oasp.gastronomy.restaurant.processmanagement.logic.api.Processmanagement;
import io.oasp.module.beanmapping.common.api.BeanMapper;
import io.oasp.module.test.common.base.ModuleTest;

/**
 * TODO VMUSCHTE This type ...
 *
 * @author VMUSCHTE
 * @since dev
 */
public class ProcessmanagementImplTest extends ModuleTest {

  File table = null;

  OrderProcess orderProcess;

  Processmanagement processmanagement;

  /**
   * This method initializes the object {@link ProcessmanagementImpl} and assigns the mocked objects of the classes
   * {@link XXXX} and {@link BeanMapper} to the attributes of the {@link ProcessmanagementImpl} object before tests, if
   * they are not null.
   */
  @Before
  public void init() {

    assertThat(this.table.canWrite());
  }

  /**
   * This method dereferences all object after each test
   */
  @After
  public void clean() {

    // wie hinterlässt man die Tabelle?
  }

  @Test
  public void startProcessTest() {

    assertTrue(this.processmanagement.startProcess(this.orderProcess.getOrderProcessId()));

  }

  private void prepareTestData() {

    // this.orderProcess = new OrderProcessEto();
    // this.orderProcess.setOrderPositionState(OrderPositionState.ORDERED);
  }

}

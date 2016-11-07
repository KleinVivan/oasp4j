package io.oasp.gastronomy.restaurant.processmanagement.logic.impl;

import javax.inject.Inject;

import org.camunda.bpm.engine.RuntimeService;
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
public class ProcessmanagementImpl implements Processmanagement {

  @Inject
  private RuntimeService runtimeService;

  @Override
  public void startProcess(ProcessKeyName processKeyName, Long orderId) {

    ProcessInstance processInstance = this.runtimeService.startProcessInstanceByKey(processKeyName.getKeyName());
    // //this.runtimeService.createProcessInstanceQuery().singleResult().getProcessInstanceId();
    //
    // this.runtimeService.createProcessInstanceQuery().variableValueEquals(name, value)
    // System.out.println(processInstance.getProcessInstanceId());

    // neue Entity

  }

}

package io.oasp.gastronomy.restaurant.processmanagement;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;

/**
 * TODO VMUSCHTE This type ...
 *
 * @author VMUSCHTE
 * @since dev
 */
public class UserTaskListener implements TaskListener {

  @Override
  public void notify(DelegateTask delegateTask) {

    // logic for create, assign, complete, delete events goes here, e.g. setDueDate()

    delegateTask.setAssignee("Dummy User");
    System.out.println("Set assignee in TaskListener!");

  }

}

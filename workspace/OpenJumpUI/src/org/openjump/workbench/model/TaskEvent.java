package org.openjump.workbench.model;

import java.util.EventObject;

/**
 *
 * @author Matthias Scholz
 *
 * This Event will be fired if a new Task was added or loaded.
 *
 */
public class TaskEvent extends EventObject {

	private final Task task;

	/**
	 * Creates a new TaskEvent.
	 *
	 * @param source - The Source.
	 * @param task - The added or loaded Task.
	 */
	public TaskEvent(Object source, Task task) {
		super(source);
		this.task = task;
	}

	/**
	 * @return the task
	 */
	public Task getTask() {
		return task;
	}

}

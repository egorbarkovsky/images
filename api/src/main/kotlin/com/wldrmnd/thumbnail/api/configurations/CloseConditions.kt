package com.wldrmnd.thumbnail.api.configurations

import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

internal class CloseCondition {
    var LOGGER = LoggerFactory.getLogger(CloseCondition::class.java)
    var tasksSubmitted = AtomicInteger(0)
    var tasksCompleted = AtomicInteger(0)
    var allTaskssubmitted = AtomicBoolean(false)

    /**
     * notify all tasks have been subitted, determine of the file channel can be closed
     * @return true if the asynchronous file stream can be closed
     */
    fun canCloseOnComplete(): Boolean {
        allTaskssubmitted.set(true)
        return tasksCompleted.get() == tasksSubmitted.get()
    }

    /**
     * notify a task has been submitted
     */
    fun onTaskSubmitted() {
        tasksSubmitted.incrementAndGet()
    }

    /**
     * notify a task has been completed
     * @return true if the asynchronous file stream can be closed
     */
    fun onTaskCompleted(): Boolean {
        val allSubmittedClosed = tasksSubmitted.get() == tasksCompleted.incrementAndGet()
        return allSubmittedClosed && allTaskssubmitted.get()
    }
}
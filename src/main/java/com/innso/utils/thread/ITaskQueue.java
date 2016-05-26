
package com.innso.utils.thread;

public interface ITaskQueue {

    PoolTask nextTask();

    void addTask(PoolTask task);

}

package com.georgev22.library.minecraft.scheduler;

public interface SchedulerTask {

    void cancel();

    boolean isCancelled();

    int getTaskId();

}

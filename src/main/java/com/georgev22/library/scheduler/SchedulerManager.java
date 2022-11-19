package com.georgev22.library.scheduler;

public class SchedulerManager {

    private final static Scheduler scheduler = new Scheduler();

    public static Scheduler getScheduler() {
        return scheduler;
    }
}

package com.hiveel.autossav.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.hiveel.autossav.job.task.PlanTask;
import com.hiveel.core.util.ThreadUtil;

@Component
public class PlanJob {
	
    @Autowired
    private PlanTask planTask;
	
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;
	
    @Async
    @Scheduled(cron = "30 30 0 * * ?")
    public void saveInpsectionReminder3DayBefore(){
    	ThreadUtil.run(()->planTask.run());
    }

}

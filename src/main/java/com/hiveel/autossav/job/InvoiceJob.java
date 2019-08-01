package com.hiveel.autossav.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import com.hiveel.autossav.job.task.InvoiceExamTask;
import com.hiveel.autossav.job.task.InvoicePlatformTask;
import com.hiveel.core.util.ThreadUtil;

@Component
public class InvoiceJob {
	
    @Autowired
    private InvoiceExamTask invoiceExamTask;
    
    @Autowired
    private InvoicePlatformTask invoicePlatformTask;
	
    @Async
    @Scheduled(cron = "30 10 0 * * ?")
    public void saveInvoiceExam(){
    	ThreadUtil.run(()->invoiceExamTask.run());
    }
    
    @Async
    @Scheduled(cron = "30 20 0 * * ?")
    public void saveInvoicePlatform(){
    	ThreadUtil.run(()->invoicePlatformTask.run());
    }
}

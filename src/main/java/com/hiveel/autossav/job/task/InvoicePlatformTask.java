package com.hiveel.autossav.job.task;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hiveel.autossav.service.InspectionService;
import com.hiveel.autossav.service.InvoiceExamQuoteService;
import com.hiveel.autossav.service.InvoiceExamService;
import com.hiveel.autossav.service.InvoicePlatformService;
import com.hiveel.autossav.service.IssueService;
import com.hiveel.autossav.service.PersonService;
import com.hiveel.autossav.service.QuoteService;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Inspection;
import com.hiveel.autossav.model.entity.InvoiceExam;
import com.hiveel.autossav.model.entity.InvoiceExamQuote;
import com.hiveel.autossav.model.entity.InvoicePlatform;
import com.hiveel.autossav.model.entity.Issue;
import com.hiveel.autossav.model.entity.PersonType;
import com.hiveel.autossav.model.entity.Quote;


@Component
public class InvoicePlatformTask implements Runnable {

    @Autowired
    private InvoicePlatformService service;
    @Autowired
    private PersonService personService;

	public InvoicePlatformTask() {
	}

	@Override
	public void run() {
    	service.deleteAll(); 
    	savePlatformInvoice();
	}

    private void savePlatformInvoice() {
    	LocalDate firstDate = LocalDate.parse("2019-02-01");
    	LocalDate nowDate = LocalDate.now(ZoneId.of("UTC"));
    	Period period = Period.between(firstDate, nowDate);
    	for (int i = 0; i <= period.getMonths(); i++) {
    		LocalDate calDate = firstDate.plusMonths(i);
    		LocalDate maxDate = LocalDate.of(calDate.getYear(), calDate.getMonth().getValue(), calDate.lengthOfMonth());
			SearchCondition personSearchCondition = new SearchCondition();
			personSearchCondition.setLimit(0);
			personSearchCondition.setType(PersonType.DR.name());
			personSearchCondition.setMinDate(firstDate.toString()+" 00:00:00");
			personSearchCondition.setMaxDate(maxDate.toString()+" 23:59:59");
			int count = personService.count(personSearchCondition);
			InvoicePlatform invoicePlatform = new InvoicePlatform.Builder().set("price", count*3.99).set("date", maxDate.plusDays(7).toString()).build();
			service.save(invoicePlatform);
    	}
    }
}

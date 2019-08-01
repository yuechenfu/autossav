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
import com.hiveel.autossav.service.IssueService;
import com.hiveel.autossav.service.QuoteService;
import com.hiveel.autossav.model.SearchCondition;
import com.hiveel.autossav.model.entity.Inspection;
import com.hiveel.autossav.model.entity.InvoiceExam;
import com.hiveel.autossav.model.entity.InvoiceExamQuote;
import com.hiveel.autossav.model.entity.Issue;
import com.hiveel.autossav.model.entity.Quote;


@Component
public class InvoiceExamTask implements Runnable {

    @Autowired
    private InvoiceExamService service;
    @Autowired   
    private InspectionService inspectionService;
    @Autowired
    private IssueService issueService;
    @Autowired
    private QuoteService quoteService;
    @Autowired
    private InvoiceExamQuoteService invoiceExamQuoteService;

	public InvoiceExamTask() {
	}

	@Override
	public void run() {
    	invoiceExamQuoteService.deleteAll(); 
    	service.deleteAll(); 
    	saveExamInvoice();
	}

    private void saveExamInvoice() {
    	LocalDate firstDate = LocalDate.parse("2019-02-01");
    	LocalDate nowDate = LocalDate.now(ZoneId.of("UTC"));
    	Period period = Period.between(firstDate, nowDate);
    	for (int i = 0; i <= period.getMonths(); i++) {
    		LocalDate calDate = firstDate.plusMonths(i);
    		LocalDate maxDate = LocalDate.of(calDate.getYear(), calDate.getMonth().getValue(), calDate.lengthOfMonth());
			SearchCondition commonSearchCondition = new SearchCondition();
			commonSearchCondition.setLimit(0);
			commonSearchCondition.setMinDate(calDate.toString()+" 00:00:00");
			commonSearchCondition.setMaxDate(maxDate.toString()+" 23:59:59");
			List<Inspection> inspectionList = inspectionService.find(commonSearchCondition);
			List<Issue> issueList = issueService.find(commonSearchCondition);
			SearchCondition quoteSearchCondition = new SearchCondition();
			quoteSearchCondition.setLimit(0);				
			List<Quote> quoteList = new ArrayList<Quote>();
			inspectionList.stream().parallel().forEach(inspection->quoteList.addAll(quoteService.findByInspection(quoteSearchCondition, inspection)));
			issueList.stream().parallel().forEach(issue->quoteList.addAll(quoteService.findByIssue(quoteSearchCondition, issue)));
			double price = quoteList.stream().mapToDouble(quote->quote.getLabor()+quote.getPart()).sum();
			InvoiceExam invoiceExam = new InvoiceExam.Builder().set("price", price).set("date", maxDate.plusDays(7).toString()).build();
			service.save(invoiceExam);
			quoteList.stream().parallel().forEach(e->invoiceExamQuoteService.save(new InvoiceExamQuote.Builder().set("invoiceExam", invoiceExam).set("name", e.getName()).set("labor", e.getLabor()).set("part", e.getPart()).build()));
		
    	}
    }
}

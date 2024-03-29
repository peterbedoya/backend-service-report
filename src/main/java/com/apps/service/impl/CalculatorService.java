package com.apps.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apps.domain.ReportHours;
import com.apps.domain.ServiceReportRequest;
import com.apps.models.ServiceReport;
import com.apps.repository.ServiceReportRepository;
import com.apps.service.ICalculatorService;

@Service
public class CalculatorService implements ICalculatorService {

	@Autowired
	private ServiceReportRepository reportRepository;

	private static final Logger log = LogManager.getLogger(CalculatorService.class);

	@Override
	public ServiceReport saveServiceReportHours(ServiceReportRequest request) {
		// TODO Auto-generated method stub
		
		DateTime startedTime = new DateTime(request.getStartedDate().getTime());
		DateTime endTime = new DateTime(request.getEndDate().getTime());
		 
		DateTime dtStartedTime = startedTime
			    .withSecondOfMinute(0);
		
		DateTime dtEndTime = endTime
			    .withSecondOfMinute(0);
		
		ServiceReport serviceReport = new ServiceReport(request.getTechnicalId(), request.getServiceId(),
				dtStartedTime.toDate(), dtEndTime.toDate());
		return reportRepository.save(serviceReport);

	}

	@Override
	public ReportHours calculateHours(ServiceReportRequest request) {
		log.info("Ejecutando mEtodo [@calculateSundayHours]");
		ReportHours reportHours = new ReportHours();
		double totalHours=0;
		List<ServiceReport> serviceReports = (List<ServiceReport>) reportRepository
				.findbytechnicalIdAndWeekYear(request.getTechnicalId(), request.getWeekYear());
		
	
		if(!serviceReports.isEmpty()) {
		reportHours.setTotalNormalHours(calculateNormalHours(serviceReports));
		reportHours.setTotalNightHours(calculateNightHours( serviceReports));
		reportHours.setTotalSundayHours(calculateSundayHours(serviceReports));
		
		
		if (reportHours.getTotalNormalHours() >= 48) {
			reportHours.setTotalNormalExtraHours(reportHours.getTotalNormalHours() - 48);
			reportHours.setTotalNormalHours(48);
			reportHours.setTotalSundayExtraHours(calculateSundayHours(serviceReports));

		} else {
			reportHours.setTotalSundayHours(calculateSundayHours(serviceReports));

		}
		
		}
		
		totalHours=reportHours.getTotalNormalHours()+reportHours.getTotalNormalExtraHours()+reportHours.getTotalNightHours()+reportHours.getTotalNightExtraHours()
		+reportHours.getTotalSundayHours()+reportHours.getTotalSundayExtraHours();
		
		reportHours.setTotalHours(totalHours);
		return reportHours;

	}

	private double calculateNormalHours(List<ServiceReport> serviceReports) {
		List<String> listHours = new ArrayList<String>();
		serviceReports.stream().filter(serviceReport -> !isSunday(serviceReport)).forEach(serviceReport -> {

			DateTime startedTime = new DateTime(serviceReport.getStartedDate().getTime());
			DateTime endTime = new DateTime(serviceReport.getEndDate().getTime());
			if (startedTime.getHourOfDay() < 7) {
				int diff = 7 - startedTime.getHourOfDay();
				startedTime = startedTime.plusHours(diff);
			}
			if (endTime.getHourOfDay() > 20) {
				int diff = 20 - endTime.getHourOfDay();
				endTime = endTime.plusHours(diff);
			}
			Duration duration = new Duration(startedTime.toDate().getTime(), endTime.toDate().getTime());
			listHours.add(duration.toPeriod().toString(getPeriodFormatter()));
		});
		return calculatePeriod(listHours);
	}

	private double calculateNightHours(List<ServiceReport> serviceReports) {
		List<String> listHours = new ArrayList<String>();
		serviceReports.stream().filter(serviceReport -> !isSunday(serviceReport)).forEach(serviceReport -> {

			DateTime startedTime = new DateTime(serviceReport.getStartedDate().getTime());
			DateTime endTime = new DateTime(serviceReport.getEndDate().getTime());
			 
			DateTime dt = endTime
					.withDayOfWeek(endTime.getDayOfWeek()+1)
				    .withHourOfDay(7)
				    .withMinuteOfHour(0)
				    .withSecondOfMinute(0);
			
			if (startedTime.getHourOfDay() >= 20 && endTime.getMillis()<=dt.getMillis()) {
			
				Duration duration = new Duration(startedTime.toDate().getTime(), endTime.toDate().getTime());
				listHours.add(duration.toPeriod().toString(getPeriodFormatter()));
			}
			
		});
		return calculatePeriod(listHours);
	}

	private double calculateSundayHours(List<ServiceReport> serviceReports) {
		List<String> listHours = new ArrayList<String>();
		serviceReports.stream().filter(serviceReport -> isSunday(serviceReport)).forEach(serviceReport -> {
			Duration duration = new Duration(serviceReport.getStartedDate().getTime(),
					serviceReport.getEndDate().getTime());
			listHours.add(duration.toPeriod().toString(getPeriodFormatter()));
		});
		return calculatePeriod(listHours);
	}

	private PeriodFormatter getPeriodFormatter() {
		return new PeriodFormatterBuilder().appendHours().appendSeparator(":").appendMinutes().appendSeparator(":")
				.appendSeconds().toFormatter();
	}

	private double calculatePeriod(List<String> listTimes) {
		Duration durationSum = Duration.ZERO;
		for (String durationString : listTimes) {
			Period period = getPeriodFormatter().parsePeriod(durationString);
			Duration duration = period.toStandardDuration();
			durationSum = durationSum.plus(duration);
		}
		double minutes = durationSum.toPeriod().getMinutes();
		double hours = durationSum.toPeriod().getHours();

		return hours + (minutes) / 60;
	}

	private boolean isSunday(ServiceReport serviceReport) {
		return new DateTime(serviceReport.getStartedDate()).getDayOfWeek() == DateTimeConstants.SUNDAY
				&& new DateTime(serviceReport.getEndDate()).getDayOfWeek() == DateTimeConstants.SUNDAY;
	}

}

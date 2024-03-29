package com.apps.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.apps.domain.ReportHours;
import com.apps.domain.ServiceReportRequest;
import com.apps.models.ServiceReport;

public class ServiceReportObjectMother {

	public static List<ServiceReport> buildList() {
		List<ServiceReport> listServiceReport;
		try {
			listServiceReport = new ArrayList<>(Arrays.asList(
					// NormalHours
					new ServiceReport("123456789", "0001", getDate("2020-12-07 07:00:00.00"),
							getDate("2020-12-07 20:00:00.00")),
					new ServiceReport("123456789", "0001", getDate("2020-12-08 07:00:00.00"),
							getDate("2020-12-08 20:00:00.00")),
					new ServiceReport("123456789", "0001", getDate("2020-12-09 20:00:00.00"),
							getDate("2020-12-09 23:00:00.00")),
					new ServiceReport("123456789", "0001", getDate("2020-12-10 07:00:00.00"),
							getDate("2020-12-10 20:00:00.00")),
					// SundayHours
					new ServiceReport("123456789", "0001", getDate("2020-12-13 08:00:00.00"),
							getDate("2020-12-13 10:30:00.00")),
					new ServiceReport("123456789", "0001", getDate("2020-12-13 11:00:00.00"),
							getDate("2020-12-13 22:00:00.00"))));

			return listServiceReport;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return new ArrayList<ServiceReport>();
		}

	}

	public static List<ServiceReport> buildListEmty() {

		return new ArrayList<ServiceReport>();

	}

	private static Date getDate(String date) throws ParseException {

		return new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(date);

	}

	public static ReportHours buildReportHoursToExpect() {

		ReportHours reportHours = new ReportHours();

		reportHours.setTotalNormalHours(39);
		reportHours.setTotalSundayHours(13.5);
		reportHours.setTotalNightHours(3.0);

		return reportHours;

	}

	public static ServiceReportRequest buildServiceReportRequest() {
		ServiceReportRequest serviceReportRequest = new ServiceReportRequest();
		serviceReportRequest.setTechnicalId("123456789");
		serviceReportRequest.setWeekYear(50);
		return serviceReportRequest;
	}

}

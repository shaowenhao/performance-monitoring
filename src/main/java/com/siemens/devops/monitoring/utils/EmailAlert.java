package com.siemens.devops.monitoring.utils;

import com.siemens.devops.monitoring.Configuration;

import cn.hutool.extra.mail.MailUtil;

public class EmailAlert implements Alert {

	private String to;
	private String subject;
	private String content;

	public EmailAlert(String to, Configuration configuration) {
		this.to = to;
		CaseResultsCollector caseResultsCollector = CustomizedTestListener.getCaseResultsCollector();
		this.subject = String.valueOf(caseResultsCollector.getFailedCaseCount()) + " cases failed for "
				+ configuration.getConfigName();
		StringBuilder sb = new StringBuilder();
		for (String str : caseResultsCollector.getFailedCases()) {
			sb.append(str);
			sb.append("\n==============================\n");
		}
		sb.append("\n");
		sb.append(caseResultsCollector.getSummary());
		sb.append("\n");
		this.content = sb.toString();
	}

	public EmailAlert(String to, String reportUrl, String reportName) {
		this.to = to;
		this.subject = "Test cases report for " + reportName;
		this.content = "\nReport URL: " + reportUrl + "\n";
	}

	@Override
	public void execute() {
		MailUtil.send(to, subject, content, false);
	}
}

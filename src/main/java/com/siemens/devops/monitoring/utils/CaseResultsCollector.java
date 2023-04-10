package com.siemens.devops.monitoring.utils;

import java.util.ArrayList;
import java.util.List;

public class CaseResultsCollector {
	public static String STR_FAILED = "FAILED";
	public static String STR_SKIPPED = "SKIPPED";
	public static String STR_PASSED = "PASSED";
	public static String STR_SUMMARY = "SUMMARY";

	private List<String> failedCases = new ArrayList<String>();
	private List<String> skippedCases = new ArrayList<String>();
	private List<String> passedCases = new ArrayList<String>();
	private List<String> unknownStatusCases = new ArrayList<String>();
	private String summary = "";

	public void add(String status, String detailedInfo) {
		if (STR_FAILED.equals(status)) {
			this.failedCases.add(detailedInfo);
		} else if (STR_SKIPPED.equals(status)) {
			this.skippedCases.add(detailedInfo);
		} else if (STR_PASSED.equals(status)) {
			this.passedCases.add(detailedInfo);
		} else {
			this.unknownStatusCases.add(detailedInfo);
		}
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getSummary() {
		return summary;
	}

	public int getFailedCaseCount() {
		return this.failedCases.size();
	}

	public int getSkippedCaseCount() {
		return this.skippedCases.size();
	}

	public int getPassedCaseCount() {
		return this.passedCases.size();
	}

	public int getUnknownStatusCaseCount() {
		return this.unknownStatusCases.size();
	}

	public List<String> getFailedCases() {
		return failedCases;
	}

	public List<String> getSkippedCases() {
		return skippedCases;
	}

	public List<String> getPassedCases() {
		return passedCases;
	}

	public List<String> getUnknownStatusCases() {
		return unknownStatusCases;
	}

}

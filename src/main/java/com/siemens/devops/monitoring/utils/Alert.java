package com.siemens.devops.monitoring.utils;

public interface Alert {
	public enum AlertType {
		EMAIL, TEAMS
	}

	public void execute();
}

package com.siemens.datalayer.iems.model;


public final class RestConstants {
//	获得设备相关数据
	public static final String LISTDEVICETYPES = "listDeviceTypes";//返回所有设备类型
	public static final String LISTALLDEVICETYPES = "listAllDeviceTypes";//列出所有设备的ID和所属类型
	public static final String GETDEVICESBYTYPE = "getDevicesByType";//根据设备类型获取相应设备列表
	public static final String GETDEVICEINFO = "getDeviceInfo";//根据设备id获取具体的设备信息
	public static final String GETSENSORBYDEVICEID = "getSensorByDeviceId";//根据设备号获取其在KG中绑定的传感器列表，并附带各传感器属性信息
	
//	获得原始数据
	public static final String GETSENSORDATABYSENSORID = "getSensorDataBySensorId";//根据传感器ID列表获取其在一定时间内的数据
	public static final String GETSENSORDATABYDEVICEID = "getSensorDataByDeviceId";//根据设备ID获取其绑定传感器在一定时间窗口内的所有数据
	public static final String SUBSCRIPTIONSBYSENSORID = "subscriptionsBySensorId";//根据传感器ID列表获取在线实时数据推送
	public static final String SUBSCRIPTIONBYDEVICEID = "subscriptionsByDeviceId";//根据设备ID获取其绑定传感器的在线实时数据推送
	public static final String DELETESUBSCRIPTIONS = "deleteSubscriptions";//根据ID删除实时数据订阅
	public static final String GETTOPSENSORDATABYDEVICEID = "getTopSensorDataByDeviceId";//根据设备ID获取最新N条的历史数据
	public static final String SENSORDATA = "sensorData";
	
//	获得KPI数据
	public static final String GETKPIDATABYDEVICEID = "getKpiDataByDeviceId";//根据设备ID获取其绑定传感器及KPI在一定时间窗口内的所有数据
	public static final String GETTOPKPIDATABYDEVICEID = "getTopKPIDataByDeviceId";//根据设备ID获取其最新n条的历史数据及KPI
	public static final String SUBSCRIPTIONSWITHKPIBYDEVICEID = "subscriptionsWithKPIByDeviceId";//根据设备ID列表获取其绑定传感器及KPI的在线实时数据推送
	public static final String KPIDATA = "kpiData";
	
	public static final String SUBSCRIPTIONDATA = "subscriptionData";
	public static final String DELETESUBSCRIPTION = "deleteSubscription";
}
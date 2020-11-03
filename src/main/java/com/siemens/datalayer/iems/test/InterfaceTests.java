package com.siemens.datalayer.iems.test;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

import io.qameta.allure.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.siemens.datalayer.iems.model.SensorDataPro;
import com.siemens.datalayer.iems.model.SubscriptionsRequestDataPro;
import com.siemens.datalayer.utils.AllureEnvironmentPropertiesWriter;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.siemens.datalayer.iems.model.AssetDataPro;
import com.siemens.datalayer.iems.model.RestConstants;

import org.jsoup.internal.StringUtil;
import org.testng.Assert;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

@Epic("iEMS Interface")
public class InterfaceTests {
	
//	private Properties properties;
	private String bodystr = "";
	private Connection connection;
    private Channel channel;
    private String queue;
    private int timeout;
    private String mqHost;
    private String mqPort;
    private String mqUsername;
    private String mqPassword;
    private String mqVirtualhost;
    private String mqTimeout;
    private String mqExchange;

	@Parameters({ "base_url", "port", "pre_asset", "pre_data" ,
		"rabbitmq_host", "rabbitmq_port", "rabbitmq_username", "rabbitmq_password", "rabbitmq_virtual_host", "rabbitmq_timeout", "rabbitmq_exchange"})
	@BeforeClass(description = "Configure host address and communication port for Connector service")
	public void setConnectorEndpoint(
			@Optional("http://140.231.89.85") String base_url, 
			@Optional("30684") String port,
			@Optional("/datalayer/api/v1/asset/") String pre_asset, 
			@Optional("/datalayer/api/v1/data/") String pre_data,
			@Optional("140.231.89.85") String rabbitmq_host, 
			@Optional("30248") String rabbitmq_port,
			@Optional("guest") String rabbitmq_username, 
			@Optional("guest") String rabbitmq_password, 
			@Optional("/") String rabbitmq_virtual_host,
			@Optional("30") String rabbitmq_timeout,
			@Optional("datalayer.exchange.out") String rabbitmq_exchange)
					throws IOException, TimeoutException {
		Endpoint.setBaseUrl(base_url);
		Endpoint.setPort(port);
		Endpoint.setPreAsset(pre_asset);
		Endpoint.setPreData(pre_data);
		
		mqHost = rabbitmq_host;
		mqPort = rabbitmq_port;
		mqUsername = rabbitmq_username;
		mqPassword = rabbitmq_password;
		mqVirtualhost = rabbitmq_virtual_host;
		mqTimeout = rabbitmq_timeout;
		mqExchange = rabbitmq_exchange;
		
//		properties = readProperties();
	    AllureEnvironmentPropertiesWriter.addEnvironmentItem("REST API for asset", base_url + ":" + port + pre_asset );
	    AllureEnvironmentPropertiesWriter.addEnvironmentItem("REST API for data", base_url + ":" + port + pre_data );
	    AllureEnvironmentPropertiesWriter.addEnvironmentItem("RabbitMQ_url", mqHost + ":" + mqPort );
	    AllureEnvironmentPropertiesWriter.addEnvironmentItem("RabbitMQ_Timeout", mqTimeout );
	    AllureEnvironmentPropertiesWriter.addEnvironmentItem("RabbitMQ_exchange", mqExchange );
	}

	public void deleteSubscription(Response response){
    	JsonPath jsonPathEvaluator = response.jsonPath();
		HashMap data = jsonPathEvaluator.get("data");
		String replyTo = data.get("replyTo").toString();
    	Response responseDelete = Endpoint.deleteSubscriptions(replyTo);
		Assert.assertEquals(responseDelete.getStatusCode(), 200);
		System.out.println("Delete subscription : "+ replyTo +" success.");
    }
    
    public Properties readProperties(){
        //创建对象
        Properties pro = new Properties();
        //读取properties文件到缓存[获取src/main/resources下文件的绝对路径]
        try {
            BufferedInputStream in = (BufferedInputStream) this.getClass().getResourceAsStream("/mq.properties");
            pro.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pro;
    }
    
    public boolean initRabbitMQ() throws IOException, TimeoutException {
    	// 创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(mqUsername);
        factory.setPassword(mqPassword);
        // 设置RabbitMQ地址
        factory.setHost(mqHost);
        factory.setPort(Integer.parseInt(mqPort));
        factory.setVirtualHost(mqVirtualhost);
        timeout = Integer.parseInt(mqTimeout);
        
        // 建立到代理服务器连接
		connection = factory.newConnection();
		// 创建信道
        channel = connection.createChannel();
        // 声明交换器
        channel.exchangeDeclare(mqExchange, "topic", true);
        // 声明队列
        queue = channel.queueDeclare().getQueue();
		boolean isConnected = connection.isOpen();
        boolean isChannelOpen = channel.isOpen();
        System.out.println("is connected " + isConnected + ", is channel open " + isChannelOpen);
        return connection.isOpen() && channel.isOpen();
    }
    
	@Test(priority = 0, description = RestConstants.LISTDEVICETYPES)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT and verify if all the available device type can be read out.")
	@Feature("Get asset data API")
	@Story("Get All device type")
	public void listDeviceTypes() {
		Response response = Endpoint.listDeviceTypes();
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.jsonPath().getList("data").size() > 0);
		assertThat(response.getBody().asString(),
				matchesJsonSchemaInClasspath("iems/" + RestConstants.LISTDEVICETYPES + ".JSON"));
	}

	@Test(priority = 0, description = RestConstants.LISTALLDEVICETYPES)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT and verify if all the available device id and type can be read out.")
	@Feature("Get asset data API")
	@Story("Get All device type contain id and type")
	public void listAllDeviceTypes() {
		Response response = Endpoint.listAllDeviceTypes();
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.jsonPath().getList("data").size() > 0);
		assertThat(response.getBody().asString(),
				matchesJsonSchemaInClasspath("iems/" + RestConstants.LISTALLDEVICETYPES + ".JSON"));
	}

	@Test(priority = 0, description = RestConstants.GETDEVICESBYTYPE, dataProviderClass = AssetDataPro.class, dataProvider = "dataForGetDevicesByType")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT with specified parameters and check the response message.")
	@Feature("Get asset data API")
	@Story("Get devices by type")
	public void getDevicesByType(Map<String, String> paramMaps) {
		HashMap<String, Object> queryParameters = new HashMap<>();

		if (paramMaps.containsKey("deviceType"))
			queryParameters.put("device_type", paramMaps.get("deviceType"));

		Response response = Endpoint.getDevicesByType(queryParameters);

		Assert.assertEquals(response.getStatusCode(), 200);

		if (paramMaps.containsKey("expectCode"))
			Assert.assertEquals(response.jsonPath().getString("code"), paramMaps.get("expectCode"));

		if (paramMaps.containsKey("expectMessage"))
			Assert.assertTrue(response.jsonPath().getString("message").contains(paramMaps.get("expectMessage")));

		if (paramMaps.get("description").contains("good request")) {
			if (paramMaps.get("description").contains("data not found"))
				Assert.assertTrue(response.jsonPath().getList("data").isEmpty());

			if (paramMaps.get("description").contains("data retrieved")) {
				Assert.assertTrue(response.jsonPath().getList("data").size() > 0);
				assertThat(response.getBody().asString(),
						matchesJsonSchemaInClasspath("iems/" + RestConstants.LISTALLDEVICETYPES + ".JSON"));
			}
		} else {
			Assert.assertNull(response.jsonPath().getList("data"));
		}

	}

	@Test(priority = 0, description = RestConstants.GETDEVICEINFO, dataProviderClass = AssetDataPro.class, dataProvider = "dataForDeviceId")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT with specified parameters and check the response message.")
	@Feature("Get asset data API")
	@Story("Get device infomation")
	public void getDeviceInfo(Map<String, String> paramMaps) {
		HashMap<String, Object> queryParameters = new HashMap<>();
		if (paramMaps.containsKey("id"))
			queryParameters.put("id", paramMaps.get("id"));
		Response response = Endpoint.getDeviceInfo(queryParameters);
		if (paramMaps.get("description").contains("good request")) {
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertNotNull(response.jsonPath().getString("data"));
			assertThat(response.getBody().asString(),
					matchesJsonSchemaInClasspath("iems/" + RestConstants.GETDEVICEINFO + ".JSON"));
		} else {
			Assert.assertNull(response.jsonPath().getString("data"));
		}
	}
	
	@Test(priority = 0, description = RestConstants.GETSENSORBYDEVICEID, dataProviderClass = AssetDataPro.class, dataProvider = "dataForDeviceId")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT with specified parameters and check the response message.")
	@Feature("Get asset data API")
	@Story("Get sensors of device")
	public void getSensorByDeviceId(Map<String, String> paramMaps) {
		HashMap<String, Object> queryParameters = new HashMap<>();
		if (paramMaps.containsKey("id"))
			queryParameters.put("id", paramMaps.get("id"));
		Response response = Endpoint.getSensorByDeviceId(queryParameters);
		if (paramMaps.get("description").contains("good request")) {
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertTrue(response.jsonPath().getList("data").size() > 0);
		} else {
			Assert.assertNull(response.jsonPath().getList("data"));
		}
	}
	
//	Get Sensor Data API
	@Test(priority = 0, description = RestConstants.GETSENSORDATABYSENSORID, dataProviderClass = SensorDataPro.class, dataProvider = "dataForGetSensorDataBySensorId")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT with json and check the response message.")
	@Feature("Get sensor data API")
	@Story("Get sensor data by sensor ids")
	public void getSensorDataBySensorId(Map<String, Object> paramMaps) {
		Response response = Endpoint.getSensorDataBySensorId( paramMaps.get("body").toString());
		if (paramMaps.get("description").toString().contains("good request")) {
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertTrue(response.jsonPath().getList("data").size() > 0);
			assertThat(response.getBody().asString(),
					matchesJsonSchemaInClasspath("iems/" + RestConstants.SENSORDATA + ".JSON"));
		} else {
			Assert.assertNull(response.jsonPath().getList("data"));
		}
	}
	
	@Test(priority = 0, description = RestConstants.GETSENSORDATABYDEVICEID, dataProviderClass = SensorDataPro.class, dataProvider = "dataForGetSensorDataByDeviceId")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT with json and check the response message.")
	@Feature("Get sensor data API")
	@Story("Get sensor data by device id")
	public void getSensorDataByDeviceId(Map<String, Object> paramMaps) {
		Response response = Endpoint.getSensorDataByDeviceId( paramMaps.get("body").toString());
		if (paramMaps.get("description").toString().contains("good request")) {
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertTrue(response.jsonPath().getList("data").size() > 0);
			assertThat(response.getBody().asString(),
					matchesJsonSchemaInClasspath("iems/" + RestConstants.SENSORDATA + ".JSON"));
		} else {
			Assert.assertNull(response.jsonPath().getList("data"));
		}
	}
	
	@Test(priority = 0, description = RestConstants.GETTOPSENSORDATABYDEVICEID, dataProviderClass = SensorDataPro.class, dataProvider = "getTopSensorDataByDeviceId")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT with json and check the response message.")
	@Feature("Get sensor data API")
	@Story("Get top n sensor data by device id")
	public void getTopSensorDataByDeviceId(Map<String, Object> paramMaps) {
		Response response = Endpoint.getTopSensorDataByDeviceId( paramMaps.get("body").toString());
		if (paramMaps.get("description").toString().contains("good request")) {
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertTrue(response.jsonPath().getList("data").size() > 0);
			assertThat(response.getBody().asString(),
					matchesJsonSchemaInClasspath("iems/" + RestConstants.SENSORDATA + ".JSON"));
		} else {
			Assert.assertNull(response.jsonPath().getList("data"));
		}
	}
	
	
	@Test(priority = 0, description = RestConstants.GETKPIDATABYDEVICEID, dataProviderClass = SensorDataPro.class, dataProvider = "dataForGetSensorDataByDeviceId")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT with json and check the response message.")
	@Feature("Get kpi data API")
	@Story("Get kpi data by device id")
	public void getKpiDataByDeviceId(Map<String, Object> paramMaps) {
		Response response = Endpoint.getKpiDataByDeviceId( paramMaps.get("body").toString());
		if (paramMaps.get("description").toString().contains("good request")) {
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertTrue(response.jsonPath().getList("data").size() > 0);
			assertThat(response.getBody().asString(),
					matchesJsonSchemaInClasspath("iems/" + RestConstants.KPIDATA + ".JSON"));
		} else {
			Assert.assertNull(response.jsonPath().getList("data"));
		}
	}
	
	@Test(priority = 0, description = RestConstants.GETTOPKPIDATABYDEVICEID, dataProviderClass = SensorDataPro.class, dataProvider = "getTopSensorDataByDeviceId")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT with json and check the response message.")
	@Feature("Get kpi data API")
	@Story("Get top n kpi data by device id")
	public void getTopKPIDataByDeviceId(Map<String, Object> paramMaps) {
		Response response = Endpoint.getTopKPIDataByDeviceId( paramMaps.get("body").toString());
		if (paramMaps.get("description").toString().contains("good request")) {
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertTrue(response.jsonPath().getList("data").size() > 0);
			assertThat(response.getBody().asString(),
					matchesJsonSchemaInClasspath("iems/" + RestConstants.KPIDATA + ".JSON"));
		} else {
			Assert.assertNull(response.jsonPath().getList("data"));
		}
	}
	
	
//	Subscription Data
	@Test(priority = 0, description = RestConstants.SUBSCRIPTIONSBYSENSORID, dataProviderClass = SubscriptionsRequestDataPro.class, dataProvider = "dataForSubscriptionBySensorId")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT with sensor ids and subscription the response message.")
	@Feature("Get subscription data API")
	@Story("Get subscription sensor data by sensor ids")
	public void subscriptionsBySensorId(Map<String, String> paramMaps) throws IOException, TimeoutException {
		HashMap<String, Object> queryParameters = new HashMap<>();
		if (paramMaps.containsKey("request"))
			queryParameters.put("request", paramMaps.get("request").toString());
		Response response = Endpoint.subscriptionsBySensorId(queryParameters);
		if (paramMaps.get("description").contains("good request")) {
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertNotNull(response.jsonPath().getString("data"));
			assertThat(response.getBody().asString(),
					matchesJsonSchemaInClasspath("iems/" + RestConstants.SUBSCRIPTIONDATA + ".JSON"));
			boolean flag = validMQData(response);
			if(flag) {
				System.out.println("Get sensor data by sensor ids success.");
			}else {
				Assert.fail("Get MQ data fail.");
			}
			deleteSubscription(response);
		} else {
			Assert.assertNull(response.jsonPath().getString("data"));
		}
		
	}
	
	@Test(priority = 0, description = RestConstants.SUBSCRIPTIONBYDEVICEID, dataProviderClass = SubscriptionsRequestDataPro.class, dataProvider = "dataForSubscriptionsByDeviceId")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT with device id and subscription the response message.")
	@Feature("Get subscription data API")
	@Story("Get subscription sensor data by device id")
	public void subscriptionsByDeviceId(Map<String, String> paramMaps) throws IOException, TimeoutException {
		HashMap<String, Object> queryParameters = new HashMap<>();
		if (paramMaps.containsKey("deviceId"))
			queryParameters.put("deviceId", paramMaps.get("deviceId"));
		Response response = Endpoint.subscriptionsByDeviceId(queryParameters);
		if (paramMaps.get("description").contains("good request")) {
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertNotNull(response.jsonPath().getString("data"));
			assertThat(response.getBody().asString(),
					matchesJsonSchemaInClasspath("iems/" + RestConstants.SUBSCRIPTIONDATA + ".JSON"));
			
			boolean flag = validMQData(response);
			if(flag) {
				System.out.println("Get sensor data by device id success.");
			}else {
				Assert.fail("Get MQ data fail.");
			}
			deleteSubscription(response);
		} else {
			Assert.assertNull(response.jsonPath().getString("data"));
		}
		
	}
	
	@Test(priority = 0, description = RestConstants.SUBSCRIPTIONSWITHKPIBYDEVICEID, dataProviderClass = SubscriptionsRequestDataPro.class, dataProvider = "dataForSubscriptionsWithKPIByDeviceId")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT with device ids and subscription the response message.")
	@Feature("Get subscription data API")
	@Story("Get subscription kpi data by device ids")
	public void subscriptionsWithKPIByDeviceId(Map<String, String> paramMaps) throws IOException, TimeoutException {
		HashMap<String, Object> queryParameters = new HashMap<>();
		if (paramMaps.containsKey("request"))
			queryParameters.put("request", paramMaps.get("request").toString());
		Response response = Endpoint.subscriptionsWithKPIByDeviceId(queryParameters);
		if (paramMaps.get("description").contains("good request")) {
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertNotNull(response.jsonPath().getString("data"));
			assertThat(response.getBody().asString(),
					matchesJsonSchemaInClasspath("iems/" + RestConstants.SUBSCRIPTIONDATA + ".JSON"));
			boolean flag = validMQData(response);
			if(flag) {
				System.out.println("Get kpi data by device ids success.");
			}else {
				Assert.fail("Get MQ data fail.");
			}
			deleteSubscription(response);
		} else {
			Assert.assertNull(response.jsonPath().getString("data"));
		}
		
	}
	
	@Test(priority = 0, description = RestConstants.DELETESUBSCRIPTIONS)
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT with id and delete subscription.")
	@Feature("Get subscription data API")
	@Story("Delete subscription by id")
	public void deleteSubscriptionsSuccess() {
		HashMap<String, String> deviceMap = Endpoint.getDeviceIdByName(new ArrayList<String>(){{
            add("1#制冷机");
            add("3#制冷机");
        }});
		HashMap<String, Object> queryParameters = new HashMap<>();
		queryParameters.put("request", Integer.parseInt(deviceMap.get("1#制冷机")));
		Response responsePro = Endpoint.subscriptionsWithKPIByDeviceId(queryParameters);
		Assert.assertEquals(responsePro.getStatusCode(), 200);
		JsonPath jsonPathEvaluator = responsePro.jsonPath();
		Assert.assertNotNull(jsonPathEvaluator.get("data"));
		HashMap data = jsonPathEvaluator.get("data");
		String replyTo = data.get("replyTo").toString();
		Response response = Endpoint.deleteSubscriptions(replyTo);
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertNotNull(response.jsonPath().getString("data"));
		assertThat(response.getBody().asString(),
				matchesJsonSchemaInClasspath("iems/" + RestConstants.DELETESUBSCRIPTION + ".JSON"));
	}
	
	@Test(priority = 0, description = RestConstants.DELETESUBSCRIPTIONS, dataProviderClass = SubscriptionsRequestDataPro.class, dataProvider = "dataForDeleteSubscriptions")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Send a request to SUT with id and delete subscription.")
	@Feature("Get subscription data API")
	@Story("Delete subscription by id")
	public void deleteSubscriptions(Map<String, String> paramMaps) {
		Response response = Endpoint.deleteSubscriptions(paramMaps.get("id"));
		if (paramMaps.get("description").contains("good request")) {
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertNotNull(response.jsonPath().getString("data"));
			assertThat(response.getBody().asString(),
					matchesJsonSchemaInClasspath("iems/" + RestConstants.DELETESUBSCRIPTION + ".JSON"));
		} else {
			Assert.assertNull(response.jsonPath().getString("data"));
		}
	}
    
	
	public boolean validMQData(Response response) throws IOException, TimeoutException {
		boolean flag = false;
		boolean isConnected = initRabbitMQ();
		if(!isConnected) {
			return false;
		}
        JsonPath jsonPathEvaluator = response.jsonPath();
		HashMap data = jsonPathEvaluator.get("data");
		String replyTo = data.get("replyTo").toString();
		String routingKey = replyTo;
        // 绑定队列
        channel.queueBind(queue, mqExchange, routingKey);
        System.out.println("RabbitMQ consumer start ...");
        long t1 = System.currentTimeMillis();
        while (true) {
        	long t2 = System.currentTimeMillis();
            if(t2-t1 > timeout*1000){
                break;
            }else{
	            // 消费消息
	            boolean autoAck = false;
	            String consumerTag = "";
	            channel.basicConsume(queue, autoAck, consumerTag, new DefaultConsumer(channel) {
	                @Override
	                public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties,
	                        byte[] body) throws IOException {
	                    super.handleDelivery(consumerTag, envelope, properties, body);
	                    String routingKey = envelope.getRoutingKey();
	                    String contentType = properties.getContentType();
	                    System.out.println("routingKey: " + routingKey);
//	                    System.out.println("消费的内容类型 contentType: " + contentType);
	
	                    long deliveryTag = envelope.getDeliveryTag();
	                    channel.basicAck(deliveryTag, false);
	                    bodystr = new String(body, "UTF-8");
	                    System.out.println("bodystr: " + bodystr);
	                }
	            });
            }
        }
        if(!StringUtil.isBlank(bodystr)) {
        	flag = true;
        }else {
        	flag = false;
        }
        return flag;
    }
	
	
//  /***
//  * 客户端重连
//  *
//  * 测试步骤：
//  * 1. 设置 RECONNECT_INTERVAL_SECONDS(default 10s) 和 RECONNECT_TIMES(default 3)
//  * 2. 执行单元测试方法 
//  * 3. 手动关闭 RabbitMQ Server 进程
//  * 4. 等待几秒钟后重启 RabbitMQ Server， 不能超过30秒(RabbitMQ 启动需要5-6秒)
//  * 5. 查看打印，是否重新连接成功
//  *
//  * 提示：封装客户端的时候，继承Closeable接口
//  *
//  * @throws InterruptedException
//  */
// @Test(priority = 0, description = RestConstants.SUBSCRIPTIONBYDEVICEID)
//	@Severity(SeverityLevel.BLOCKER)
//	@Description("Client connect to RabbitMQ.")
// @Feature("Get subscription data API")
//	@Story("Connect to RabbitMQ")
// public void validRabbitMQConnection() throws InterruptedException {
// 		boolean reconnected = false;
//         int retry = 0;
//         do {
//             try {
//                 reconnected = initRabbitMQ();
//             } catch (IOException e) {
//                 e.printStackTrace();
//             } catch (TimeoutException e) {
//                 e.printStackTrace();
//             }
//             if (reconnected) {
//                 System.out.println("rabbitmq connected, retry times is " + retry);
//                 break;
//             }
//             retry++;
//             System.out.println("reconnect after " + RECONNECT_INTERVAL_SECONDS + "s, retry is " + retry);
//             try {
//                 TimeUnit.SECONDS.sleep(RECONNECT_INTERVAL_SECONDS);
//             } catch (InterruptedException e) {
//                 e.printStackTrace();
//             }
//         } while (retry < RECONNECT_TIMES && !stop);
//
//         Assert.assertTrue(reconnected);
//
//     TimeUnit.MINUTES.sleep(1);
// }

}

package com.siemens.datalayer.snc.util;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

public class MessageSupplier implements Supplier<String> {
    @Override
    public String get() {

        LocalDateTime localDateTime = LocalDateTime.now();
        String message = null;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = dateTimeFormatter.format(localDateTime);
		String minute = String.valueOf(localDateTime.getMinute());

		// Alarm enity in SNC-TEST, send  following message to kafka with will generated instance-kg
		// Cyclye不一样 会清掉重新建立instance-kg
        // Context_id was grapname
			message = "{"
					+ "	\"context_id\": \"release160\",\n"
					+ "	\"graphql_count\": \"2\",\n"
					+ "	\"graphql_id\": \"1\",\n"
					+ "	\"graphql_id_total_count\": 255,\n"
					+ "	\"graphql_id_current_count\": 200,\n"
					+ "	\"cycle\": \"" + minute+ "\",\n"
					+ "	\"type\": \"Jena\",\n"
					+ "	\"data\": {\n"
					+ "		\"Alarm\": [\n"
					+ "			{\n"
					+ "      \"start_time\": \"" + currentTime + "\",\n"
					+ "      \"device_id\": \"1\",\n"
					+ "      \"end_time\":  \"" + currentTime + "\",\n"
					+ "      \"description\": \"sdl-8401 instance 1\"\n"
					+ "			}\n"
					+ "		]\n"
					+ "	}\n"
					+ "}";
//        }else {
//            message = "{"
//                    + "	\"context_id\": \"1\",\n"
//                    + "	\"graphql_count\": \"2\",\n"
//                    + "	\"graphql_id\": \"1\",\n"
//                    + "	\"graphql_id_total_count\": 255,\n"
//                    + "	\"graphql_id_current_count\": 200,\n"
//                    + "	\"cycle\": \"2\",\n"
//                    + "	\"type\": \"Jena\",\n"
//                    + "	\"data\": {\n"
//                    + "		\"Alarm\": [\n"
//                    + "			{\n"
//                    + "      \"start_time\": \""+ currentTime + "\",\n"
//                    + "      \"device_id\": \"2\",\n"
//                    + "      \"end_time\":  \"" + currentTime + "\",\n"
//                    + "      \"description\": \"sdl-8401 instance 2\"\n"
//                    + "			}\n"
//                    + "		]\n"
//                    + "	}\n"
//                    + "}";
//        }
		System.out.println(message);
		return  message;
    }
}

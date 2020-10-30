package com.siemens.datalayer.apiengine.model;

import com.siemens.datalayer.iems.model.ResponseCode;
import com.siemens.datalayer.iems.test.Endpoint;
import org.testng.annotations.DataProvider;

import java.util.*;

public class SNCDataPro {

	@DataProvider(name = "dataForSNCProjectOrder")
	Iterator<Object[]> dataForSNCProjectOrder() {
		Collection<Object[]> queryParamCollection = new ArrayList<Object[]>();
		List<Map<String, Object>> listOfQueryParams = new ArrayList<>();
		Map<String, Object> goodQuery01 = new HashMap<>();
		String query1 = "{\n" +
				"    Product_Order(cond: \"{ demand_date:{_gte: \\\"2020-10-01 00:00:00\\\"} }\"){\n" +
				"        insert_time\n" +
				"        demand_date\n" +
				"        sales_order\n" +
				"    }\n" +
				"}";
		goodQuery01.put("query", query1);
		goodQuery01.put("jsonpath", "data.Product_Order");
		goodQuery01.put("schema", "snc-product-order1.json");
		listOfQueryParams.add(goodQuery01);

		Map<String, Object> goodQuery02 = new HashMap<>();
		String query2 = "{\n" +
				"    Product_Order(cond: \"{ sales_order:{_eq: \\\"800014035899\\\"} }\"){\n" +
				"        sales_order\n" +
				"        invert_Product {\n" +
				"            product_no\n" +
				"        }\n" +
				"    }\n" +
				"}";
		goodQuery02.put("query", query2);
		goodQuery02.put("jsonpath", "data.Product_Order");
		goodQuery02.put("schema", "snc-product-order2.json");
		listOfQueryParams.add(goodQuery02);


		Map<String, Object> goodQuery03 = new HashMap<>();
		String query3 = "{\n" +
				"    Product_Order(cond: \"{ demand_date:{_gte: \\\"2020-10-01 00:00:00\\\"} }\") {\n" +
				"        demand_date\n" +
				"        insert_time\n" +
				"        product\n" +
				"        sales_order\n" +
				"        invert_Product {\n" +
				"            product_no\n" +
				"            invert_Preactor_Order(cond: \"{ start_time:{_gte:\\\"2020-10-01 00:00:00\\\", _lt:\\\"2020-10-02 00:00:00\\\"} }\") {\n" +
				"                order_no\n" +
				"                product\n" +
				"                plan_quantity\n" +
				"                invert_Product_Order_Process(cond: \"{ mach_type:{_eq:\\\"MPM******\\\"}, event_time:{_gte:\\\"2020-10-01 00:00:00\\\", _lt:\\\"2020-10-02 00:00:00\\\"} }\") {\n" +
				"                    product_order\n" +
				"                    product\n" +
				"                    pass_quantity\n" +
				"                    produce_quantity\n" +
				"                }\n" +
				"            }\n" +
				"            produced_Product_Qualification(cond: \"{ mach_type:{_eq:\\\"MPM******\\\"}, event_time:{_gte:\\\"2020-10-01 00:00:00\\\", _lt:\\\"2020-10-02 00:00:00\\\"} }\") {\n" +
				"                test_result\n" +
				"                product\n" +
				"                mach_type\n" +
				"                event_time\n" +
				"                fid\n" +
				"                invert_Production_Procedure {\n" +
				"                    Work_On_Work_Position {\n" +
				"                        work_center_id\n" +
				"                    }\n" +
				"                }\n" +
				"            }\n" +
				"        }\n" +
				"    }\n" +
				"}";
		goodQuery03.put("query", query3);
		goodQuery03.put("jsonpath", "data.Product_Order");
		goodQuery03.put("schema", "snc-product-order3.json");
		listOfQueryParams.add(goodQuery03);


		Map<String, Object> goodQuery04 = new HashMap<>();
		String query4 = "{\n" +
				"    Product_Order(cond: \"{ sales_order:{_eq:\\\"800014035899\\\"} }\") {\n" +
				"        demand_date\n" +
				"        insert_time\n" +
				"        product\n" +
				"        sales_order\n" +
				"        invert_Product {\n" +
				"            product_no\n" +
				"            invert_Preactor_Order(cond: \"{ start_time:{_gte:\\\"2020-10-01 00:00:00\\\", _lt:\\\"2020-10-02 00:00:00\\\"} }\") {\n" +
				"                order_no\n" +
				"                product\n" +
				"                plan_quantity\n" +
				"                invert_Product_Order_Process(cond: \"{ mach_type:{_eq:\\\"MPM******\\\"}, event_time:{_gte:\\\"2020-10-01 00:00:00\\\", _lt:\\\"2020-10-02 00:00:00\\\"} }\") {\n" +
				"                    product_order\n" +
				"                    product\n" +
				"                    pass_quantity\n" +
				"                    produce_quantity\n" +
				"                }\n" +
				"            }\n" +
				"            produced_Product_Qualification(cond: \"{ mach_type:{_eq:\\\"MPM******\\\"}, event_time:{_gte:\\\"2020-10-01 00:00:00\\\", _lt:\\\"2020-10-02 00:00:00\\\"} }\") {\n" +
				"                test_result\n" +
				"                product\n" +
				"                mach_type\n" +
				"                event_time\n" +
				"                fid\n" +
				"                invert_Production_Procedure {\n" +
				"                    Work_On_Work_Position {\n" +
				"                        work_center_id\n" +
				"                    }\n" +
				"                }\n" +
				"            }\n" +
				"        }\n" +
				"    }\n" +
				"}";
		goodQuery04.put("query", query4);
		goodQuery04.put("jsonpath", "data.Product_Order");
		goodQuery04.put("schema", "snc-product-order4.json");
		listOfQueryParams.add(goodQuery04);


		Map<String, Object> goodQuery05 = new HashMap<>();
		String query5 = "{\n" +
				"    Product_Order(cond: \"{ sales_order:{_eq:\\\"800014035899\\\"}, product:{_eq:\\\"A5E03262691\\\"} }\") {\n" +
				"        demand_date\n" +
				"        insert_time\n" +
				"        product\n" +
				"        sales_order\n" +
				"        invert_Product {\n" +
				"            product_no\n" +
				"            invert_Preactor_Order(cond: \"{ start_time:{_gte:\\\"2020-10-01 00:00:00\\\", _lt:\\\"2020-10-02 00:00:00\\\"} }\") {\n" +
				"                order_no\n" +
				"                product\n" +
				"                plan_quantity\n" +
				"                invert_Product_Order_Process(cond: \"{ mach_type:{_eq:\\\"MPM******\\\"}, event_time:{_gte:\\\"2020-10-01 00:00:00\\\", _lt:\\\"2020-10-02 00:00:00\\\"} }\") {\n" +
				"                    product_order\n" +
				"                    product\n" +
				"                    pass_quantity\n" +
				"                    produce_quantity\n" +
				"                }\n" +
				"            }\n" +
				"            produced_Product_Qualification(cond: \"{ mach_type:{_eq:\\\"MPM******\\\"}, event_time:{_gte:\\\"2020-10-01 00:00:00\\\", _lt:\\\"2020-10-02 00:00:00\\\"} }\") {\n" +
				"                test_result\n" +
				"                product\n" +
				"                mach_type\n" +
				"                event_time\n" +
				"                fid\n" +
				"                invert_Production_Procedure {\n" +
				"                    Work_On_Work_Position {\n" +
				"                        work_center_id\n" +
				"                    }\n" +
				"                }\n" +
				"            }\n" +
				"        }\n" +
				"    }\n" +
				"}";
		goodQuery05.put("query", query5);
		goodQuery05.put("jsonpath", "data.Product_Order");
		goodQuery05.put("schema", "snc-product-order5.json");
		listOfQueryParams.add(goodQuery05);

		for (Map<String, Object> map : listOfQueryParams) {
			queryParamCollection.add(new Object[] { map });
		}

		return queryParamCollection.iterator();
	}



}

package com.siemens.datalayer.jinzu.test;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("JinZu Interface")
public class JinzuTests {

    @Test(priority = 0, description = "查看某个租赁物的详细信息.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("查看某个租赁物的详细信息.")
    @Feature("Lease")
    @Story("Get lease detail")
    public void leaseDetail() {
        String data = "{Lease(\n" +
                "cond:\"{id:{_eq:667}}\",\n" +
                "order:\"\")\n" +
                " {\n" +
                " lease_type_gb\n" +
                " lending\n" +
                " lposition\n" +
                " invoice_no\n" +
                " project\n" +
                " lease_type_yj\n" +
                " brand_name\n" +
                " unit_price\n" +
                " serial_num\n" +
                " discount_ratio\n" +
                " transfer_price\n" +
                " lease_net_val\n" +
                " nominal_cost\n" +
                " supplier\n" +
                " asset_type\n" +
                " name\n" +
                " lease_type\n" +
                " position\n" +
                " id\n" +
                " purchase_time\n" +
                " status\n" +
                " lease_group\n" +
                " product_model\n" +
                " invert_Lease_Group(\n" +
                " cond:\"\",\n" +
                " order:\"\")\n" +
                "  {\n" +
                "  lease_net_val\n" +
                "  transfer_price\n" +
                "  lease_type_gb\n" +
                "  nominal_cost\n" +
                "  count\n" +
                "  asset_type\n" +
                "  project\n" +
                "  lease_type_yj\n" +
                "  id\n" +
                "  lease_type\n" +
                "  unit_price\n" +
                "  discount_ratio\n" +
                "  invert_Project(\n" +
                "  cond:\"\",\n" +
                "  order:\"\")\n" +
                "   {\n" +
                "   no\n" +
                "   rent_type\n" +
                "   is_manufacture_buy_back\n" +
                "   classification_level\n" +
                "   city\n" +
                "   detail_address\n" +
                "   charge_frequency\n" +
                "   credit_amount\n" +
                "   risk_mgr\n" +
                "   is_manufacture_leasing\n" +
                "   business_unit\n" +
                "   discount_ratio\n" +
                "   class_level\n" +
                "   manufacture\n" +
                "   business_mgr\n" +
                "   expire_date\n" +
                "   province\n" +
                "   district\n" +
                "   name\n" +
                "   guarantee_type\n" +
                "   id\n" +
                "   status\n" +
                "   invert_Customer(\n" +
                "   cond:\"\",\n" +
                "   order:\"\")\n" +
                "    {\n" +
                "    registered_address\n" +
                "    city\n" +
                "    major_class\n" +
                "    cname\n" +
                "    enterprise_size\n" +
                "    project\n" +
                "    is_connected_tx\n" +
                "    legal_person_id\n" +
                "    small_class\n" +
                "    province\n" +
                "    is_gov_fin_customer\n" +
                "    contact\n" +
                "    id\n" +
                "    group\n" +
                "    legal_person\n" +
                "    contact_detail\n" +
                "    actual_controller\n" +
                "    office_address\n" +
                "    ctype\n" +
                "    district\n" +
                "    is_group_customer\n" +
                "    middle_class\n" +
                "    category\n" +
                "    cid\n" +
                "    holding_type\n" +
                "   }\n" +
                "  }\n" +
                "  Refer_To_Power_Station_Properties(\n" +
                "  cond:\"\",\n" +
                "  order:\"\")\n" +
                "   {\n" +
                "   avg_annual_eq_hours\n" +
                "   lease_group\n" +
                "   ps_type\n" +
                "   structure\n" +
                "   capacity\n" +
                "  }\n" +
                " }\n" +
                " Refer_To_Power_Station(\n" +
                " cond:\"\",\n" +
                " order:\"\")\n" +
                "  {\n" +
                "  id\n" +
                "  Compose_Of_Site(\n" +
                "  cond:\"\",\n" +
                "  order:\"\")\n" +
                "   {\n" +
                "   avg_annual_eq_hours\n" +
                "   avg_irradiance\n" +
                "   capacity\n" +
                "   city\n" +
                "   commissioning_date\n" +
                "   grid_inject_production\n" +
                "   id\n" +
                "   irradiance\n" +
                "   location\n" +
                "   name\n" +
                "   power_station\n" +
                "   state\n" +
                "   sys_engaged_date\n" +
                "   type\n" +
                "   voltage_degree\n" +
                "   Generate_Site_Report(\n" +
                "   cond:\"{report_time:{_eq:\\\"1582128000000\\\"}}\",\n" +
                "   order:\"\")\n" +
                "    {\n" +
                "    energy_loss\n" +
                "    pr\n" +
                "    peak_power_moment\n" +
                "    production\n" +
                "    purchasing_energy\n" +
                "    sunshin_duration\n" +
                "    full_generation_hours\n" +
                "    comprehensive_plant_power_consumption\n" +
                "    peak_power\n" +
                "    self_consumed_percent\n" +
                "    site\n" +
                "    revenue\n" +
                "    report_time\n" +
                "    comprehensive_plant_power_consumption_rate\n" +
                "    self_consumed_production\n" +
                "    energy_loss_rate\n" +
                "    equivalent_hours\n" +
                "    pr_validity\n" +
                "   }\n" +
                "   invert_Weather(\n" +
                "   cond:\"\",\n" +
                "   order:\"\")\n" +
                "    {\n" +
                "    max_temperature\n" +
                "    update_time\n" +
                "    site\n" +
                "    rainfall\n" +
                "    main_weather_station\n" +
                "    avg_temperature\n" +
                "    description\n" +
                "    min_temperature\n" +
                "   }\n" +
                "   Involve_Device_Alarm_Event(\n" +
                "   cond:\"\",\n" +
                "   order:\"\")\n" +
                "    {\n" +
                "    alarm_location\n" +
                "    update_time\n" +
                "    site\n" +
                "    alarm_handle_status\n" +
                "    alarm_template\n" +
                "    occurring_time\n" +
                "    event_name\n" +
                "    id\n" +
                "    update_by\n" +
                "    device\n" +
                "    confirm_time\n" +
                "   }\n" +
                "  }\n" +
                " }\n" +
                "}}";
        Response response = JinzuEndpoint.leaseDetail(data);
        Assert.assertEquals(response.getStatusCode(), 200);
    }


    @Test(priority = 0, description = "根据project id获取租赁物信息列表.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("根据project id获取租赁物信息列表.")
    @Feature("Lease")
    @Story("Get lease list")
    public void leaseList() {
        String data = "{Lease(\n" +
                "cond:\"{project:{_eq:99}}\",\n" +
                "order:\"\")\n" +
                "{\n" +
                "lease_type_gb\n" +
                "lending\n" +
                "lposition\n" +
                "invoice_no\n" +
                "project\n" +
                "lease_type_yj\n" +
                "brand_name\n" +
                "unit_price\n" +
                "serial_num\n" +
                "discount_ratio\n" +
                "transfer_price\n" +
                "lease_net_val\n" +
                "nominal_cost\n" +
                "supplier\n" +
                "asset_type\n" +
                "name\n" +
                "lease_type\n" +
                "position\n" +
                "id\n" +
                "purchase_time\n" +
                "status\n" +
                "lease_group\n" +
                "product_model\n" +
                "Refer_To_Power_Station(\n" +
                "cond:\"\",\n" +
                "order:\"\")\n" +
                "{\n" +
                "id\n" +
                "Compose_Of_Site(\n" +
                "cond:\"\",\n" +
                "order:\"\")\n" +
                "{\n" +
                "avg_annual_eq_hours\n" +
                "avg_irradiance\n" +
                "capacity\n" +
                "city\n" +
                "commissioning_date\n" +
                "grid_inject_production\n" +
                "id\n" +
                "irradiance\n" +
                "location\n" +
                "name\n" +
                "power_station\n" +
                "state\n" +
                "sys_engaged_date\n" +
                "type\n" +
                "voltage_degree\n" +
                "}\n" +
                "}\n" +
                "}}";
        Response response = JinzuEndpoint.graphQuery(data);
        Assert.assertEquals(response.getStatusCode(), 200);
    }



    @Test(priority = 0, description = "获取某一个项目详情.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("获取某一个项目详情111.")
    @Feature("Project")
    @Story("Get project detail")
    public void projectDetail() {
        String data = "{Contract(\n" +
                "cond:\"{project:{_eq:99}}\",\n" +
                "order:\"\")\n" +
                " {\n" +
                " accumulated_amount\n" +
                " charge_frequency\n" +
                " contract_amount\n" +
                " customer\n" +
                " grant_loan_frequency\n" +
                " id\n" +
                " lease_balance\n" +
                " lease_end_time\n" +
                " lease_num\n" +
                " lease_start_time\n" +
                " lease_unit\n" +
                " leasing_principal\n" +
                " make_loan_day\n" +
                " overdue_amount\n" +
                " overdue_days\n" +
                " overdue_interest\n" +
                " overdue_principal\n" +
                " payment_method\n" +
                " project\n" +
                " invert_Project(\n" +
                " cond:\"\",\n" +
                " order:\"\")\n" +
                "  {\n" +
                "  business_mgr\n" +
                "  business_unit\n" +
                "  charge_frequency\n" +
                "  city\n" +
                "  province\n" +
                "  district\n" +
                "  class_level\n" +
                "  classification_level\n" +
                "  credit_amount\n" +
                "  detail_address\n" +
                "  discount_ratio\n" +
                "  expire_date\n" +
                "  guarantee_type\n" +
                "  id\n" +
                "  is_manufacture_buy_back\n" +
                "  is_manufacture_leasing\n" +
                "  manufacture\n" +
                "  no\n" +
                "  status\n" +
                "  name\n" +
                "  risk_mgr\n" +
                "  rent_type\n" +
                "  Refer_To_Lease_Group(\n" +
                "  cond:\"\",\n" +
                "  order:\"\")\n" +
                "   {\n" +
                "   asset_type\n" +
                "   count\n" +
                "   discount_ratio\n" +
                "   id\n" +
                "   lease_net_val\n" +
                "   lease_type\n" +
                "   lease_type_gb\n" +
                "   lease_type_yj\n" +
                "   nominal_cost\n" +
                "   project\n" +
                "   transfer_price\n" +
                "   unit_price\n" +
                "   Refer_To_Power_Station_Properties(\n" +
                "   cond:\"\",\n" +
                "   order:\"\")\n" +
                "    {\n" +
                "    ps_type\n" +
                "    structure\n" +
                "    avg_annual_eq_hours\n" +
                "    capacity\n" +
                "   }\n" +
                "  \n" +
                "  }\n" +
                "  \n" +
                " }\n" +
                " invert_Customer(\n" +
                " cond:\"\",\n" +
                " order:\"\")\n" +
                "  {\n" +
                "  actual_controller\n" +
                "  category\n" +
                "  cid\n" +
                "  city\n" +
                "  cname\n" +
                "  contact\n" +
                "  contact_detail\n" +
                "  ctype\n" +
                "  district\n" +
                "  enterprise_size\n" +
                "  group\n" +
                "  holding_type\n" +
                "  id\n" +
                "  is_connected_tx\n" +
                "  is_gov_fin_customer\n" +
                "  is_group_customer\n" +
                "  legal_person_id\n" +
                "  legal_person\n" +
                "  major_class\n" +
                "  middle_class\n" +
                "  office_address\n" +
                "  project\n" +
                "  province\n" +
                "  registered_address\n" +
                "  small_class\n" +
                " }\n" +
                "  \n" +
                "} }";
        Response response = JinzuEndpoint.graphQuery(data);
        Assert.assertEquals(response.getStatusCode(), 200);
    }


    @Test(priority = 0, description = "通过项目类型对项目进行过滤.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("通过项目类型对项目进行过滤.")
    @Feature("Project")
    @Story("Get Project by condition")
    public void projectFilter() {
        String data = "{\n" +
                "        Project(cond:\"{status:{_eq:\\\"online\\\"},Lease_Group:{lease_type:{_eq:\\\"2\\\"}}}\",order:\"\")\n" +
                "        {\n" +
                "          business_mgr\n" +
                "          business_unit\n" +
                "          charge_frequency\n" +
                "          city\n" +
                "          province\n" +
                "          district\n" +
                "          class_level\n" +
                "          classification_level\n" +
                "          credit_amount\n" +
                "          detail_address\n" +
                "          discount_ratio\n" +
                "          expire_date\n" +
                "          guarantee_type\n" +
                "          id\n" +
                "          is_manufacture_buy_back\n" +
                "          is_manufacture_leasing\n" +
                "          manufacture\n" +
                "          no\n" +
                "          status\n" +
                "          name\n" +
                "          risk_mgr\n" +
                "          rent_type\n" +
                "          invert_Customer(cond:\"\",order:\"\")\n" +
                "          {\n" +
                "            actual_controller\n" +
                "            category\n" +
                "            cid\n" +
                "            city\n" +
                "            cname\n" +
                "            contact\n" +
                "            contact_detail\n" +
                "            ctype\n" +
                "            district\n" +
                "            enterprise_size\n" +
                "            group\n" +
                "            holding_type\n" +
                "            id\n" +
                "            is_connected_tx\n" +
                "            is_gov_fin_customer\n" +
                "            is_group_customer\n" +
                "            legal_person_id\n" +
                "            legal_person\n" +
                "            major_class\n" +
                "            middle_class\n" +
                "            office_address\n" +
                "            project\n" +
                "            province\n" +
                "            registered_address\n" +
                "            small_class\n" +
                "          }\n" +
                "        Restricted_By_Contract(cond:\"\",order:\"\")\n" +
                "        {\n" +
                "          accumulated_amount\n" +
                "          charge_frequency\n" +
                "          contract_amount\n" +
                "          customer\n" +
                "          grant_loan_frequency\n" +
                "          id\n" +
                "          lease_balance\n" +
                "          lease_end_time\n" +
                "          lease_num\n" +
                "          lease_start_time\n" +
                "          lease_unit\n" +
                "          leasing_principal\n" +
                "          make_loan_day\n" +
                "          overdue_amount\n" +
                "          overdue_days\n" +
                "          overdue_interest\n" +
                "          overdue_principal\n" +
                "          payment_method\n" +
                "          project\n" +
                "        }\n" +
                "        Refer_To_Lease_Group(cond:\"\",order:\"\")\n" +
                "        {\n" +
                "          asset_type\n" +
                "          count\n" +
                "          discount_ratio\n" +
                "          id\n" +
                "          lease_net_val\n" +
                "          lease_type\n" +
                "          lease_type_gb\n" +
                "          lease_type_yj\n" +
                "          nominal_cost\n" +
                "          project\n" +
                "          transfer_price\n" +
                "          unit_price\n" +
                "        }\n" +
                "        }\n" +
                "      }";
        Response response = JinzuEndpoint.graphQuery(data);
        Assert.assertEquals(response.getStatusCode(), 200);
    }



    @Test(priority = 0, description = "查看电站的逆变器信息.")
    @Severity(SeverityLevel.BLOCKER)
    @Description("查看电站的逆变器信息.")
    @Feature("Site")
    @Story("Site detail")
    public void siteDetail() {
        String data = "{\n" +
                "Site(cond:\"{id:{_eq:\\\"P000000666\\\"}}\",order:\"\"){\n" +
                "id\n" +
                "location\n" +
                "commissioning_date\n" +
                "state\n" +
                "power_station\n" +
                "Has_Device_Inverter{\n" +
                "site\n" +
                "pr\n" +
                "production\n" +
                "name\n" +
                "type\n" +
                "full_generation_hours}\n" +
                "}\n" +
                "}";
        Response response = JinzuEndpoint.leaseDetail(data);
        Assert.assertEquals(response.getStatusCode(), 200);
    }
}



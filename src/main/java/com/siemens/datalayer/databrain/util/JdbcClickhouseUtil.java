package com.siemens.datalayer.databrain.util;

import ru.yandex.clickhouse.ClickHouseConnection;
import ru.yandex.clickhouse.ClickHouseDataSource;
import ru.yandex.clickhouse.settings.ClickHouseProperties;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * clickhouse驱动
 */
public class JdbcClickhouseUtil {
    // 返回数据库连接
    public static Connection getConnect(String address){
        ClickHouseProperties properties = new ClickHouseProperties();
        ClickHouseDataSource clickHouseDataSource = new ClickHouseDataSource(address, properties);

        ClickHouseConnection conn = null;

        try {
            conn = clickHouseDataSource.getConnection();
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}

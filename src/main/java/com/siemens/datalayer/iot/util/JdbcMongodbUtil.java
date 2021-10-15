package com.siemens.datalayer.iot.util;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import java.util.*;

public class JdbcMongodbUtil {
    // 返回数据库连接
    public static MongoDatabase getConnect(String host, Integer port, String username, String password, String databasename){
        List<ServerAddress> adds = new ArrayList<>();
        // ServerAddress()两个参数分别为服务器地址和端口
        ServerAddress serverAddress = new ServerAddress(host,port);
        adds.add(serverAddress);

        List<MongoCredential> credentials = new ArrayList<>();
        // MongoCredential.createScramSha1Credential()三个参数分别为用户名，数据库名称和密码
        MongoCredential mongoCredential = MongoCredential.createScramSha1Credential(username,databasename,password.toCharArray());
        credentials.add(mongoCredential);

        // 通过连接认证获取MongoDB的连接
        MongoClient mongoClient = new MongoClient(adds,credentials);

        // 连接到数据库admin(因为用户名和密码在admin中)
        MongoDatabase mongoDatabase = mongoClient.getDatabase("admin");
        // 切换到数据库connector
        mongoDatabase = mongoClient.getDatabase("connector");

        // 返回连接数据库对象
        return mongoDatabase;
    }
}
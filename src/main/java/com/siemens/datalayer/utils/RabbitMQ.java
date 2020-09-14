package com.siemens.datalayer.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RabbitMQ {
    private String host;
    private String virturalHost;
    private String port;
    private String username;
    private String password;
    private String exchange;
    private String exchangeType;
    private boolean exchangeDurable;
    private String queue;
    private boolean queueDurable;
    private boolean autoAck;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getVirturalHost() {
        return virturalHost;
    }

    public void setVirturalHost(String virturalHost) {
        this.virturalHost = virturalHost;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(String exchangeType) {
        this.exchangeType = exchangeType;
    }

    public boolean getExchangeDurable() {
        return exchangeDurable;
    }

    public void setExchangeDurable(boolean exchangeDurable) {
        this.exchangeDurable = exchangeDurable;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public boolean getQueueDurable() {
        return queueDurable;
    }

    public void setQueueDurable(boolean queueDurable) {
        this.queueDurable = queueDurable;
    }

    public boolean getAutoAck() {
        return autoAck;
    }

    public void setAutoAck(boolean autoAck) {
        this.autoAck = autoAck;
    }


    public AMQPer initKPIAMQPer() throws IOException {
        return new AMQPer(Utils.loadRabbitMQConfig());
    }

    public AMQPer initSp5AMQPer() throws IOException {
        return new AMQPer(Utils.loadRabbitMQConfig());
    }

    public void simulateKPIProduce() {
        try {
            AMQPer mr = initKPIAMQPer();
            this.simulateProduce(mr, "simulation_result", "simulation_result", "HeapPump.json");
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void simulateSp5Produce() {
        try {
            AMQPer mr = initSp5AMQPer();
            this.simulateProduce(mr, "sp5_RT", "sp5_RT", "sp5.json");
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void simulateProduce(AMQPer mr, String exchange, String routingKey, String jsonFile){
        mr.setExchange(exchange);
        mr.setRoutingKey(routingKey);
        mr.setMessageRoutingKey(routingKey);

        ObjectMapper objMapper = new ObjectMapper();
        ExecutorService cachedPool = Executors.newCachedThreadPool();
        try {
            JsonNode rootNode = objMapper.readTree(new File(AMQPer.class.getClassLoader().getResource(jsonFile).getPath()));
            cachedPool.execute(new Runnable() {
                @Override
                public void run() {
                    mr.setMessage(rootNode.toPrettyString());
                    mr.produce(100);
                }
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public String simulateKPIConsume(String routingKey){
        try {
            AMQPer mr = initKPIAMQPer();
            return this.simulateConsume(mr, "datalayer.exchange.out", "iEMSofKPIAutomation", routingKey);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String simulateSp5Consume(String routingKey){
        try {
            AMQPer mr = initKPIAMQPer();
            return this.simulateConsume(mr,"datalayer.exchange.out", "iEMSofSp5Automation", routingKey);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String simulateConsume(AMQPer mr,String exchange, String queue, String routingKey){
        mr.setExchange(exchange);
        mr.setExchangeDurable(true);
        mr.setQueue(queue);
        mr.setQueueAutoDelete(true);
        mr.setRoutingKey(routingKey);

        String result = mr.consume(60);
        return result;
    }
}

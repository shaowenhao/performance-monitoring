package com.siemens.datalayer.utils;

import com.rabbitmq.client.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AMQPer {
    private static Logger logger = Logger.getLogger(AMQPer.class);

    private transient ConnectionFactory factory;
    private transient Connection connection;
    private transient Channel channel;
    private transient DefaultConsumer consumer;
    private transient String consumerTag;
    private String queue;
    private String host;
    private String virtualHost;
    private String port;
    private boolean isSSL;
    private String exchange;
    private String exchangeType;
    private String routingKey;
    private String username;
    private String password;
    private boolean queueRedeclare;
    private boolean queueDurable;
    private boolean queueAutoDelete;
    private boolean queueExclusive;
    private boolean exchangeRedeclare;
    private boolean exchangeAutoDelete;
    private boolean exchangeDurable;
    private boolean autoAck;
    private String messageTTL;
    private String messageExpires;


    public AMQPer() {
        exchangeDurable = false;
        exchangeAutoDelete = false;
        exchangeRedeclare = false;
        queueAutoDelete = false;
        queueDurable = false;
        queueExclusive = false;
        queueRedeclare = false;
        this.factory = new ConnectionFactory();
        this.factory.setRequestedHeartbeat(1);
    }

    public boolean getQueueDurable() {
        return queueDurable;
    }

    public void setQueueDurable(boolean content) {
        queueDurable = content;
    }

    public boolean getQueueAutoDelete() {
        return queueAutoDelete;
    }

    public void setQueueAutoDelete(boolean content) {
        queueAutoDelete = content;
    }

    public boolean getExchangeAutoDelete() {
        return exchangeAutoDelete;
    }

    public void setExchangeAutoDelete(boolean autoDelete) {
        exchangeAutoDelete = autoDelete;
    }

    public boolean getQueueExclusive() {
        return queueExclusive;
    }

    public void setQueueExclusive(boolean content) {
        queueExclusive = content;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public String getPort() {
        return port;
    }

    public int getPortAsInt() {
        return Integer.parseInt(port);
    }

    public void setPort(String port) {
        this.port = port;
    }

    public boolean isSSL() {
        return isSSL;
    }

    public void setSSL(boolean SSL) {
        isSSL = SSL;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
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

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    protected int getTimeoutAsInt() {
        return Integer.parseInt(getTimeout());
    }

    public String getTimeout() {
        return "1000";
    }

    public boolean getQueueRedeclare() {
        return queueRedeclare;
    }

    public void setQueueRedeclare(Boolean content) {
        this.queueRedeclare = content;
    }

    public String getMessageExpires() {
        return messageExpires;
    }

    public void setMessageExpires(String name) {
        this.messageExpires = name;
    }

    protected Integer getMessageExpiresAsInt() {
        return Integer.parseInt(getMessageExpires());
    }

    public String getMessageTTL() {
        return messageTTL;
    }

    public void setMessageTTL(String name) {
        this.messageTTL = name;
    }

    protected Integer getMessageTTLAsInt() {
        return Integer.parseInt(getMessageTTL());
    }

    public Boolean getExchangeRedeclare() {
        return exchangeRedeclare;
    }

    public void setExchangeRedeclare(Boolean content) {
        this.exchangeRedeclare = content;
    }

    public String getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(String name) {
        exchangeType = name;
    }

    public boolean getExchangeDurable() {
        return exchangeDurable;
    }

    public void setExchangeDurable(boolean durable) {
        exchangeDurable = durable;
    }

    protected void deleteQueue() throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        // use a different channel since channel closes on exception.
        Channel channel = createChannel();
        try {
            logger.info("Deleting queue " + getQueue());
            channel.queueDelete(getQueue());
        } catch (Exception ex) {
            logger.debug(ex.toString(), ex);
            // ignore it.
        } finally {
            if (channel.isOpen()) {
                channel.close();
            }
        }
    }

    private Map<String, Object> getQueueArguments() {
        Map<String, Object> arguments = new HashMap<String, Object>();

        if (getMessageTTL() != null && !getMessageTTL().isEmpty()) {
            arguments.put("x-message-ttl", getMessageTTLAsInt());
        }

        if (getMessageExpires() != null && !getMessageExpires().isEmpty()) {
            arguments.put("x-expires", getMessageExpiresAsInt());
        }

        return arguments;
    }


    protected void deleteExchange() throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        // use a different channel since channel closes on exception.
        Channel channel = createChannel();
        try {
            logger.info("Deleting exchange " + getExchange());
            channel.exchangeDelete(getExchange());
        } catch (Exception ex) {
            logger.debug(ex.toString(), ex);
            // ignore it.
        } finally {
            if (channel.isOpen()) {
                channel.close();
            }
        }
    }

    protected boolean initChannel() throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        Channel channel = getChannel();

        if (channel != null && !channel.isOpen()) {
            logger.warn("channel " + channel.getChannelNumber()
                    + " closed unexpectedly: ", channel.getCloseReason());
            channel = null; // so we re-open it below
        }

        if (channel == null) {
            channel = createChannel();
            setChannel(channel);

            //TODO: Break out queue binding
            boolean queueConfigured = (getQueue() != null && !getQueue().isEmpty());

            if (queueConfigured) {
                if (getQueueRedeclare()) {
                    deleteQueue();
                }

                AMQP.Queue.DeclareOk declareQueueResp = channel.queueDeclare(getQueue(), getQueueDurable(), getQueueExclusive(), getQueueAutoDelete(), getQueueArguments());
            }

            if (!StringUtils.isBlank(getExchange())) { //Use a named exchange
                if (getExchangeRedeclare()) {
                    deleteExchange();
                }

                AMQP.Exchange.DeclareOk declareExchangeResp = channel.exchangeDeclare(getExchange(), getExchangeType(), getExchangeDurable(), getExchangeAutoDelete(), Collections.<String, Object>emptyMap());
                if (queueConfigured) {
                    channel.queueBind(getQueue(), getExchange(), getRoutingKey());
                }
            }

            logger.info("bound to:"
                    + "\n\t queue: " + getQueue()
                    + "\n\t exchange: " + getExchange()
                    + "\n\t exchange(D)? " + getExchangeDurable()
                    + "\n\t routing key: " + getRoutingKey()
                    + "\n\t arguments: " + getQueueArguments()
            );

        }
        return true;
    }


    protected Channel createChannel() throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        logger.info("Creating channel " + getVirtualHost() + ":" + getPortAsInt());

        if (connection == null || !connection.isOpen()) {
            factory.setConnectionTimeout(getTimeoutAsInt());
            factory.setVirtualHost(getVirtualHost());
            factory.setUsername(getUsername());
            factory.setPassword(getPassword());
            if (connectionSSL()) {
                factory.useSslProtocol("TLS");
            }

            logger.info("RabbitMQ ConnectionFactory using:"
                    + "\n\t virtual host: " + getVirtualHost()
                    + "\n\t host: " + getHost()
                    + "\n\t port: " + getPort()
                    + "\n\t username: " + getUsername()
                    + "\n\t password: " + getPassword()
                    + "\n\t timeout: " + getTimeout()
                    + "\n\t heartbeat: " + factory.getRequestedHeartbeat()
                    + "\nin " + this
            );

            String[] hosts = getHost().split(",");
            Address[] addresses = new Address[hosts.length];
            for (int i = 0; i < hosts.length; i++) {
                addresses[i] = new Address(hosts[i], getPortAsInt());
            }
            logger.info("Using hosts: " + Arrays.toString(hosts) + " addresses: " + Arrays.toString(addresses));
            connection = factory.newConnection(addresses);
        }

        Channel channel = connection.createChannel();
        if (!channel.isOpen()) {
            logger.error("Failed to open channel: " + channel.getCloseReason().getLocalizedMessage());
        }
        return channel;
    }

    private boolean connectionSSL() {
        return isSSL;
    }

    /**
     * @return the whether or not to auto ack
     */
    public boolean getAutoAck() {
        return autoAck;
    }

    public void setAutoAck(boolean content) {
        autoAck = content;
    }


    public void consume(){
        final BlockingQueue<String> response = new ArrayBlockingQueue<String>(1);
        try {
            initChannel();
            if (consumer == null) {
                logger.info("Creating consumer");
                consumer = new DefaultConsumer(channel){
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                        logger.info("get message+++++++++++");
                        response.offer(new String(body, "UTF-8"));
                    }
                };
            }
            if (consumerTag == null) {
                logger.info("Starting basic consumer");
                consumerTag = channel.basicConsume(getQueue(), getAutoAck(), consumer);
            }
        } catch (Exception ex) {
            logger.error("Failed to initialize channel", ex);
        }
        try {
            for (int i = 0; i < 1000; i++) {
                String result = response.poll(5000, TimeUnit.MILLISECONDS);
                logger.info("get message:" + result);
            }
        } catch (Exception ex) {
            logger.error("Failed to initialize channel", ex);
        }
    }

    public static void main(String[] args) {
        AMQPer mr = new AMQPer();
        mr.setHost("140.231.89.97");
        mr.setVirtualHost("/");
        mr.setPort("5672");
        mr.setUsername("guest");
        mr.setPassword("guest");
        mr.setExchange("datalayer.exchange.out");
        mr.setExchangeType("topic");
        mr.setExchangeDurable(true);
        mr.setQueue("iEMS555");
        mr.setQueueDurable(true);
        mr.setRoutingKey("c03462b4b4d9aaa56cdb978520d00f26");
        mr.setAutoAck(true);
        mr.consume();
    }
}
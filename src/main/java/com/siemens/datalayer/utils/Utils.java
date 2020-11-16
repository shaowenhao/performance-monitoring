package com.siemens.datalayer.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.log4j.Logger;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {
    private static Logger logger = Logger.getLogger(Utils.class);

    public static boolean equalLists(List<String> one, List<String> two) {
        if (one == null && two == null) {
            return true;
        }

        if ((one == null && two != null)
                || one != null && two == null
                || one.size() != two.size()) {
            return false;
        }

        //to avoid messing the order of the lists we will use a copy
        //as noted in comments by A. R. S.
        one = new ArrayList<String>(one);
        two = new ArrayList<String>(two);

        Collections.sort(one);
        Collections.sort(two);
        return one.equals(two);
    }

    public static boolean isNullOrEmpty( final Collection< ? > c ) {
        return c == null || c.isEmpty();
    }

    public static boolean isNullOrEmpty( final Map< ?, ? > m ) {
        return m == null || m.isEmpty();
    }

    public static <T> boolean haveSamePropertyValues (Class<T> type, T t1, T t2)
            throws Exception {

        BeanInfo beanInfo = Introspector.getBeanInfo(type);
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            Method m = pd.getReadMethod();
            Object o1 = m.invoke(t1);
            Object o2 = m.invoke(t2);
            if (!Objects.equals(o1, o2)) {
                logger.info(String.format("Value not same when compare with %s", m.toString()));
                return false;
            }
        }
        return true;
    }

    public static JsonNode loadTestConfig() throws IOException {
        String jsonFile = "TestConfig.yml";
        ObjectMapper objMapper = new ObjectMapper(new YAMLFactory());
        JsonNode rootNode = objMapper.readTree(new File(Utils.class.getClassLoader().getResource(jsonFile).getPath()));
        return rootNode;
    }

    public static RabbitMQ loadRabbitMQConfig() throws IOException {
        JsonNode rootNode = Utils.loadTestConfig();
        ObjectMapper objMapper = new ObjectMapper(new YAMLFactory());
        return objMapper.readValue(rootNode.get("rabbitmq").toString(), RabbitMQ.class);
    }

    public static List<?> flatten(List<?> list){
        return list.stream()
                .flatMap(e -> e instanceof List ? flatten((List<?>) e).stream() : Stream.of(e))
                .collect(Collectors.toList());
    }

}

package com.siemens.datalayer.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Utils {
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

}

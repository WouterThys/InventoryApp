package com.waldo.inventory.Utils.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

abstract class Resource {

    private Properties properties;

    void initProperties(String propertiesUrl, String fileName) throws IOException {
        properties = new Properties();

        String resourceFileName = propertiesUrl + fileName;
        InputStream input = getClass().getClassLoader().getResourceAsStream(resourceFileName);
        properties.load(input);
    }

    public String readString(String key) {
        return properties.getProperty(key);
    }
}

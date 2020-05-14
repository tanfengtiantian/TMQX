package org.tmqx.core;

import org.tmqx.common.config.ApplicationConfig;
import org.yaml.snakeyaml.Yaml;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;

public enum SnifferConfigInitializer {
    INSTANCE;
    private ApplicationConfig applicationConfig;
    private final String APPLICATION_CONFIG = "application.yml";
    private final String FILE_PREFIX = "file:/";

    public synchronized ApplicationConfig initialize() throws FileNotFoundException {
        if(applicationConfig == null) {
            applicationConfig = new Yaml().loadAs(getFile(APPLICATION_CONFIG), ApplicationConfig.class);
        }
        return applicationConfig;
    }

    private FileInputStream getFile(String filename) throws FileNotFoundException {
        String fullfilename = "";
        if (filename == null || filename.length() <= 0) {
            throw new RuntimeException("conf file name can not be empty");
        }
        //说明是绝对路径
        if (filename.indexOf(":") > -1 || filename.indexOf("/") > -1 || filename.indexOf("\\") > -1) {
            fullfilename = filename;
        } else {
            fullfilename = getFullfilename(filename);
        }

        return new FileInputStream(fullfilename);
    }

    private String getFullfilename(String filename) {
        try {
            URL url = ApplicationConfig.class.getClassLoader().getResource(filename);
            String urlstring = url.toString();
            if (urlstring.startsWith(FILE_PREFIX)) {
                urlstring = urlstring.substring(FILE_PREFIX.length());
                if (urlstring.indexOf(":") < 0) {
                    urlstring = "/" + urlstring;
                }
            }
            return urlstring;
        } catch (Throwable e) {
            throw new RuntimeException("can not find file:" + filename, e);
        }
    }

}
package org.tmqx.common.config;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Map;

public class ApplicationConfig {

    private static final String FILE_PREFIX = "file:/";

    private static final String APPLICATION_CONFIG = "application.yml";

    public static final String FileSuffix = ".conf";

    public static void initialize() throws Exception {
        Map map = new Yaml().loadAs(getFile(APPLICATION_CONFIG), Map.class);
        //Map map = new Yaml().load(getFile(APPLICATION_CONFIG), Map.class));
        System.out.println(map.size());

    }

    private static FileInputStream getFile(String filename) throws FileNotFoundException {
        String fullfilename = "";
        if(filename == null || filename.length() <= 0){
            throw new RuntimeException("conf file name can not be empty");
        }
        //说明是绝对路径
        if(filename.indexOf(":")>-1 || filename.indexOf("/")>-1 || filename.indexOf("\\")>-1 ){
            fullfilename = filename;
        }else{
            fullfilename = getFullfilename(filename);
        }

        return new FileInputStream(fullfilename);
    }

    private static String getFullfilename(String filename){
        try{
            URL url = ApplicationConfig.class.getClassLoader().getResource(filename);
            String urlstring = url.toString();
            if(urlstring.startsWith(FILE_PREFIX)){
                urlstring = urlstring.substring(FILE_PREFIX.length());
                if(urlstring.indexOf(":")<0){
                    urlstring = "/"+urlstring;
                }
            }
            return urlstring;
        }catch(Throwable e){
            throw new RuntimeException("can not find file:"+filename, e);
        }
    }
    
}

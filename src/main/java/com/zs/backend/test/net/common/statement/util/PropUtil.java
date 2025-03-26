package com.zs.backend.test.net.common.statement.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @desc 属性文件工具类
 * @create 2022-05-06 10:03
 **/


public class PropUtil {
    private static Logger log = LoggerFactory.getLogger(PropUtil.class);
    // 默认配置文件路径classpath:config.properties
    private String properiesName = "config.properties";
    public PropUtil() {

    }

    /**
     * @param fileName properties文件全路径
     */
    public PropUtil(String fileName) {
        String realPath = findFile(PropUtil.class.getClassLoader(), fileName);
        log.info("PropUtil realPath={}",realPath);
        this.properiesName = realPath;
    }

    /**
     * 读取key对应的value值
     * @param key
     * @return
     */
    public String get(String key) {
        String value = "";
        InputStream is = null;
        try {
            is = new FileInputStream(this.properiesName);
            Properties p = new Properties();
            p.load(is);
            value = p.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    /**
     * 读取key对应的value值
     * @param key
     * @return
     */
    public String get(String key,String defaultValue) {
        String value= get(key.trim());
        if (StringUtils.isBlank(value)){
            value = defaultValue;
        }
        return value.trim();
    }

    /**
     * 返回Properties对象
     * @return
     */
    public Properties getProperties() {
        Properties p = new Properties();
        InputStream is = null;
        try {
            is = PropUtil.class.getClassLoader().getResourceAsStream(properiesName);
            p.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return p;
    }

    /**
     * 往properties文件中写入key-value键值对
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        InputStream is = null;
        OutputStream os = null;
        Properties p = new Properties();
        try {
            is = new FileInputStream(PropUtil.class.getClassLoader().getResource(properiesName).getFile());
            p.load(is);
            os = new FileOutputStream(PropUtil.class.getClassLoader().getResource(properiesName).getFile());

            p.setProperty(key, value);
            p.store(os, key);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != is){
                    is.close();
                }
                if (null != os){
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 获取文件真实地址
     * @param classLoader
     * @param path
     * @return
     */
    public static String findFile(ClassLoader classLoader, String path) {

        if (path != null) {
            URL result = null;
            try {
                result = new URL(path);
                log.info("ConfigLoader result= {}",result);
                return result.getPath();
            } catch (MalformedURLException e) {
                log.info("ConfigLoader catch= {}",e.getMessage());
                URL url = classLoader.getResource(path);
                log.info("ConfigLoader getResource= {}",url);
                if( url != null ){
                    return  url.getPath();
                }
                String realPath = StringUtils.EMPTY;//真实磁盘地址
                URL systemResource = PropUtil.class.getClassLoader().getSystemResource("");
                log.info("ConfigLoader getSystemResource= {}",systemResource);
                if( systemResource != null ){
                    realPath = systemResource.getPath() + "/" + path;
                    log.info("ConfigLoader getSystemResource path = {}",realPath);
                }else{
                    String userDir = System.getProperty("user.dir");//当前用户目录的相对路径
                    log.info("ConfigLoader user.dir= {}",userDir);
                    if( !StringUtils.isEmpty(userDir) ){
                        realPath = userDir + "/" + path;
                        log.info("ConfigLoader user.dir path = {}",realPath);
                    }
                }
                if( !StringUtils.isEmpty(realPath) ){
                    return realPath;
                }
            }
        }

        return null;
    }


}

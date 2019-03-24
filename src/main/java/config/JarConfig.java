package config;

import java.io.Serializable;

public class JarConfig implements Serializable {

    private static volatile JarConfig instance;

    private String token;

    private String url;

    private Integer limitOnList;

    public static void setInstance(final String url, final String token, final Integer limit){
        if(instance == null){
            synchronized (JarConfig.class){
                if(instance == null){
                    instance = new JarConfig();
                    instance.setUrl(url);
                    instance.setToken(token);
                    instance.setLimitOnList(limit);
                }
            }
        }
    }

    public static JarConfig getInstance(){
        return instance;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getLimitOnList() {
        return limitOnList;
    }

    public void setLimitOnList(Integer limitOnList) {
        this.limitOnList = limitOnList;
    }
}

package org.tmqx.common.config;

public class BrokerConfig {

    private String tmqxHome = System.getProperty("tmqxHome",System.getenv("TMQX_HOME"));

    private String version = "1.0.0";

    private boolean anonymousEnable = true;

    private int pollThreadNum = Runtime.getRuntime().availableProcessors() * 2;

    public int getPollThreadNum() {
        return pollThreadNum;
    }

    public void setPollThreadNum(int pollThreadNum) {
        this.pollThreadNum = pollThreadNum;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isAnonymousEnable() {
        return anonymousEnable;
    }

    public void setAnonymousEnable(boolean anonymousEnable) {
        this.anonymousEnable = anonymousEnable;
    }

    public String getTmqxHome() {
        return tmqxHome;
    }

    public void setTmqxHome(String tmqxHome) {
        this.tmqxHome = tmqxHome;
    }
}

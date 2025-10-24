package de.larsensmods.mctrains.config;

public class MCTConfig {
    public boolean enableCartChaining = true;

    public MCTConfig() {}

    // 保留两种风格的访问器以兼容现有调用
    public boolean enableCartChaining() {
        return enableCartChaining;
    }

    public boolean isEnableCartChaining() {
        return enableCartChaining;
    }

    public void setEnableCartChaining(boolean enableCartChaining) {
        this.enableCartChaining = enableCartChaining;
    }
}
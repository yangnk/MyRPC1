package balance;

public enum LoadBalancePolicy {

    RANDOM("random", "随机");

    /**
     * 策略名称
     */
    private String name;

    /**
     * 策略描述
     */

    private String desc;

    /**
     * 随机
     */

    LoadBalancePolicy(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}

package test;

import org.springframework.beans.factory.InitializingBean;

public class InitBeanTest implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("=== InitBeanTest.afterPropertiesSet() ===");
    }
}
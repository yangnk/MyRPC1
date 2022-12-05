package org.example.consumer;

import org.example.api.DemoService;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import utils.SpringUtil;

public class Demo {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/consumer.xml");
        context.start();
        DemoService demoService = SpringUtil.getApplicationContext().getBean("demoService", DemoService.class);
        String hello = demoService.hello("world");
        System.out.println("result: " + hello);
    }
}

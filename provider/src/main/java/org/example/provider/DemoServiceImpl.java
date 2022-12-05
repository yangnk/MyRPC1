package org.example.provider;

import org.example.api.DemoService;

import java.util.Date;

public class DemoServiceImpl implements DemoService {
    @Override
    public String hello(String name) {
        return name + ":" + new Date();
    }
}

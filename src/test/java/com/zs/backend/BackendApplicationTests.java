package com.zs.backend;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BackendApplicationTests {

    @Test
    void contextLoads() {
        String s = "  ";
        System.out.println("test s : " + StringUtils.isNotBlank(s));
        System.out.println("test s : " + StringUtils.isNotEmpty(s));

    }

}

package info.kqkd.cloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("info.kqkd.cloud.dao")
public class ApiFileApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiFileApplication.class, args);
    }

}

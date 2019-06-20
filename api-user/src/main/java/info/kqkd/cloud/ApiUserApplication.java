package info.kqkd.cloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "info.kqkd.cloud.*")
@MapperScan("info.kqkd.cloud.dao")
public class ApiUserApplication {

    public static void main(String[] args) {

        SpringApplication.run(ApiUserApplication.class, args);
    }

}

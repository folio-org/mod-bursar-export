package org.folio.bursar.export;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BursarExportApplication {

  public static void main(String[] args) {
    SpringApplication.run(BursarExportApplication.class, args);
  }

}

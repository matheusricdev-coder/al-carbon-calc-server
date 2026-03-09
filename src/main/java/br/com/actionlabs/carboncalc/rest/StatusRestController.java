package br.com.actionlabs.carboncalc.rest;


import br.com.actionlabs.carboncalc.dto.ServerStatusDTO;
import java.util.Date;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/status")
@RequiredArgsConstructor
@Slf4j
public class StatusRestController {

  @Value("${server.version}")
  private String version;


  @ResponseBody
  @GetMapping("/check")
  public ServerStatusDTO checkStatus() {
    long currentTimeMillis = System.currentTimeMillis();
    return new ServerStatusDTO(version, currentTimeMillis, new Date(currentTimeMillis).toString());
  }

}


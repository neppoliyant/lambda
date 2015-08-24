package com.digitalsanctum.lambda.ws;

import com.digitalsanctum.lambda.Executor;
import java.lang.Exception;
import java.lang.String;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/hello")
public class Endpoint {
  @Autowired
  private Executor executor;

  @POST
  public String message(String input) throws Exception {
    return (String) executor.execute(input).getResult();
  }
}

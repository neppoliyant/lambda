package com.digitalsanctum.lambda.ws;

import com.digitalsanctum.lambda.Executor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Component
@Path("/hello")
public class Endpoint {

    private Executor executor;

    @Autowired
    public Endpoint(Executor executor) {
        this.executor = executor;
    }

    @GET
    public String message(@QueryParam("name") String input) throws Exception {
        return (String) executor.execute(input).getResult();
    }

    /*@POST
    public String message(@RequestBody String input) throws Exception {
        return (String) executor.execute(input).getResult();
    }*/
}
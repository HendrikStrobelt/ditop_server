package de.hs8.ditop.services;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hen on 10/27/14.
 */


@RestController
public class Info {


    @Value("${localStorage.dir}") String dataDir;

    @RequestMapping("/hello")
    public String greeting(@RequestParam(value = "name", defaultValue = "${localStorage.dir}"  ) String name){

        return name  + ": " + dataDir + ".. ";
    }



}

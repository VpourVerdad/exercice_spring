package fr.xavierristat.exercicespring.feature;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hello")
public class HelloController {

    @GetMapping(value = "/world", produces = MediaType.TEXT_PLAIN_VALUE)
    public String helloWorld() {
        return "Hello World!";
    }



}

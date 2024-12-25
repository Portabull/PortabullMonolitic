package com.portabull.git;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class BuildController {

    private final BuildService buildService;

    public BuildController(BuildService buildService) {
        this.buildService = buildService;
    }

//    @GetMapping("send")
//    public void deployJar() throws IOException {
//        buildService.deployJar();
//    }
}

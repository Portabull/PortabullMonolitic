package com.portabull.gateway;

import com.portabull.utils.fileutils.FileHandling;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class RoutingPages {

    @RequestMapping("admin")
    public ModelAndView handleAdminPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("adminlogin.html");
        return modelAndView;
    }

    @RequestMapping("portabull")
    public ModelAndView handlePortabullWebsite() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("portabull.html");
        return modelAndView;
    }

    @RequestMapping("approve-request")
    public ModelAndView handleApproval(@RequestParam(required = false) String token,
                                       @RequestParam(required = false) String approval) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("notifyapproval.html?token=" + token + "&approval=" + approval);
        return modelAndView;
    }

    @PostMapping("get-image-via-gateway")
    public ResponseEntity<?> getImageViaGateway(@RequestBody List<String> fileNames) {

        Map<String, String> response = new HashMap<>();

        fileNames.forEach(fileName -> {

            try {

                byte[] staticFileAsBytes = FileHandling.getStaticFileAsBytes(fileName);

                response.put(fileName, "data:" + ";base64," + Base64.getEncoder().encodeToString(staticFileAsBytes));

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

}

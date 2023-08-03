package com.portabull.gateway;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

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

}

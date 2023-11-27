package com.portabull.router;

import com.portabull.router.operation.GenericRouterOperation;
import com.portabull.utils.RequestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@RestController
public class GenericRouterController {


    @Autowired
    GenericRouterOperation operatorClass;

    @PostMapping("/do-invoke")
    public ResponseEntity<?> doInvoke(@RequestBody Map<String, Object> payload) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {

        String serviceId = RequestHelper.getCurrentRequest().getHeader("serviceId");

        Object o = operatorClass.invokeServiceCall(serviceId, payload);

        return new ResponseEntity<>(o, HttpStatus.OK);

    }
}

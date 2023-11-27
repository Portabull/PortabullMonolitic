package com.portabull.utils.annotation;


import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.response.PortableResponse;

import java.util.Map;

@WebService("khfe")
public class TestClass {


    @ServiceMapping("/uwygf")
    public PortableResponse kujwhrf(Map<String, Object> payload) {
        System.out.println("Done SUCCESS");
        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, null);
    }


}

package com.portabull.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portabull.cache.LoginUtils;
import com.portabull.response.PortableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private List<String> skipEndPoints;

    @Autowired
    LoginUtils loginUtils;

    @Value("${skip.filters.endpoint}")
    public void setSkipEndpoints(String skipEndpoint) {
        skipEndPoints = Arrays.asList(skipEndpoint.split(","));
    }

    List<String> filters = Arrays.asList("/APIGateway/UM", "/APIGateway/DMS", "/APIGateway/gs",
            "/APIGateway/MIS", "/APIGateway/oauth", "/APIGateway/spf");

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {


        if (filters.stream().anyMatch(filter -> httpServletRequest.getRequestURI().startsWith(filter))) {
            PortableResponse response;
            final String token = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
            if (skipEndPoints.stream().noneMatch(endPoint -> endPoint.equalsIgnoreCase(httpServletRequest.getRequestURI()))) {
                response = loginUtils.isValidToken(token != null ? token.replace("Bearer ", "") : "", httpServletRequest.getRequestURI());
            } else {
                response = new PortableResponse().setStatusCode(200L);
            }

            if (response == null || response.getStatusCode() == 401) {
                Map<String, Object> errorDetails = new HashMap<>();
                errorDetails.put("message", "Unauthorized");
                errorDetails.put("status", "FAILED");
                if (!"Unauthorized Access".equalsIgnoreCase(response.getMessage()))
                    errorDetails.put("desc", response.getMessage());

                PrintWriter out = httpServletResponse.getWriter();
                out.write("");
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(httpServletResponse.getWriter(), errorDetails);
                return;
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }


}

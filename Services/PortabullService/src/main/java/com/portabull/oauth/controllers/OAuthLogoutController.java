package com.portabull.oauth.controllers;

import com.portabull.constants.MessageConstants;
import com.portabull.execption.SingleSignInEnabledException;
import com.portabull.generic.models.UserDocumentStorage;
import com.portabull.oauth.service.UserOAuthServices;
import com.portabull.response.PortableResponse;
import com.portabull.um.UserCredentials;
import com.portabull.um.utils.JwtUtil;
import com.portabull.utils.commonutils.CommonUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import org.springframework.web.util.WebUtils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("oauth")
public class OAuthLoginController {

@RequestMapping("/plogout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        Cookie sessionCookie = WebUtils.getCookie(request, "JSESSIONID");
        
        if (sessionCookie != null) {
            // Invalidate the session associated with the session ID
            request.getSession().invalidate();
            
            // Expire the session ID cookie by setting its max age to 0
            sessionCookie.setMaxAge(0);
            response.addCookie(sessionCookie);
        }
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.setAuthenticated(false);
        new SecurityContextLogoutHandler().logout(request, response, authentication);
        SecurityContextHolder.clearContext();
        request.logout();
        request.getSession().invalidate();
    }

}

package com.portabull.oauth.controllers;

import com.portabull.constants.MessageConstants;
import com.portabull.execption.SingleSignInEnabledException;
import com.portabull.oauth.services.UserOAuthServices;
import com.portabull.um.UserCredentials;
import com.portabull.um.utils.JwtUtil;
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("oauth")
public class LoginController {


    @Autowired
    JwtUtil jwtTokenUtil;

    @Autowired
    UserOAuthServices userOAuthServices;

    static String BASE_URL;

    @Value("${portabull.home.page.url}")
    public synchronized void setEmailFrom(String baseUrl) {
        LoginController.BASE_URL = baseUrl;
    }

    @GetMapping("/sign-in-with-gmail")
    public String signInWithGmail(HttpServletRequest request, Model model) {
        model.addAttribute("domainAddressUrl", BASE_URL);
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            Object userName = ((DefaultOidcUser) principal).getAttributes().get("email");

            userOAuthServices.registerIfUserNotExists(userName.toString());

            return createAuthenticationToken(userName.toString(), model);
        } catch (SingleSignInEnabledException e) {
            model.addAttribute("displayErrorMessage", e.getMessage());
            model.addAttribute("displayError", true);
            return "lockedpage.html";
        }
    }

    public String createAuthenticationToken(String userName, Model model) {

        UserCredentials userCredentials = userOAuthServices.getUserCredentials(userName);

        if (BooleanUtils.isTrue(userCredentials.getAccountLocked()) || (userCredentials.getWrongPasswordCount() != null && userCredentials.getWrongPasswordCount() >= 3)) {
            model.addAttribute("isAccountLocked", true);
            model.addAttribute("notifyEmailAddress", userCredentials.getLoginUserName());
            model.addAttribute("message", (userCredentials.getWrongPasswordCount() != null && userCredentials.getWrongPasswordCount() >= 3) ?
                    MessageConstants.ACCOUNT_LOCKED : MessageConstants.ADMIN_ACCOUNT_LOCKED);
            return "lockedpage.html";
        }

        userOAuthServices.updateWrongPasswordCount(userCredentials, true);

        final String jwt = jwtTokenUtil.generateToken(userCredentials, false);

        model.addAttribute("token", jwt);

        model.addAttribute("userName", userCredentials.getLoginUserName());

        model.addAttribute("twoStepVer", BooleanUtils.isTrue(userCredentials.getTwoStepVerificationEnabled()));

        return "loginrouter";
    }

    @RequestMapping("/plogout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.setAuthenticated(false);
        new SecurityContextLogoutHandler().logout(request, response, authentication);
        SecurityContextHolder.clearContext();
        request.logout();
        request.getSession().invalidate();
    }

}

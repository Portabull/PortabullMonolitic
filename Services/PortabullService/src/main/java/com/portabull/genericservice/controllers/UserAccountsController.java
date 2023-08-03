package com.portabull.genericservice.controllers;

import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.execption.BadRequestException;
import com.portabull.genericservice.service.UserAccountService;
import com.portabull.response.PortableResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("gs/user-accounts")
public class UserAccountsController {

    @Autowired
    UserAccountService userAccountService;

    @PostMapping("/save-user-account")
    public ResponseEntity<?> saveUserAccount(@RequestBody Map<String, Object> payload) {
        return new ResponseEntity<>(userAccountService.saveUserAccount(payload), HttpStatus.OK);
    }


    @PostMapping("/get-user-accounts")
    public ResponseEntity<?> getUserAccounts(@RequestBody Map<String, Object> payload) {

        if (payload.get("pageNo") == null) {
            return new ResponseEntity<>(new PortableResponse("Page Number is mandatory",
                    StatusCodes.C_400, PortableConstants.FAILED, null), HttpStatus.BAD_REQUEST);
        }

        Long pageNo = Long.valueOf(payload.get("pageNo").toString());

        Long resultSize = StringUtils.isEmpty(payload.get("resultSize")) ? 10 : Long.valueOf(payload.get("resultSize").toString());

        pageNo = pageNo * resultSize;

        return new ResponseEntity<>(userAccountService.getUserAccounts(pageNo, resultSize),
                HttpStatus.OK);
    }

    @PostMapping("/search-user-account")
    public ResponseEntity<?> searchUserAccount(@RequestBody Map<String, Object> payload) {

        if (StringUtils.isEmpty(payload.get("search"))) {
            throw new BadRequestException("Search is Empty");
        }

        return new ResponseEntity<>(userAccountService.searchUserAccount(payload.get("search").toString()), HttpStatus.OK);
    }


    @PostMapping("/delete-user-accounts")
    public ResponseEntity<?> deleteUserAccounts(@RequestBody Map<String, Object> payload) {

        if (StringUtils.isEmpty(payload.get("accountIds"))) {
            throw new BadRequestException("accountIds is Empty");
        }

        List<Long> accountIds = (List<Long>) payload.get("accountIds");

        return new ResponseEntity<>(userAccountService.deleteUserAccounts(accountIds), HttpStatus.OK);
    }


}

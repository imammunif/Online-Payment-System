package com.dansmultipro.ops.controller;

import com.dansmultipro.ops.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("users")
public class UserActivationController {

    private final UserService userService;

    public UserActivationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/activate")
    public String activateUserCustomer(@RequestParam String email, @RequestParam String code, Model model) {
        try {
            userService.activateUserCustomer(email, code);
            return "user-activation-succeeded";
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            model.addAttribute("errorMessage", errorMessage);
            return "user-activation-failed";
        }
    }

}

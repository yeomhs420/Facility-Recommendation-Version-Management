package com.example.project.direction.controller;

import com.example.project.direction.dto.UserDto;
import com.example.project.direction.entity.UserEntity;
import com.example.project.direction.repository.UserRepository;
import com.example.project.direction.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/join")
public class JoinController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HttpSession session;
    @Autowired
    private LoginService loginService;

    @GetMapping({"/", ""})
    public String Join(){
        return "/join";
    }

    @PostMapping("/action")
    @ResponseBody
    public ResponseEntity<Map<String, String>> joinAction(@Valid UserDto userDto, BindingResult bindingResult){// jquery 의 serialize 를 이용
        Map<String, String> error_text = new HashMap<>();
        if(bindingResult.hasErrors()){
            if(!bindingResult.getFieldErrors("userId").isEmpty())
                error_text.put("id_error", bindingResult.getFieldErrors("userId").get(0).getDefaultMessage());
            if(!bindingResult.getFieldErrors("password").isEmpty()) {
                error_text.put("pw_error", bindingResult.getFieldErrors("password").get(0).getDefaultMessage());
            }
            return new ResponseEntity<>(error_text, HttpStatus.BAD_REQUEST);
        }

        if(loginService.create(userDto, error_text) == true) {
            return new ResponseEntity<>(error_text, HttpStatus.BAD_REQUEST);
        }

        UserEntity userEntity = userRepository.findByUserId(userDto.getUserId());

        session.setAttribute("user", userEntity);

        session.setMaxInactiveInterval(7200);

        return new ResponseEntity<>(error_text, HttpStatus.OK);
    }

}

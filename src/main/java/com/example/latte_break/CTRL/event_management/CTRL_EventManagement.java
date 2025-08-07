package com.example.latte_break.CTRL.event_management;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CTRL_EventManagement {
    @RequestMapping("event_management")
    public ModelAndView event_management(HttpServletRequest request) {
        HttpSession session = request.getSession();

        if (session.getAttribute("user_id") == null) {
            return new ModelAndView("redirect:/login");
        }

        ModelAndView mav = new ModelAndView("view/event_management/index");

        return mav;
    }
}

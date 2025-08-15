package com.example.latte_break.CTRL.event_management;

import com.example.latte_break.BEAN.event_management.BEAN_EventManagement;
import com.example.latte_break.DAO.event_management.DAO_EventManagement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("event_management")
public class CTRL_EventManagement {

    @Autowired
    DAO_EventManagement daoEventManagement;

    @RequestMapping("")
    public ModelAndView event_management(HttpServletRequest request) {
        HttpSession session = request.getSession();

        if (session.getAttribute("user_id") == null) {
            return new ModelAndView("redirect:/login");
        }

        ModelAndView mav = new ModelAndView("view/event_management/index");

        mav.addObject("users", daoEventManagement.getAllUser());

        return mav;
    }

    @RequestMapping("/ajax/getAllUsers")
    @ResponseBody
    public Map<String, Object> getAllUsers() {
        Map<String, Object> response = new HashMap<>();

        List<BEAN_EventManagement> list = daoEventManagement.getAllUser();

        response.put("data", list);

        return response;
    }

    @RequestMapping("/ajax/addEvent")
    @ResponseBody
    public Map<String, Object> addEvent(BEAN_EventManagement bean, HttpServletRequest request) {
        HttpSession session = request.getSession();

        int user_id = (int) session.getAttribute("user_id");

        Map<String, Object> response = new HashMap<>();
        String event_name = bean.getEvent_name();
        String purpose = bean.getPurpose();
        String date = bean.getDate();
        String time = bean.getTime();
        String participants_ids = bean.getParticipants_ids();
        String participants_name = bean.getParticipants_name();

        int res = daoEventManagement.addEvent(event_name, purpose, date, time, participants_name, participants_ids, user_id);

        if (res > 0) {
            response.put("status", "success");
            response.put("message", "Event Successfully Added!");
        } else {
            response.put("status", "failed");
            response.put("message", "Error on Saving Event");
        }
        return response;
    }

    @RequestMapping("/ajax/getAllEvent")
    @ResponseBody
    public Map<String, Object> getAllEvent() {
        Map<String, Object> response = new HashMap<>();

        List<BEAN_EventManagement> list = daoEventManagement.getAllEvent();

        response.put("data", list);
        
        return response;
    }
}

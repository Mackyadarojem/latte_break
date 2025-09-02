package com.example.latte_break.CTRL.event_management;

import com.example.latte_break.BEAN.event_management.BEAN_EventManagement;
import com.example.latte_break.BEAN.inventory.BEAN_ItemList;
import com.example.latte_break.BEAN.inventory.BEAN_ProductList;
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
    public Map<String, Object> getAllEvent(BEAN_EventManagement beanEventManagement) {
        Map<String, Object> response = new HashMap<>();

        String event_name = beanEventManagement.getEvent_name();
        String date_from = beanEventManagement.getDate_from();
        String date_to = beanEventManagement.getDate_to();

        List<BEAN_EventManagement> list = daoEventManagement.getAllEvent(event_name, date_from, date_to);

        response.put("data", list);

        return response;
    }

    @RequestMapping("/ajax/editEvent")
    @ResponseBody
    public Map<String, Object> editEvent(BEAN_EventManagement bean, HttpServletRequest request) {
        HttpSession session = request.getSession();
        int user_id = (int) session.getAttribute("user_id");

        Map<String, Object> response = new HashMap<>();
        String event_name = bean.getEvent_name();
        String purpose = bean.getPurpose();
        String date = bean.getDate();
        String time = bean.getTime();
        String participants_ids = bean.getParticipants_ids();
        String participants_name = bean.getParticipants_name();
        int id = bean.getId();

        int res = daoEventManagement.editEvent(event_name, purpose, date, time, participants_name, participants_ids, user_id, id);

        if (res > 0) {
            response.put("status", "success");
            response.put("message", "Event Successfully Updated!");
        } else {
            response.put("status", "failed");
            response.put("message", "Error on Updating Event");
        }
        return response;
    }

    @RequestMapping("/ajax/archiveEvent")
    @ResponseBody
    public Map<String, Object> archiveEvent(BEAN_EventManagement bean, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        Map<String, Object> response = new HashMap<>();

        int res = daoEventManagement.archiveEvent(bean.getId(), username);

        if (res > 0) {
            response.put("status", "success");
            response.put("message", "Event Successfully Archived!");
        } else {
            response.put("status", "failed");
            response.put("message", "Error on Archiving Event");
        }

        return response;
    }

    @RequestMapping("/ajax/getAllArchiveEvent")
    @ResponseBody
    public Map<String, Object> getAllArchiveEvent(){
        Map<String, Object> response = new HashMap<>();

        List<BEAN_EventManagement> list = daoEventManagement.getArchiveEvent();

        response.put("data", list);

        return response;
    }

    @RequestMapping("ajax/deleteEvent")
    @ResponseBody
    public Map<String, Object> deleteEvent(BEAN_EventManagement bean) {
        Map<String, Object> result = new HashMap<>();
        int id = bean.getId();

        int res = daoEventManagement.deleteEvent(id);
        if (res > 0) {
            result.put("status", "success");
            result.put("message", "Event Successfully Deleted!");
        } else {
            result.put("status", "failed");
            result.put("message", "Fail to Delete Event!");
        }
        return result;
    }

    @RequestMapping("ajax/restoreEvent")
    @ResponseBody
    public Map<String, Object> restoreEvent(BEAN_EventManagement bean, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Map<String, Object> result = new HashMap<>();

        String username = (String) session.getAttribute("username");
        int id = bean.getId();

        int res = daoEventManagement.restoreEvent(username, id);

        if (res > 0) {
            result.put("status", "success");
            result.put("message", "Event Successfully Recovered!");
        } else {
            result.put("status", "failed");
            result.put("message", "Failed to Recover Event!");
        }

        return result;
    }
}

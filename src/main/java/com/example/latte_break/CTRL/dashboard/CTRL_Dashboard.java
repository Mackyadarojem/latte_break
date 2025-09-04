package com.example.latte_break.CTRL.dashboard;

import com.example.latte_break.BEAN.event_management.BEAN_EventManagement;
import com.example.latte_break.BEAN.inventory.BEAN_ItemList;
import com.example.latte_break.DAO.event_management.DAO_EventManagement;
import com.example.latte_break.DAO.inventory.DAO_ItemList;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("home")
public class CTRL_Dashboard {

    @Autowired
    DAO_EventManagement daoEventManagement;

    @Autowired
    DAO_ItemList daoItemList;

    //DASHBOARD
    @RequestMapping("")
    public ModelAndView home(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session.getAttribute("user_id") == null) {
            return new ModelAndView("redirect:/login");
        }
        return new ModelAndView("view/dashboard/index");
    }

    @RequestMapping("/ajax/getAllEvent")
    @ResponseBody
    public Map<String, Object> getAllEvent(BEAN_EventManagement beanEventManagement) {
        Map<String, Object> response = new HashMap<>();
        String date_from = "";
        if (beanEventManagement.getDate_from() == null) {
            date_from = "";
        } else {
            date_from = beanEventManagement.getDate_from();
        }
        List<BEAN_EventManagement> list = daoEventManagement.getAllEvent("", date_from, date_from);

        response.put("data", list);

        return response;
    }

    @RequestMapping("/ajax/getAllCriticalItem")
    @ResponseBody
    public Map<String, Object> getAllCriticalItem() {
        Map<String, Object> response = new HashMap<>();

        List<BEAN_ItemList> list = daoItemList.getAllCriticalItem();

        response.put("data", list);

        return response;
    }
}

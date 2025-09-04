package com.example.latte_break.CTRL.dashboard;

import com.example.latte_break.BEAN.dashboard.BEAN_Dashboard;
import com.example.latte_break.BEAN.event_management.BEAN_EventManagement;
import com.example.latte_break.DAO.audit_logs.DAO_AuditLogs;
import com.example.latte_break.DAO.dashboard.DAO_Dashboard;
import com.example.latte_break.DAO.event_management.DAO_EventManagement;
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
    DAO_AuditLogs daoAuditLogs;

    @Autowired
    DAO_Dashboard daoDashboard;
    //DASHBOARD
    @RequestMapping("")
    public ModelAndView home(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session.getAttribute("user_id") == null) {
            return new ModelAndView("redirect:/login");
        }

        int user_id = (int) session.getAttribute("user_id");
        daoAuditLogs.saveAuditLogs(user_id, "View Dashboard");

        List<BEAN_Dashboard> categoryList =  daoDashboard.getCategoryList();
        List<BEAN_Dashboard> topProducts =  daoDashboard.getTopProducts();
        ModelAndView mav = new ModelAndView("view/dashboard/index");
        mav.addObject("categoryList", categoryList);
        mav.addObject("topProducts", topProducts);
        return mav;
    }

    @RequestMapping("/ajax/getAllEvent")
    @ResponseBody
    public Map<String, Object> getAllEvent(BEAN_EventManagement beanEventManagement) {
        Map<String, Object> response = new HashMap<>();
        String date_from = "";
        if(beanEventManagement.getDate_from() == null){
            date_from = "";
        }else{
            date_from = beanEventManagement.getDate_from();
        }
        List<BEAN_EventManagement> list = daoEventManagement.getAllEvent("", date_from, date_from);

        response.put("data", list);

        return response;
    }

}

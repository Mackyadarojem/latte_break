package com.example.latte_break.CTRL.audit_logs;

import com.example.latte_break.BEAN.audit_logs.BEAN_AuditLogs;
import com.example.latte_break.DAO.audit_logs.DAO_AuditLogs;
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
@RequestMapping("/audit_logs")
public class CTRL_AuditLogs {
    @Autowired
    DAO_AuditLogs daoAuditLogs;

    @RequestMapping("")
    @ResponseBody
    public ModelAndView audit_logs(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("view/audit_logs/index");
        HttpSession session = request.getSession();

        int user_id_session = (int) session.getAttribute("user_id");
        daoAuditLogs.saveAuditLogs(user_id_session, "View Audit Logs");

        return mav;
    }

    @RequestMapping("/ajax/getAuditLogs")
    @ResponseBody
    public Map<String, Object> getAuditLogs(){
        Map<String, Object> response = new HashMap<>();

        List<BEAN_AuditLogs> list = daoAuditLogs.getAuditLogs();

        response.put("data", list);

        return response;
    }
}

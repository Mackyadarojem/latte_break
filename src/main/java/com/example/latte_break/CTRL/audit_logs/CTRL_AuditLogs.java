package com.example.latte_break.CTRL.audit_logs;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/audit_logs")
public class CTRL_AuditLogs {

    @RequestMapping("")
    @ResponseBody
    public ModelAndView audit_logs() {
        ModelAndView mav = new ModelAndView("view/audit_logs/index");

        return mav;
    }
}

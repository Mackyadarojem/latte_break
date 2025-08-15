package com.example.latte_break.CTRL;

import com.example.latte_break.BEAN.BEAN_Account;
import com.example.latte_break.DAO.DAO_Login;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CTRL_Login {
    @Autowired
    DAO_Login dao;

    @Autowired
    private EmailSender emailSender;


    //    LOGIN
    @RequestMapping("login")
    public ModelAndView login(HttpServletRequest request) {

        HttpSession session = request.getSession();
        if (session.getAttribute("user_id") != null) {
            return new ModelAndView("redirect:/home");
        } else {
            return new ModelAndView("view/login/login");
        }
    }

    @RequestMapping("/logout")
    public ModelAndView logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session.getAttribute("user_id") == null) {
            return new ModelAndView("redirect:/login");
        } else {
            int id = (int) session.getAttribute("user_id");
            dao.updateLastLogout(id);
            session.invalidate();
            return new ModelAndView("redirect:/login");
        }
    }

    @RequestMapping("/sendOTP")
    public @ResponseBody Map<String, Object> sendOTP(BEAN_Account bean, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Map<String, Object> response = new HashMap<>();
        int id = bean.getUser_id();
        String email = bean.getEmail();
        String otpResponse = emailSender.sendEmail(email, id);
        response.put("message", otpResponse);
        response.put("status", "success");
        return response;
    }

    @RequestMapping("/verifyOTP")
    public @ResponseBody Map<String, Object> verifyOTP(BEAN_Account bean, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Map<String, Object> response = new HashMap<>();
        int id = bean.getUser_id();
        int otp = bean.getOtp();
        int res = dao.verifyOTP(otp, id);
        System.out.println(res);
        if (res > 0) {
            response.put("status", "success");
        } else {
            response.put("status", "failed");
            response.put("message", "Invalid OTP Code! Please try again!");
        }
        return response;
    }

    @RequestMapping("checkUser")
    public @ResponseBody Map<String, Object> checkUser(BEAN_Account bean, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Map<String, Object> response = new HashMap<>();
        String username = bean.getUsername();
        String password = bean.getPassword();
        int result = dao.checkUser(username, password);

        if (result > 0) {
            BEAN_Account user = dao.getInfo(result);
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role_id", user.getRole_id());
            session.setAttribute("role_name", user.getRole_name());
            session.setAttribute("full_name", user.getFull_name());
            session.setAttribute("user_id", result);
            dao.updateLastLogin(result);
            response.put("role_id", user.getRole_id());
            response.put("status", "success");
        } else {
            response.put("status", "failed");
        }

        return response;
    }

    @RequestMapping("ajax/updateNewPassword")
    @ResponseBody
    public Map<String, Object> updateNewPassword(@RequestParam("newPassword") String newPassword, @RequestParam("username") String username) {
        Map<String, Object> response = new HashMap();
        int res = dao.updatePassword(username, newPassword);
        if (res > 0) {
            response.put("status", "success");
            response.put("message", "New Password Successfully Saved!");
        } else {
            response.put("status", "failed");
            response.put("message", "Fail to update your new password!");
        }
        return response;
    }

    @RequestMapping("ajax/checkEmail")
    @ResponseBody
    public Map<String, Object> checkEmail(@RequestParam("email") String email) {
        Map<String, Object> response = new HashMap();
        int res = dao.checkEmail(email);
        if (res > 0) {
            response.put("status", "success");
            BEAN_Account bean = dao.getInfo(res);
            response.put("username", bean.getUsername());
            response.put("id", bean.getUser_id());
        } else {
            response.put("status", "failed");
            response.put("message", "Email Address Not Found!");
        }
        return response;
    }

    //DASHBOARD
    @RequestMapping("home")
    public ModelAndView home(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session.getAttribute("user_id") == null) {
            return new ModelAndView("redirect:/login");
        }
        return new ModelAndView("view/dashboard/index");
    }


    //Kitchen Display
    @RequestMapping("kitchen_display")
    public ModelAndView kitchen_display() {
        return new ModelAndView("view/kitchen_display/index");
    }

    //LOYALTY CARD
    @RequestMapping("customerList")
    public ModelAndView customerList() {
        return new ModelAndView("view/loyalty_card/customerList");
    }

    //BILLIARD
    @RequestMapping("poolMonitor")
    public ModelAndView poolMonitor() {
        return new ModelAndView("view/billiard/pool_play_monitor");
    }

    @RequestMapping("scheduleList")
    public ModelAndView scheduleList() {
        return new ModelAndView("view/billiard/scheduleList");
    }


}


package com.example.latte_break.CTRL;

import com.example.latte_break.BEAN.BEAN_Account;
import com.example.latte_break.DAO.DAO_Account;
import com.fasterxml.jackson.databind.util.JSONPObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CTRL_Account {
    @Autowired
    DAO_Account daoAcc;

    //ACCOUNT
    @RequestMapping("accountList")
    public ModelAndView accountList(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session.getAttribute("user_id") == null) {
            return new ModelAndView("redirect:/login");
        }
        ModelAndView mav = new ModelAndView("view/account/accountList");
        return mav;
    }

    @RequestMapping("ajax/addAccount")
    public @ResponseBody Map<String, String> addAccount(BEAN_Account bean) {
        Map<String, String> response = new HashMap<>();
        String username = bean.getUsername();
        String password = bean.getPassword();
        String first_name = bean.getFirst_name();
        String middle_name = bean.getMiddle_name();
        String last_name = bean.getLast_name();
        String email = bean.getEmail();
        int role_id = bean.getRole_id();

        int checkUsername = daoAcc.checkUsername(username);
        int checkEmail = daoAcc.checkEmail(email);
        if (checkUsername > 0) {
            response.put("message", "Username is already taken, please use different username!");
            response.put("status", "failed");
            return response;
        }
        if (checkEmail > 0) {
            response.put("message", "Email Address is already taken, please use different Email Address!");
            response.put("status", "failed");
            return response;
        }

        int res = daoAcc.addAccount(first_name, middle_name, last_name, username,
                password, role_id, email);
        if (res > 0) {
            response.put("message", "Account Successfully Added!");
            response.put("status", "success");
        } else {
            response.put("message", "Error on adding account!");
            response.put("status", "failed");
        }


        return response;
    }


    @RequestMapping("ajax/getAllUser")
    @ResponseBody
    public Map<String, Object> getAllUser(HttpServletRequest request, BEAN_Account bean) {
        HttpSession session = request.getSession();
        Map<String, Object> response = new HashMap<>();
        if (session == null || session.getAttribute("user_id") == null) {
            response.put("status", "failed");
            response.put("redirect", "/login");
        } else {
            String username = bean.getUsername();
            String full_name = bean.getFull_name();
            int role_id = bean.getRole_id();
            List<BEAN_Account> users = daoAcc.getAllUser(username, full_name, role_id);
            response.put("data", users);
        }


        return response;
    }

    @RequestMapping("ajax/updateUserInfo")
    public @ResponseBody Map<String, String> updateUserInfo(BEAN_Account bean) {
        Map<String, String> response = new HashMap<>();
        int user_id = bean.getUser_id();
        int role_id = bean.getRole_id();
        String first_name = bean.getFirst_name();
        String middle_name = bean.getMiddle_name();
        String last_name = bean.getLast_name();
        String username = bean.getUsername();
        String email = bean.getEmail();

        int checkUsername = daoAcc.checkUsernameById(username, user_id);
        if (checkUsername > 0) {
            response.put("message", "Username is already taken, please use different username!");
            response.put("status", "failed");
            return response;
        }

        int res = daoAcc.updateUserInfo(user_id, username, first_name, middle_name, last_name, role_id, email);
        if (res > 0) {
            response.put("message", "Account Successfully Updated!");
            response.put("status", "success");
        } else {
            response.put("message", "Error on updating account!");
            response.put("status", "failed");
        }
        return response;

    }

    @RequestMapping("ajax/archiveUser")
    public @ResponseBody Map<String, String> archiveUser(BEAN_Account bean, HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        int id = bean.getUser_id();
        int res = daoAcc.archiveUser(id, username);
        if (res > 0) {
            response.put("message", "Account Successfully Archived!");
            response.put("status", "success");
        } else {
            response.put("message", "Error on archiving account!");
            response.put("status", "failed");
        }
        return response;
    }

    @RequestMapping("archivedAccountList")
    public ModelAndView archivedAccountList() {
        ModelAndView mav = new ModelAndView("view/account/archiveList");
        return mav;
    }

    @RequestMapping("ajax/getAllArchivedUser")
    @ResponseBody
    public Map<String, Object> getAllArchivedUser(HttpServletRequest request, BEAN_Account bean) {
        String username = bean.getUsername();
        String full_name = bean.getFull_name();
        int role_id = bean.getRole_id();

        List<BEAN_Account> users = daoAcc.getAllArchivedUser(username, full_name, role_id);
        Map<String, Object> response = new HashMap<>();
        response.put("data", users);
        response.put("recordsTotal", users.size());  // Total records before filtering
        response.put("recordsFiltered", users.size());  // Total records after filtering

        return response;
    }

    @RequestMapping("ajax/restoreAccount")
    @ResponseBody
    public Map<String, Object> restoreAccount(BEAN_Account bean) {
        Map<String, Object> response = new HashMap<>();
        int id = bean.getUser_id();
        int res = daoAcc.restoreAccount(id);
        if (res > 0) {
            response.put("message", "Account Successfully Restored!");
            response.put("status", "success");
        } else {
            response.put("message", "Error on Restoring Account!");
            response.put("status", "failed");
        }

        return response;
    }

    @RequestMapping("changePass")
    public ModelAndView changePass() {
        return new ModelAndView("view/account/changePass");
    }

    @RequestMapping("ajax/changePassword")
    @ResponseBody
    public Map<String, Object> ajaxChangePassword(@RequestParam("currentPassword") String currentPassword, @RequestParam("password") String newPassword, BEAN_Account bean) {
        Map<String, Object> response = new HashMap();
        int user_id = bean.getUser_id();
        int matchPass = daoAcc.matchPass(currentPassword, user_id);
        if (matchPass > 0) {
            int updatePass = daoAcc.updatePassword(user_id, newPassword);
            if (updatePass > 0) {
                response.put("message", "Password Successfully Changed!");
                response.put("status", "success");
            } else {
                response.put("message", "Failed to update!");
                response.put("status", "failed");
            }
        } else {
            response.put("message", "Current password does not match!");
            response.put("status", "failed");
        }
        return response;
    }


    @RequestMapping("ajax/resetUser")
    @ResponseBody
    public Map<String, Object> resetUser(@RequestParam("user_id") int user_id, @RequestParam("username") String username) {
        Map<String, Object> response = new HashMap<>();
        int res = daoAcc.resetUser(user_id, username);
        if (res > 0) {
            response.put("status", "success");
            response.put("message", "Account Successfully Reset!");
        } else {
            response.put("status", "failed");
            response.put("message", "Failed to Reset the Account!");
        }
        return response;
    }

    @RequestMapping("ajax/deleteAccount")
    @ResponseBody
    public Map<String, Object> deleteAccount(@RequestParam("id") int id) {
        Map<String, Object> response = new HashMap<>();
        int res = daoAcc.deleteAccount(id);
        if (res > 0) {
            response.put("status", "success");
            response.put("message", "Account Successfully Deleted!");
        } else {
            response.put("status", "failed");
            response.put("message", "Failed to Delete the Account!");
        }
        return response;
    }

}

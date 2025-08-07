package com.example.latte_break.CTRL.transaction;

import com.example.latte_break.BEAN.transaction.BEAN_Transaction;
import com.example.latte_break.DAO.transaction.DAO_Transaction;
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
public class CTRL_Transaction {

    @Autowired
    DAO_Transaction daoTransaction;

    //    TRANSACTION
    @RequestMapping("transactionList")
    public ModelAndView transactionList(HttpServletRequest request) {
        HttpSession session = request.getSession();
        ModelAndView mav = new ModelAndView("view/transaction/transactionList");
        if (session.getAttribute("user_id") == null) {
            return new ModelAndView("redirect:/login");
        }
        return mav;
    }

    @RequestMapping("transactionList/ajax/getTransactionList")
    @ResponseBody
    public Map<String, Object> getTransactionList(BEAN_Transaction bean) {
        Map<String, Object> response = new HashMap<>();

        List<BEAN_Transaction> list = daoTransaction.getTransactionList(bean.getTransaction(), bean.getDate_to(), bean.getDate_from());

        response.put("data", list);
        return response;
    }
}

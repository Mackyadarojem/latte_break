package com.example.latte_break.CTRL;

import com.example.latte_break.BEAN.inventory.BEAN_ItemList;
import com.example.latte_break.BEAN.inventory.BEAN_ProductList;
import com.example.latte_break.DAO.audit_logs.DAO_AuditLogs;
import com.example.latte_break.DAO.event_management.DAO_EventManagement;
import com.example.latte_break.DAO.inventory.DAO_ItemList;
import com.example.latte_break.DAO.inventory.DAO_ProductList;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("archive")
public class CTRL_Archive {
    @Autowired
    DAO_ProductList daoProduct;

    @Autowired
    DAO_ItemList daoItem;

    @Autowired
    DAO_EventManagement daoEventManagement;

    @Autowired
    DAO_AuditLogs daoAuditLogs;

    @RequestMapping("")
    public ModelAndView viewArchiveItemList(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session.getAttribute("user_id") == null) {
            return new ModelAndView("redirect:/login");
        }
        int user_id_session = (int) session.getAttribute("user_id");
        daoAuditLogs.saveAuditLogs(user_id_session, "View Archive List");

        ModelAndView mav = new ModelAndView("view/archive/index");
        List<BEAN_ProductList> category = daoProduct.getCategory();
        List<BEAN_ItemList> itemCategory = daoItem.getCategory();
        mav.addObject("category", category);
        mav.addObject("itemCategory", itemCategory);
        return mav;
    }

}

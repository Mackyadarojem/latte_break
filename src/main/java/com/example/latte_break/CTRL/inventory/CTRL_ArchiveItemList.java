package com.example.latte_break.CTRL.inventory;

import com.example.latte_break.BEAN.BEAN_Account;
import com.example.latte_break.BEAN.inventory.BEAN_ItemList;
import com.example.latte_break.DAO.inventory.DAO_ArchiveItemList;
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
public class CTRL_ArchiveItemList {
    @Autowired
    DAO_ArchiveItemList daoArchive;
    @Autowired
    DAO_ItemList daoItem;
    @RequestMapping("archiveItemList")
    public ModelAndView archiveList(HttpServletRequest request) {

        HttpSession session = request.getSession();
        if (session.getAttribute("user_id") == null) {
            return new ModelAndView("redirect:/login");
        }
        ModelAndView mav = new ModelAndView("view/inventory/archiveItemList");

        List<BEAN_ItemList> category = daoItem.getCategory();
        mav.addObject("category", category);

        return mav;

    }

    @RequestMapping("ajax/getAllArchiveItem")
    @ResponseBody
    public Map<String, Object> getAllArchiveItem(BEAN_ItemList bean){
        Map<String, Object> result = new HashMap<>();
        String name = bean.getName();
        int category_id = bean.getCategory_id();
        List<BEAN_ItemList> list = daoArchive.getAllArchivedItem(name, category_id);

        result.put("data", list);
        return result;
    }

    @RequestMapping("ajax/restoreItem")
    @ResponseBody
    public Map<String, Object> restoreItem(BEAN_ItemList bean, HttpServletRequest request){
        HttpSession session = request.getSession();
        Map<String, Object> result = new HashMap<>();

        String username = (String) session.getAttribute("username");
        int id = bean.getId();

        int res = daoArchive.restoreItem(username, id);

        if (res > 0) {
            result.put("status", "success");
            result.put("message", "Item Successfully Recovered!");
        } else {
            result.put("status", "failed");
            result.put("message", "Fail to Recover Item!");
        }

        return result;
    }

    @RequestMapping("ajax/deleteItem")
    @ResponseBody
    public Map<String, Object> deleteItem(BEAN_ItemList bean, HttpServletRequest request){
        HttpSession session = request.getSession();
        Map<String, Object> result = new HashMap<>();

        int id = bean.getId();

        int res = daoArchive.deleteItem(id);

        if (res > 0) {
            result.put("status", "success");
            result.put("message", "Item Successfully Deleted!");
        } else {
            result.put("status", "failed");
            result.put("message", "Fail to Recover Deleted!");
        }

        return result;
    }
}

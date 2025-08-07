package com.example.latte_break.CTRL.inventory;

import com.example.latte_break.BEAN.inventory.BEAN_ItemList;
import com.example.latte_break.DAO.inventory.DAO_ItemList;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
public class CTRL_ItemList {
    @Autowired
    DAO_ItemList daoItem;

    @RequestMapping("itemList")
    public ModelAndView itemList(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session.getAttribute("user_id") == null) {
            return new ModelAndView("redirect:/login");
        }
        ModelAndView mav = new ModelAndView("view/inventory/itemList");
        List<BEAN_ItemList> category = daoItem.getCategory();
        mav.addObject("category", category);
        return mav;
    }

    @RequestMapping("ajax/addItem")
    @ResponseBody
    public Map<String, Object> addItem(BEAN_ItemList bean, HttpServletRequest request) {

        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        Map<String, Object> result = new HashMap<>();

        String name = bean.getName();
        int category_id = bean.getCategory_id();
        String brand = bean.getBrand();
        String critical_quantity = bean.getCritical_quantity();
        String description = bean.getDescription();
        String unit = bean.getUnit();
        boolean expire = bean.isExpire();
        String method = bean.getMethod();
        if (Objects.equals(method, "Add")) {
            int res = daoItem.addItem(name, brand, category_id, critical_quantity, unit, expire, username, description);
            if (res > 0) {
                result.put("status", "success");
                result.put("message", "Item Successfully Added!");
            } else {
                result.put("status", "failed");
                result.put("message", "Fail to Add Item!");
            }
        } else {
            int id = bean.getId();
            int res = daoItem.updateItem(name, brand, category_id, critical_quantity, unit, expire, username, description, id);
            if (res > 0) {
                result.put("status", "success");
                result.put("message", "Item Successfully Updated!");
            } else {
                result.put("status", "failed");
                result.put("message", "Fail to Update Item!");
            }
        }

        return result;
    }

    @RequestMapping("ajax/getAllItem")
    @ResponseBody
    public Map<String, Object> getAllItem(BEAN_ItemList bean) {
        Map<String, Object> result = new HashMap<>();

        String name = bean.getName();
        int category_id = bean.getCategory_id();
        List<BEAN_ItemList> list = daoItem.getAllItem(name, category_id);

        result.put("data", list);
        return result;
    }

    @RequestMapping("ajax/archiveItem")
    @ResponseBody
    public Map<String, Object> archiveItem(BEAN_ItemList bean, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Map<String, Object> result = new HashMap<>();
        int id = bean.getId();
        String username = (String) session.getAttribute("username");
        int res = daoItem.archiveItem(id, username);

        if (res > 0) {
            result.put("status", "success");
            result.put("message", "Item Successfully Archived!");
        } else {
            result.put("status", "failed");
            result.put("message", "Fail to Archive Item!");
        }

        return result;
    }

    @RequestMapping("ajax/viewStockById")
    @ResponseBody
    public Map<String, Object> viewStockById(@RequestParam("id") int id, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        List<BEAN_ItemList> list = daoItem.getBatchItemListById(id);

        result.put("data", list);

        return result;
    }


//    Stock In

    @RequestMapping("ajax/stockIn")
    @ResponseBody
    public Map<String, Object> stockIn(@RequestBody List<BEAN_ItemList> items, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Map<String, Object> result = new HashMap<>();
        for (BEAN_ItemList item : items) {
            int id = item.getId();
            String quantity = item.getQuantity();
            boolean expire = item.isExpire();
            String expiration_date = item.getExpiration_date();
            String username = (String) session.getAttribute("username");
            int insert = daoItem.addBatch(id, quantity, expiration_date, username);
            int stockIn = daoItem.stockIn(insert, id, quantity, expiration_date, username);

            if (insert > 0) {
                int update = daoItem.updateStock(id, quantity);
                if (update > 0) {
                    result.put("status", "success");
                    result.put("message", "Item has been successfully stocked in!");
                } else {
                    result.put("status", "failed");
                    result.put("message", "Item has been fail stocked in!");
                }
            }
        }
        return result;
    }

    @RequestMapping("ajax/getStockInHistory")
    @ResponseBody
    public Map<String, Object> getStockInHistory() {
        Map<String, Object> result = new HashMap<>();

        List<BEAN_ItemList> list = daoItem.getStockInHistory();

        result.put("data", list);

        return result;
    }

    //    Stock Out
    @RequestMapping("ajax/getBatchItemList")
    @ResponseBody
    public Map<String, Object> getBatchItemList() {
        Map<String, Object> result = new HashMap<>();
        List<BEAN_ItemList> list = daoItem.getBatchItemList();

        result.put("data", list);
        return result;
    }

    @RequestMapping("ajax/stockOut")
    @ResponseBody
    public Map<String, Object> stockOut(@RequestBody List<BEAN_ItemList> items, HttpServletRequest request) {
        HttpSession session = request.getSession();

        Map<String, Object> result = new HashMap<>();
        for (BEAN_ItemList item : items) {
            String username = (String) session.getAttribute("username");
            String quantity = item.getQuantity();
            int batch_id = item.getBatch_id();
            int item_id = item.getId();
            String expiration_date = item.getExpiration_date();
            int deduct = daoItem.deductBatch(batch_id, quantity, username);
            int stockOut = daoItem.stockOut(batch_id, item_id, quantity, expiration_date, username);
            int updateStock = daoItem.deductStock(item_id, quantity);
            if (updateStock > 0) {
                result.put("status", "success");
                result.put("message", "Item has been successfully stocked out!");
            } else {
                result.put("status", "failed");
                result.put("message", "Item fail to stocked out!");
            }
        }
        return result;
    }

    @RequestMapping("ajax/getStockOutHistory")
    @ResponseBody
    public Map<String, Object> getStockOutHistory() {
        Map<String, Object> result = new HashMap<>();
        List<BEAN_ItemList> list = daoItem.getStockOutHistory();

        result.put("data", list);

        return result;
    }
}

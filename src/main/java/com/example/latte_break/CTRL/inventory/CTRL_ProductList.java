package com.example.latte_break.CTRL.inventory;

import com.example.latte_break.BEAN.inventory.BEAN_ItemList;
import com.example.latte_break.BEAN.inventory.BEAN_ProductList;
import com.example.latte_break.DAO.inventory.DAO_ProductList;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
public class CTRL_ProductList {
    @Autowired
    DAO_ProductList daoInventory;

    @RequestMapping("/productList")
    public ModelAndView productList(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session.getAttribute("user_id") == null) {
            return new ModelAndView("redirect:/login");
        }
        ModelAndView mav = new ModelAndView("view/inventory/productList");
        List<BEAN_ProductList> category = daoInventory.getCategory();
        mav.addObject("category", category);
        return mav;
    }

    @RequestMapping("ajax/addProduct")
    @ResponseBody
    public Map<String, Object> addProduct(BEAN_ProductList bean) {
        Map<String, Object> result = new HashMap<>();
        String name = bean.getName();
        int category_id = bean.getCategory_id();
        String description = bean.getDescription();
        String price = bean.getPrice();
        String priceMedium = bean.getPriceMedium();
        String priceLarge = bean.getPriceLarge();
        boolean available = bean.isAvailable();
        String method = bean.getMethod();
        System.out.println("price >>" + price);
        if (Objects.equals(method, "Add")) {
            int res = daoInventory.addProduct(name, category_id, description, price, priceMedium, priceLarge, available);
            if (res > 0) {
                result.put("status", "success");
                result.put("message", "Product Successfully Added!");
            } else {
                result.put("status", "failed");
                result.put("message", "Fail to Add Product!");
            }
        } else {
            int id = bean.getId();
            int res = daoInventory.updateProduct(name, category_id, description, price, available, id);
            if (res > 0) {
                result.put("status", "success");
                result.put("message", "Product Successfully Updated!");
            } else {
                result.put("status", "failed");
                result.put("message", "Fail to Update Product!");
            }
        }

        return result;
    }

    @RequestMapping("ajax/getAllProduct")
    @ResponseBody
    public Map<String, Object> getAllProduct(BEAN_ProductList bean) {

        Map<String, Object> result = new HashMap<>();
        String name = bean.getName();
        int category_id = bean.getCategory_id();
        List<BEAN_ProductList> list = daoInventory.getAllProduct(name, category_id);
        result.put("data", list);
        return result;
    }

    @RequestMapping("ajax/getArchivedProduct")
    @ResponseBody
    public Map<String, Object> getArchivedProduct(BEAN_ProductList bean) {
        Map<String, Object> result = new HashMap<>();
        String name = bean.getName();
        int category_id = bean.getCategory_id();
        List<BEAN_ProductList> list = daoInventory.getArchiveProduct(name, category_id);
        result.put("data", list);
        System.out.println(list);
        return result;
    }

    @RequestMapping("ajax/deleteProduct")
    @ResponseBody
    public Map<String, Object> deleteProduct(BEAN_ProductList bean) {
        Map<String, Object> result = new HashMap<>();
        int id = bean.getId();

        int res = daoInventory.deleteProduct(id);
        if (res > 0) {
            result.put("status", "success");
            result.put("message", "Product Successfully Deleted!");
        } else {
            result.put("status", "failed");
            result.put("message", "Fail to Delete Product!");
        }
        return result;
    }

    @RequestMapping("ajax/archiveProduct")
    @ResponseBody
    public Map<String, Object> archiveProduct(BEAN_ProductList bean, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        int id = bean.getId();
        int res = daoInventory.archiveProduct(id, username);
        if (res > 0) {
            result.put("status", "success");
            result.put("message", "Product Successfully Archived!");
        } else {
            result.put("status", "failed");
            result.put("message", "Fail to Archived Product!");
        }
        return result;
    }

    @RequestMapping("ajax/restoreProduct")
    @ResponseBody
    public Map<String, Object> restoreItem(BEAN_ItemList bean, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Map<String, Object> result = new HashMap<>();

        String username = (String) session.getAttribute("username");
        int id = bean.getId();

        int res = daoInventory.restoreProduct(username, id);

        if (res > 0) {
            result.put("status", "success");
            result.put("message", "Product Successfully Recovered!");
        } else {
            result.put("status", "failed");
            result.put("message", "Fail to Recover Product!");
        }

        return result;
    }

    @RequestMapping("ajax/changeAvailability")
    @ResponseBody
    public Map<String, Object> changeAvailability(@RequestParam("id") int id, @RequestParam("available") boolean available) {
        Map<String, Object> result = new HashMap<>();

        int res = daoInventory.changeAvailability(available, id);

        if (res > 0) {
            result.put("status", "success");
            result.put("message", "Product Successfully Updated!");
        } else {
            result.put("status", "failed");
            result.put("message", "Fail to Update Product!");
        }

        return result;
    }
}

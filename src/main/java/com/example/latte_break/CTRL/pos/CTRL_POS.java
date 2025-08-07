package com.example.latte_break.CTRL.pos;

import com.example.latte_break.BEAN.inventory.BEAN_ProductList;
import com.example.latte_break.BEAN.pos.BEAN_AddOns;
import com.example.latte_break.BEAN.pos.BEAN_InvoiceRequest;
import com.example.latte_break.BEAN.pos.BEAN_POS;
import com.example.latte_break.DAO.billiard.DAO_Billiard;
import com.example.latte_break.DAO.pos.DAO_POS;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CTRL_POS {
    @Autowired
    DAO_POS daoPos;

    @Autowired
    DAO_Billiard daoBilliard;

    //POS
    @RequestMapping("pos")
    public ModelAndView pos(HttpServletRequest request) {
        HttpSession session = request.getSession();
        ModelAndView mav = new ModelAndView("view/pos/pos");
        if (session.getAttribute("user_id") == null) {
            return new ModelAndView("redirect:/login");
        }
        List<BEAN_ProductList> list = daoPos.getAllProduct();
        List<BEAN_ProductList> category = daoPos.getAllCategory();

        mav.addObject("data", list);
        mav.addObject("category", category);
        return mav;
    }

    @ResponseBody
    @RequestMapping("pos/verifyAdminPassword")
    public Map<String, Object> verifyAdminPassword(@RequestParam("password") String password) {
        Map<String, Object> result = new HashMap<>();
        int res = daoPos.verifyAdminPassword(password);

        if (res > 0) {
            result.put("status", "success");
        } else {
            result.put("status", "failed");
        }

        return result;
    }

    @ResponseBody
    @RequestMapping("pos/saveInvoice")
    public Map<String, Object> saveInvoice(@RequestBody BEAN_InvoiceRequest beanList, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        Map<String, Object> result = new HashMap<>();
        String products = "";
        boolean first = true;
        for (BEAN_POS bean : beanList.getProductList()) {
            String product = bean.getProductName();
            if (!first) {
                products += ",\n";
            }


            if (!product.equals("billiard")) {
                String quantity = bean.getQuantity();
                products += product + " (x" + quantity + ")";
                String addOns = "";
                for (BEAN_AddOns addOn : bean.getAddOns()) {
                    List<String> names = addOn.getAddOnsName();
                    List<String> prices = addOn.getAddOnsPrice();
                    for (int i = 0; i < names.size(); i++) {
                        String name = names.get(i);
                        addOns = name;
                    }
                }
                if (!addOns.isEmpty()) {
                    products += "\n " + addOns;
                }
            } else {
                String timeValue = bean.getTimeValue();
                products += product + " (" + timeValue + " hr/s)";
                int paidSched = daoBilliard.paidSched(bean.getBilliard_id());
            }

            first = false;
        }
        String total_amount = beanList.getTotal_amount();
        String change = beanList.getChange();
        String discount = beanList.getDiscount();
        String cash_tendered = beanList.getCash_tendered();
        String mode_of_payment = beanList.getMode_of_payment();
        String reference_no = beanList.getReferenceNo();
        String sender_name = beanList.getSenderName();
        String service_type = beanList.getServiceType();
        String subTotal = beanList.getSubTotal();

        String invoiceNumber = daoPos.generateInvoiceNumber();
        int res = daoPos.addInvoiceRecord(invoiceNumber, products, total_amount,
                cash_tendered, mode_of_payment, change, service_type,
                username, discount, subTotal, reference_no, sender_name);
        int addTransaction = daoPos.addTransaction(products, username, "Completed Transaction");
        if (res > 0) {
            result.put("status", "success");
            result.put("message", "Invoice Record Successfully Saved!");
            result.put("invoiceNumber", invoiceNumber);
        } else {
            result.put("status", "failed");
            result.put("message", "Invoice Record Failed to Save");
        }

        return result;
    }

    @ResponseBody
    @RequestMapping("pos/voidTransaction")
    public Map<String, Object> voidTransaction(@RequestBody BEAN_InvoiceRequest beanList, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        Map<String, Object> result = new HashMap<>();
        String products = "";
        boolean first = true;
        for (BEAN_POS bean : beanList.getProductList()) {
            String product = bean.getProductName();
            if (!first) {
                products += ",\n";
            }
            if (!product.equals("billiard")) {
                String quantity = bean.getQuantity();
                products += product + " (x" + quantity + ")";
                String addOns = "";
                for (BEAN_AddOns addOn : bean.getAddOns()) {
                    List<String> names = addOn.getAddOnsName();
                    List<String> prices = addOn.getAddOnsPrice();
                    for (int i = 0; i < names.size(); i++) {
                        String name = names.get(i);
                        addOns = name;
                    }
                }
                if (!addOns.isEmpty()) {
                    products += "\n " + addOns;
                }
            } else {
                String timeValue = bean.getTimeValue();
                products += product + " (" + timeValue + " hr/s)";
            }
            first = false;
        }

        int res = daoPos.addTransaction(products, username, beanList.getTransaction());

        if (res > 0) {
            result.put("status", "success");
            result.put("message", "Transaction Successfully Void");
        } else {
            result.put("status", "failed");
            result.put("message", "Failed to Void Transaction");
        }
        return result;
    }

}

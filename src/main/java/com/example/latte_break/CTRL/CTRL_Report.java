package com.example.latte_break.CTRL;

import com.example.latte_break.BEAN.inventory.BEAN_ItemList;
import com.example.latte_break.BEAN.inventory.BEAN_ProductList;
import com.example.latte_break.BEAN.reports.BEAN_Report;
import com.example.latte_break.DAO.inventory.DAO_ItemList;
import com.example.latte_break.DAO.inventory.DAO_ProductList;
import com.example.latte_break.DAO.reports.DAO_Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("reports")
public class CTRL_Report {
    @Autowired
    DAO_ProductList daoProduct;

    @Autowired
    DAO_ItemList daoItem;

    @Autowired
    DAO_Report daoReport;

    //    REPORTS
    @RequestMapping("")
    public ModelAndView salesReport() {
        ModelAndView mav = new ModelAndView("view/reports/index");
        List<BEAN_ProductList> category = daoProduct.getCategory();
        List<BEAN_ItemList> itemCategory = daoItem.getCategory();
        mav.addObject("category", category);
        mav.addObject("itemCategory", itemCategory);
        return mav;
    }

    @RequestMapping("/ajax/getItemList")
    @ResponseBody
    public Map<String, Object> getItemList(BEAN_Report bean) {
        Map<String, Object> result = new HashMap<>();
        String date_from = bean.getDate_from();
        String date_to = bean.getDate_to();
        int category_id = bean.getCategory_id();

        List<BEAN_Report> list = daoReport.getItemList(category_id, date_from, date_to);

        result.put("data", list);

        return result;
    }
}

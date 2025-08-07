package com.example.latte_break.CTRL;

import com.example.latte_break.BEAN.inventory.BEAN_ItemList;
import com.example.latte_break.BEAN.inventory.BEAN_ProductList;
import com.example.latte_break.DAO.inventory.DAO_ItemList;
import com.example.latte_break.DAO.inventory.DAO_ProductList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class CTRL_Report {
    @Autowired
    DAO_ProductList daoProduct;

    @Autowired
    DAO_ItemList daoItem;
    //    REPORTS
    @RequestMapping("reports")
    public ModelAndView salesReport() {
        ModelAndView mav = new ModelAndView("view/reports/index");
        List< BEAN_ProductList> category = daoProduct.getCategory();
        List<BEAN_ItemList> itemCategory = daoItem.getCategory();
        mav.addObject("category", category);
        mav.addObject("itemCategory", itemCategory);
        return mav;
    }

}

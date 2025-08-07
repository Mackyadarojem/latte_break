package com.example.latte_break.CTRL.billard;

import com.example.latte_break.BEAN.biliard.BEAN_Billiard;
import com.example.latte_break.DAO.billiard.DAO_Billiard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
public class CTRL_Billiard {
    @Autowired
    DAO_Billiard daoBilliard;

    @RequestMapping("ajax/addBilliardSched")
    @ResponseBody
    public Map<String, Object> addBilliardSched(BEAN_Billiard bean) {
        Map<String, Object> response = new HashMap<>();
        String customerName = bean.getCustomerName();
        boolean openHour = bean.isOpen_hour();
        int hour = 0;
        int minutes = 0;
        if (!openHour) {
            hour = bean.getHour();
            minutes = bean.getMinutes();
        }
        int seconds = 00;
        String duration = String.format("%02d : %02d : %02d", hour, minutes, 00);


        int res = daoBilliard.addBilliardSched(customerName, hour, minutes, openHour, duration);

        if (res > 0) {
            response.put("message", "Billiard Schedule Successfully Added!");
            response.put("status", "success");
        } else {
            response.put("message", "Error on Adding Billiard Schedule!");
            response.put("status", "failed");
        }
        return response;
    }

    @RequestMapping("ajax/getBilliardSched")
    @ResponseBody
    public Map<String, Object> getBilliardSched() {
        Map<String, Object> response = new HashMap<>();

        List<BEAN_Billiard> list = daoBilliard.getBilliardSched();

        response.put("data", list);

        return response;
    }

    @RequestMapping("billiard/ajax/startTime")
    @ResponseBody
    public Map<String, Object> startTime(BEAN_Billiard bean) {
        Map<String, Object> response = new HashMap<>();

        int id = bean.getId();
        String duration = bean.getDuration();
        boolean open_hour = bean.isOpen_hour();
        int res = -1;
        if (open_hour) {
            res = daoBilliard.startTime(id);
        } else {
            res = daoBilliard.startTime(id, duration);
        }

        if (res > 0) {
            response.put("status", "success");
        } else {
            response.put("status", "failed");
        }
        return response;
    }

    @RequestMapping("billiard/ajax/endTime")
    @ResponseBody
    public Map<String, Object> endTime(BEAN_Billiard bean) {
        Map<String, Object> response = new HashMap<>();
        int id = bean.getId();

        int res = daoBilliard.stopTime(id);

        if (res > 0) {
            response.put("status", "success");
        } else {
            response.put("status", "failed");
        }

        return response;
    }

    @RequestMapping("billiard/ajax/cancelSched")
    @ResponseBody
    public Map<String, Object> cancelSched(BEAN_Billiard bean) {
        Map<String, Object> response = new HashMap<>();
        int id = bean.getId();

        int res = daoBilliard.cancelSched(id);

        if (res > 0) {
            response.put("status", "success");
        } else {
            response.put("status", "failed");
        }

        return response;
    }
}

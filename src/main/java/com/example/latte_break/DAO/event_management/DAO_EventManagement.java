package com.example.latte_break.DAO.event_management;

import com.example.latte_break.BEAN.event_management.BEAN_EventManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class DAO_EventManagement {
    JdbcTemplate template;

    public void DAO_EventManagement() {
    }

    @Autowired
    public void DAO_EventManagement(JdbcTemplate template) {
        this.template = template;
    }

    public List<BEAN_EventManagement> getAllUser() {
        String sql = "SELECT u.id, UPPER(CONCAT(first_name, ' ',  middle_name, ' ', last_name)) as name, r.role_name  \n" +
                "FROM `tbl_user` u \n" +
                "INNER JOIN ref_role r ON u.role_id = r.id ";

        return template.query(sql, new RowMapper<BEAN_EventManagement>() {
            @Override
            public BEAN_EventManagement mapRow(ResultSet rs, int rowNum) throws SQLException {
                BEAN_EventManagement bean = new BEAN_EventManagement();
                bean.setId(rs.getInt("id"));
                bean.setName(rs.getString("name"));
                bean.setRole(rs.getString("role_name"));
                return bean;
            }
        });
    }

    public int addEvent(String event_name, String purpose, String date, String time, String participants_name, String participants_id, int user_id) {
        String sql = "INSERT into tbl_event (`event_name`, `purpose`, `date`, `time`, `participants_name`, `participants_id`, `created_by`) \n" +
                "VALUES (? , ? , ? , ? , ? , ? , ?)";
        return template.update(sql, new Object[]{event_name, purpose, date, time, participants_name, participants_id, user_id});
    }

    public List<BEAN_EventManagement> getAllEvent(String event_name, String date_from, String date_to) {
        System.out.println(event_name);
        System.out.println(date_from);
        System.out.println(date_to);
        String sql = "SELECT \n" +
                "    e.id, \n" +
                "    event_name, \n" +
                "    purpose, \n" +
                "    date, \n" +
                "    time, \n" +
                "    participants_name, \n" +
                "    participants_id,  \n" +
                "    UPPER(CONCAT(u.first_name, ' ', u.middle_name, ' ', u.last_name)) AS name\n" +
                "FROM tbl_event e \n" +
                "INNER JOIN tbl_user u ON e.created_by = u.id \n" +
                "WHERE archive_at IS NULL \n" +
                "  AND ( ? = '' OR e.event_name LIKE CONCAT('%', ?, '%') ) \n" +
                "  AND ( ( ? = '' AND ? = '' ) OR ( e.date >= ? AND e.date <=  ? ) );\n";
        return template.query(sql, new Object[]{event_name, event_name, date_from, date_to, date_from, date_to}, new RowMapper<BEAN_EventManagement>() {
            @Override
            public BEAN_EventManagement mapRow(ResultSet rs, int rowNum) throws SQLException {
                BEAN_EventManagement bean = new BEAN_EventManagement();
                bean.setEvent_name(rs.getString("event_name"));
                bean.setPurpose(rs.getString("purpose"));
                bean.setDate(rs.getString("date"));
                bean.setTime(rs.getString("time"));
                bean.setParticipants_name(rs.getString("participants_name"));
                bean.setParticipants_ids(rs.getString("participants_id"));
                bean.setName(rs.getString("name"));
                bean.setId(rs.getInt("id"));
                return bean;
            }
        });
    }

    public int editEvent(String event_name, String purpose, String date, String time, String participants_name, String participants_id, int user_id, int id) {
        String sql = "UPDATE tbl_event SET event_name = ?, purpose = ?, date = ?, time = ?, participants_name = ?, participants_id = ?, updated_by = ? \n" +
                "WHERE id = ?";
        return template.update(sql, event_name, purpose, date, time, participants_name, participants_id, user_id, id);
    }

    public int archiveEvent(int id, int user_id) {
        String sql = "UPDATE tbl_event SET archive_at = NOW(), archive_by = ? \n" +
                "WHERE id = ?";
        return template.update(sql, user_id, id);
    }
}

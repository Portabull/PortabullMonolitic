package com.portabull.generic.daoimpl;

import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.dbutils.HibernateUtils;
import com.portabull.generic.dao.GenericDao;
import com.portabull.generic.models.CustomerEnquiry;
import com.portabull.generic.models.SchedulerActions;
import com.portabull.generic.models.SchedulerTask;
import com.portabull.response.PortableResponse;
import com.portabull.utils.commonutils.CommonUtils;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class GenericDaoImpl implements GenericDao {

    @Autowired
    HibernateUtils hibernateUtils;

    @Override
    public PortableResponse saveClientContactDetails(Map<String, String> payload) {
        CustomerEnquiry customerEnquiry = new CustomerEnquiry();
        customerEnquiry.setCustomerName(payload.get("name"));
        customerEnquiry.setCustomerEmail(payload.get("email"));
        customerEnquiry.setSubject(payload.get("subject"));
        customerEnquiry.setMessage(payload.get("message"));
        customerEnquiry.setMobileNumber(payload.get("contactNumber"));
        customerEnquiry.setEnquiryData(new Date().getTime());
        hibernateUtils.saveOrUpdateEntity(customerEnquiry);
        return new PortableResponse("Your message was notified to admin user, will contact you shortly",
                StatusCodes.C_200, PortableConstants.SUCCESS, null);
    }

    @Override
    public PortableResponse getClientContactDetails() {
        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS,
                hibernateUtils.loadFullData(CustomerEnquiry.class));
    }

    @Override
    public PortableResponse saveSchedularDetails(Map<String, Object> payload) {

        SchedulerTask task = null;

        if (payload.get("schedular_id") != null)
            task = hibernateUtils.findEntity(SchedulerTask.class, Long.valueOf(payload.get("schedular_id").toString()));

        if (task == null)
            task = new SchedulerTask();

        if (payload.get("isActive") != null)
            task.setActive(Boolean.valueOf(payload.get("isActive").toString()));
        else
            task.setActive(true);

        task.setDays(payload.get("days") != null ? payload.get("days").toString() : null);

        task.setUserId(CommonUtils.getLoggedInUserId());

        task.setSchedulerName(payload.get("schedulerName").toString());

        task.setSpecificDailyTime(payload.get("specificDailyTime") != null ? payload.get("specificDailyTime").toString() : null);

        task.setTriggerType(payload.get("triggerType").toString());

        task.setTimeGap(payload.get("timeGap") != null ? Integer.valueOf(payload.get("timeGap").toString()) : null);

        hibernateUtils.saveOrUpdateEntity(task);

        List<Map<String, Object>> schedulerActions = (List<Map<String, Object>>) payload.get("schedulerActions");

        for (Map<String, Object> action : schedulerActions) {
            SchedulerActions actions = null;
            if (action.get("action_id") != null)
                actions = hibernateUtils.findEntity(SchedulerActions.class, Long.valueOf(action.get("action_id").toString()));

            if (actions == null)
                actions = new SchedulerActions();
            actions.setSchedulerId(task.getSchedulerId());
            actions.setAction(action.get("action").toString());
            actions.setAction_type(action.get("actionType").toString());
            hibernateUtils.saveOrUpdateEntity(actions);
        }

        return new PortableResponse();
    }

    @Override
    public PortableResponse getSchedulers() {
        try (Session session = hibernateUtils.getSession()) {
            List<SchedulerTask> tasks = session.createQuery("FROM SchedulerTask WHERE userId=:userId ORDER BY schedulerId", SchedulerTask.class)
                    .setParameter("userId", CommonUtils.getLoggedInUserId()).list();
            List<Map<String, Object>> response = new ArrayList<>();
            tasks.forEach(task -> {
                Map<String, Object> taskResponse = new HashMap<>();
                taskResponse.put("schedulerId", task.getSchedulerId());
                taskResponse.put("schedulerName", task.getSchedulerName());
                taskResponse.put("lastTriggeredDate", task.getLastTriggeredDate());
                taskResponse.put("isActive", task.isActive());
                taskResponse.put("triggerType", task.getTriggerType());
                response.add(taskResponse);
            });
            return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, response);
        }
    }

    @Override
    public PortableResponse changeSchedulerStatus(Long schedulerId, Boolean status) {
        SchedulerTask task = hibernateUtils.findEntity(SchedulerTask.class, schedulerId);
        if (task != null) {
            task.setActive(status);
            hibernateUtils.saveOrUpdateEntity(task);
        }
        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, null);
    }

    @Override
    public Map<String, Object> getSchedularDetails(String schedulerId) {
        List<Map<String, Object>> schedulerActions = new ArrayList<>();

        SchedulerTask task = hibernateUtils.findEntity(SchedulerTask.class, Long.valueOf(schedulerId));
        Map<String, Object> response = new HashMap<>();
        response.put("days", task.getDays());
        response.put("schedulerName", task.getSchedulerName());
        response.put("schedular_id", task.getSchedulerId());
        response.put("specificDailyTime", task.getSpecificDailyTime());
        response.put("triggerType", task.getTriggerType());

        response.put("timeGap", task.getTimeGap());

        List<SchedulerActions> actions = hibernateUtils.findEntitiesByCriteria(
                SchedulerActions.class, "schedulerId", Long.valueOf(schedulerId));

        actions.forEach(action -> {
            Map<String, Object> mapAction = new HashMap<>();
            mapAction.put("action_id", action.getActionId());
            mapAction.put("action", action.getAction());
            mapAction.put("actionType", action.getAction_type());
            schedulerActions.add(mapAction);
        });

        response.put("isActive", task.isActive());

        response.put("schedulerActions", schedulerActions);
        return response;
    }

}

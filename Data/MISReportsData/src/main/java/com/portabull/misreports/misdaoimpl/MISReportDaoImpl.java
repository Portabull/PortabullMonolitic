package com.portabull.misreports.misdaoimpl;

import com.portabull.constants.FileConstants;
import com.portabull.constants.PortableConstants;
import com.portabull.constants.StatusCodes;
import com.portabull.dbutils.HibernateUtils;
import com.portabull.execption.BadRequestException;
import com.portabull.misreports.MISMailAudit;
import com.portabull.misreports.MISReport;
import com.portabull.misreports.SelectClause;
import com.portabull.misreports.misdao.MISReportDao;
import com.portabull.payloads.MISPayload;
import com.portabull.payloads.MISReportPayload;
import com.portabull.payloads.PageLimit;
import com.portabull.response.PortableResponse;
import com.portabull.utils.queryutils.QueryGenerator;
import com.portabull.utils.queryutils.QueryResultTransformer;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Repository
public class MISReportDaoImpl implements MISReportDao {

    @Autowired
    HibernateUtils hibernateUtils;

    @Autowired
    QueryGenerator queryGenerator;


    @Override
    public MISReport getMISReport(Long reportID) {
        return hibernateUtils.findEntity(MISReport.class, reportID);
    }

    @Override
    public <T> T getReport(MISPayload payload, String format, PageLimit pageLimit) {
        try (Session session = hibernateUtils.getSession()) {
            Query<T> query = queryGenerator.generateDynamicQuery(session, payload, pageLimit);
            Map<String, String> aliasNames = QueryResultTransformer.prepareAliasNames(payload);

            if (FileConstants.TEXT.equalsIgnoreCase(format)) {
                return (T) QueryResultTransformer.prepareCustomResult(query, aliasNames);
            }

            return (T) QueryResultTransformer.prepareResultTransformer(query, aliasNames);
        }
    }

    @Override
    public MISMailAudit saveMisMailAudit(MISMailAudit misMailAudit) {
        return hibernateUtils.saveOrUpdateEntity(misMailAudit);
    }

    @Override
    public MISReport saveMisReport(MISReport misReport) {
        return hibernateUtils.saveOrUpdateEntity(misReport);
    }

    @Override
    public List<MISReport> getMISReportsbyUserID(Long userID) {
        try (Session session = hibernateUtils.getSession()) {
            return session.createQuery(" FROM MISReport where userID =:userID order by misID")
                    .setParameter("userID", userID)
                    .list();
        }
    }

    @Override
    public PortableResponse createMISReport(MISReportPayload payload, String tempLogoImagePath, Long logoDMSId) {
        MISReport misReport = null;

        if (payload.getMisReportId() != null) {
            misReport = hibernateUtils.findEntity(MISReport.class, payload.getMisReportId());
        }

        if (misReport == null) {
            misReport = new MISReport();
        }

        misReport.setMisName(payload.getMisName());

        misReport.setDocumentTittle(payload.getDocumentTittle());

        misReport.setUserID(payload.getUserId());

        misReport.setHeaderColorCode(payload.getHeaderColourCode());

        misReport.setGroupBy(payload.getGroupBy());

        misReport.setOrderBy(payload.getOrderBy());

        misReport.setTimeStampFormat(payload.getTimeStampFormat());

        misReport.setLogoRequired(payload.isLogoRequired());

        misReport.setTimeStampRequired(payload.isTimeStamoRequired());

        misReport.setLogoDMSId(logoDMSId);

        misReport.setTempLogoImagePath(tempLogoImagePath);

        misReport.setViewName(payload.getViewName());

        hibernateUtils.saveOrUpdateEntity(misReport);

        saveSelectWhereClauses(payload, misReport);

        return new PortableResponse("", StatusCodes.C_200, PortableConstants.SUCCESS, null);
    }

    @Override
    public void validateQuery(MISPayload payload) {
        try (Session session = hibernateUtils.getSession()) {

            List<Long> userIds = (List<Long>) session.createQuery("SELECT userID FROM UserCredentials WHERE loginUserName=:loginUserName").
                    setParameter("loginUserName", payload.getUserName()).list();

            if (CollectionUtils.isEmpty(userIds))
                throw new BadRequestException("User Not Found");

            payload.setUserID(userIds.get(0));

            queryGenerator.generateDynamicQuery(session, payload, null).list();

        }
    }

    private void saveSelectWhereClauses(MISReportPayload payload, MISReport misReport) {
        payload.getSelectClause().forEach((columnName, aliasName) -> {
            SelectClause selectClause = new SelectClause();
            selectClause.setMisID(misReport.getMisID());
            selectClause.setSelectName(columnName);
            selectClause.setAliasName(aliasName);
            hibernateUtils.saveOrUpdateEntity(selectClause);
        });
    }

}

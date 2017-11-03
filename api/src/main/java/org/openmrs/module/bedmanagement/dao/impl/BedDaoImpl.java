package org.openmrs.module.bedmanagement.dao.impl;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.module.bedmanagement.constants.BedStatus;
import org.openmrs.module.bedmanagement.dao.BedDao;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedPatientAssignment;
import org.openmrs.module.bedmanagement.entity.BedTag;
import org.openmrs.module.bedmanagement.entity.BedType;
import org.openmrs.module.bedmanagement.BedDetails;

import java.util.*;

public class BedDaoImpl implements BedDao {

    SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Bed getBedByUuid(String uuid) {
        return (Bed) sessionFactory.getCurrentSession()
                .createQuery("from Bed b where b.uuid = :uuid and b.voided=false ")
                .setString("uuid", uuid)
                .uniqueResult();
    }

    @Override
    public Bed getBedById(Integer id) {
        return (Bed) sessionFactory.getCurrentSession()
                .createQuery("from Bed b where b.id = :id")
                .setInteger("id", id)
                .uniqueResult();
    }

    @Override
    public List<Bed> getBeds(Integer limit, Integer offset) {
        String hql = "select b " +
                "from Bed b " +
                "where b.voided=:voided";

        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("voided", false);
        if (limit != null) {
            query.setMaxResults(limit);
            if (offset != null)
                query.setFirstResult(offset);
        }

        return query.list();
    }

    @Override
    public List<Bed> getBedsByLocation(Location location, Integer limit, Integer offset) {
        String hql = "select blm.bed " +
                "from BedLocationMapping blm " +
                "join blm.bed b " +
                "join blm.location l " +
                "where b.voided=:voided and l=:location";

        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("voided", false);
        query.setParameter("location", location);
        if (limit != null) {
            query.setMaxResults(limit);
            if (offset != null)
                query.setFirstResult(offset);
        }

        return query.list();
    }

    @Override
    public List<Bed> getBedsByBedType(BedType bedType, Integer limit, Integer offset) {
        String hql = "select b " +
                "from Bed b " +
                "where b.voided=:voided and b.bedType=:bedType";

        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("voided", false);
        query.setParameter("bedType", bedType);

        if (limit != null) {
            query.setMaxResults(limit);
            if (offset != null)
                query.setFirstResult(offset);
        }

        return query.list();
    }

    @Override
    public List<Bed> getBedsByLocationAndBedType(Location location, BedType bedType, Integer limit, Integer offset) {
        String hql = "select blm.bed " +
                "from BedLocationMapping blm " +
                "join blm.bed b " +
                "join blm.location l " +
                "where b.voided=:voided and b.bedType=:bedType and l=:location";

        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("voided", false);
        query.setParameter("bedType", bedType);
        query.setParameter("location", location);

        if (limit != null) {
            query.setMaxResults(limit);
            if (offset != null)
                query.setFirstResult(offset);
        }

        return query.list();
    }

    @Override
    public List<Bed> getBedsByStatus(BedStatus status, Integer limit, Integer offset) {
        String hql = "select b " +
                "from Bed b " +
                "where b.voided=:voided and b.status=:status";

        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("voided", false);
        query.setParameter("status", status.toString());
        if (limit != null) {
            query.setMaxResults(limit);
            if (offset != null)
                query.setFirstResult(offset);
        }

        return query.list();
    }

    @Override
    public List<Bed> getBedsByLocationAndStatus(Location location, BedStatus status, Integer limit, Integer offset) {
        String hql = "select blm.bed " +
                "from BedLocationMapping blm " +
                "join blm.bed b " +
                "join blm.location l " +
                "where b.voided=:voided and b.status=:status and l=:location";

        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("voided", false);
        query.setParameter("status", status.toString());
        query.setParameter("location", location);
        if (limit != null) {
            query.setMaxResults(limit);
            if (offset != null)
                query.setFirstResult(offset);
        }

        return query.list();
    }

    @Override
    public List<Bed> getBedsByBedTypeAndStatus(BedType bedType, BedStatus status, Integer limit, Integer offset) {
        String hql = "select b " +
                "from Bed b " +
                "where b.voided=:voided and b.status=:status and b.bedType=:bedType";

        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("voided", false);
        query.setParameter("status", status.toString());
        query.setParameter("bedType", bedType);
        if (limit != null) {
            query.setMaxResults(limit);
            if (offset != null)
                query.setFirstResult(offset);
        }

        return query.list();
    }

    @Override
    public List<Bed> getBedsByLocationAndBedTypeAndStatus(Location location, BedType bedType, BedStatus status, Integer limit, Integer offset) {
        String hql = "select blm.bed " +
                "from BedLocationMapping blm " +
                "join blm.bed b " +
                "join blm.location l " +
                "where b.voided=:voided and b.status=:status and b.bedType=:bedType and l=:location";

        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("voided", false);
        query.setParameter("status", status.toString());
        query.setParameter("bedType", bedType);
        query.setParameter("location", location);
        if (limit != null) {
            query.setMaxResults(limit);
            if (offset != null)
                query.setFirstResult(offset);
        }

        return query.list();
    }

    @Override
    public List<Bed> getBedsByLocationUuid(String LocationUuid) {
        String hql = "select blm.bed " +
                "from BedLocationMapping blm " +
                "where blm.bed.voided=:voided and blm.location.uuid=:LocationUuid";

        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("voided", false);
        query.setParameter("LocationUuid", LocationUuid);

        return query.list();
    }

    @Override
    public Long getBedCountByLocation(Location location) {
        String hql = "select count(blm.bed) " +
                "from BedLocationMapping blm " +
                "where blm.bed.voided=:voided and blm.location=:location";

        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("voided", false);
        query.setParameter("location", location);

        return (Long) query.uniqueResult();
    }

    @Override
    public Bed saveBed(Bed bed) {
        Session session = this.sessionFactory.getCurrentSession();
        session.saveOrUpdate(bed);
        session.flush();
        return bed;
    }

    @Override
    public Bed getBedByPatient(Patient patient) {
        Session session = sessionFactory.getCurrentSession();
        Bed bed = (Bed) session.createQuery("select bpa.bed.bedNumber as bedNumber,bpa.bed.id as id from BedPatientAssignment bpa " +
                "where bpa.patient = :patient and bpa.endDatetime is null")
                .setParameter("patient", patient)
                .setResultTransformer(Transformers.aliasToBean(Bed.class))
                .uniqueResult();
        return bed;
    }

    @Override
    public Bed getBedByLocationAndRowColumn(Location location, Integer row, Integer column) {
        String hql = "select blm.bed " +
                "from BedLocationMapping blm " +
                "where blm.location=:location and blm.row=:row and blm.column=:column";

        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("location", location);
        query.setParameter("row", row);
        query.setParameter("column", column);

        return (Bed) query.uniqueResult();
    }

    @Override
    public BedDetails assignPatientToBed(Patient patient, Encounter encounter, Bed bed) {

        Session session = sessionFactory.getCurrentSession();

        BedPatientAssignment bedPatientAssignment = new BedPatientAssignment();
        bedPatientAssignment.setPatient(patient);
        bedPatientAssignment.setEncounter(encounter);
        bedPatientAssignment.setBed(bed);
        bedPatientAssignment.setStartDatetime(Calendar.getInstance().getTime());

        Set<BedPatientAssignment> bedPatientAssignments = new HashSet<BedPatientAssignment>();
        bedPatientAssignments.add(bedPatientAssignment);

        bed.setBedPatientAssignment(bedPatientAssignments);
        bed.setStatus(BedStatus.OCCUPIED.toString());

        session.saveOrUpdate(bed);

        BedDetails bedDetails = new BedDetails();
        bedDetails.setBed(bed);
        bedDetails.setBedNumber(bed.getBedNumber());
        bedDetails.addCurrentAssignment(bedPatientAssignment);
        return bedDetails;
    }

    @Override
    public BedDetails unassignPatient(Patient patient, Bed bed) {
        Session session = sessionFactory.getCurrentSession();
        BedPatientAssignment currentBedPatientAssignment = (BedPatientAssignment) session.createQuery("from BedPatientAssignment where bed=:bed and patient=:patient and endDatetime is null")
                .setParameter("bed", bed)
                .setParameter("patient", patient)
                .uniqueResult();
        currentBedPatientAssignment.setEndDatetime(new Date());
        session.saveOrUpdate(currentBedPatientAssignment);

        Bed bedFromSession = (Bed) session.get(Bed.class, bed.getId());
        List<BedPatientAssignment> activeBedPatientAssignment = filterActiveBedPatientAssignments(bedFromSession);
        if (activeBedPatientAssignment.size() == 0) {
            bedFromSession.setStatus(BedStatus.AVAILABLE.toString());
        }
        session.saveOrUpdate(bedFromSession);

        session.flush();

        BedDetails bedDetails = new BedDetails();
        bedDetails.setBed(bedFromSession);
        bedDetails.setBedNumber(bedFromSession.getBedNumber());
        bedDetails.setLastAssignment(currentBedPatientAssignment);
        return bedDetails;
    }

    @Override
    public Bed getLatestBedByVisitUuid(String visitUuid) {
        Session session = sessionFactory.getCurrentSession();
        Bed bed = (Bed) session.createQuery("select bpa.bed from BedPatientAssignment bpa " +
                "inner join bpa.encounter enc " +
                "inner join enc.visit v where v.uuid = :visitUuid order by bpa.startDatetime DESC")
                .setParameter("visitUuid", visitUuid)
                .setMaxResults(1)
                .uniqueResult();
        return bed;
    }

    @Override
    public List<BedTag> getAllBedTags() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from BedTag where voided =:voided")
                .setParameter("voided", false)
                .list();
    }

    private List<BedPatientAssignment> filterActiveBedPatientAssignments(Bed bedFromSession) {
        List<BedPatientAssignment> activeBedPatientAssignment = new ArrayList<BedPatientAssignment>();
        for (BedPatientAssignment bedPatientAssignment : bedFromSession.getBedPatientAssignment()) {
            if (bedPatientAssignment.getEndDatetime() == null) {
                activeBedPatientAssignment.add(bedPatientAssignment);
            }
        }
        return activeBedPatientAssignment;
    }

}

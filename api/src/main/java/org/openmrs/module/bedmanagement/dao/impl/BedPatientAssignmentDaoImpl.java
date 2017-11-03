package org.openmrs.module.bedmanagement.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openmrs.module.bedmanagement.dao.BedPatientAssignmentDao;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedPatientAssignment;

import java.util.List;

public class BedPatientAssignmentDaoImpl implements BedPatientAssignmentDao {
    SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public BedPatientAssignment getBedPatientAssignmentByUuid(String uuid) {
        Session session = sessionFactory.getCurrentSession();
        return (BedPatientAssignment) session.createQuery("from BedPatientAssignment bpa " +
                "where bpa.uuid = :uuid")
                .setParameter("uuid", uuid)
                .uniqueResult();
    }

    @Override
    public List<BedPatientAssignment> getCurrentAssignmentsForBed(Bed bed) {
        Session session = sessionFactory.getCurrentSession();
        List<BedPatientAssignment> assignments = session.createQuery("from BedPatientAssignment where bed=:bed and endDatetime is null")
                .setParameter("bed", bed)
                .list();
        return assignments;
    }
}

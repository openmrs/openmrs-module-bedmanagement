package org.openmrs.module.bedmanagement.dao.impl;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openmrs.Location;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedLocationMapping;
import org.openmrs.module.bedmanagement.dao.BedLocationMappingDao;

import java.util.List;

public class BedLocationMappingDaoImpl implements BedLocationMappingDao {

    SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public BedLocationMapping saveBedLocationMapping(BedLocationMapping bedLocationMapping) {
        Session session = this.sessionFactory.getCurrentSession();
        session.saveOrUpdate(bedLocationMapping);
        session.flush();
        return bedLocationMapping;
    }

    @Override
    public BedLocationMapping getBedLocationMappingByBed(Bed bed) {
        String hql = "select blm " +
                "from BedLocationMapping blm " +
                "where blm.bed.voided=:voided and blm.bed=:bed";

        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("voided", false);
        query.setParameter("bed", bed);
        return (BedLocationMapping) query.uniqueResult();
    }

    @Override
    public List<BedLocationMapping> getBedLocationMappingByLocation(Location location) {
        String hql = "select blm " +
                "from BedLocationMapping blm " +
                "where blm.location=:location";

        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("location", location);
        return query.list();
    }

    @Override
    public BedLocationMapping getBedLocationMappingByLocationAndRowAndColumn(Location location, Integer row, Integer column) {
        String hql = "select blm " +
                "from BedLocationMapping blm " +
                "where blm.location=:location " +
                "and blm.row=:row and blm.column=:column";

        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("location", location);
        query.setParameter("row", row);
        query.setParameter("column", column);
        return (BedLocationMapping) query.uniqueResult();
    }
}

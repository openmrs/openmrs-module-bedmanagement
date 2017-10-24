package org.openmrs.module.bedmanagement;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class HibernateBedLocationMappingDAO implements BedLocationMappingDAO {

    SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public BedLocationMapping save(BedLocationMapping bedLocationMapping) {
        Session session = this.sessionFactory.getCurrentSession();
        session.saveOrUpdate(bedLocationMapping);
        session.flush();
        return bedLocationMapping;
    }

    @Override
    public List<Bed> listBedByLocationUuid(String locationUuid, Integer limit, Integer offset) {
        String hql = "select blm.bed " +
                "from BedLocationMapping blm " +
                "where blm.location.uuid=:locationUuid";

        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("locationUuid", locationUuid);

        if (limit != null) {
            query.setMaxResults(limit);
            if (offset != null)
                query.setFirstResult(offset);
        }

        return query.list();
    }

    @Override
    public Bed getBedByLocationAndLayout(String locationUuid, Integer row, Integer column) {
        String hql = "select blm.bed " +
                "from BedLocationMapping blm " +
                "where blm.location.uuid=:locationUuid and blm.row=:row and blm.column=:column";

        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("locationUuid", locationUuid);
        query.setParameter("row", row);
        query.setParameter("column", column);

        return (Bed) query.uniqueResult();
    }

    @Override
    public BedLocationMapping getByLocationAndLayout(String locationUuid, Integer row, Integer column) {
        String hql = "select blm " +
                "from BedLocationMapping blm " +
                "where blm.location.uuid=:locationUuid and blm.row=:row and blm.column=:column";

        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("locationUuid", locationUuid);
        query.setParameter("row", row);
        query.setParameter("column", column);

        return (BedLocationMapping) query.uniqueResult();
    }
}

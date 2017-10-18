package org.openmrs.module.bedmanagement;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class HibernateBedDAO implements BedDAO {

    SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Bed getById(int id) {
        return (Bed) sessionFactory.getCurrentSession()
                .createQuery("from Bed b where b.id = :id")
                .setInteger("id", id)
                .uniqueResult();
    }

    @Override
    public List<Bed> getAll(Integer limit, Integer offset) {
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
    public List<Bed> searchByBedType(String bedType, Integer limit, Integer offset) {
        String hql = "select b " +
                "from Bed b " +
                "where b.voided=:voided and b.bedType.name=:bedType";

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
    public List<Bed> searchByBedStatus(String status, Integer limit, Integer offset) {
        String hql = "select b " +
                "from Bed b " +
                "where b.voided=:voided and b.status=:status";

        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("voided", false);
        query.setParameter("status", status);
        if (limit != null) {
            query.setMaxResults(limit);
            if (offset != null)
                query.setFirstResult(offset);
        }

        return query.list();
    }

    @Override
    public List<Bed> searchByBedTypeAndStatus(String bedType, String status, Integer limit, Integer offset) {
        String hql = "select b " +
                "from Bed b " +
                "where b.voided=:voided and b.status=:status and b.bedType.name=:bedType";

        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("voided", false);
        query.setParameter("status", status);
        query.setParameter("bedType", bedType);

        if (limit != null) {
            query.setMaxResults(limit);
            if (offset != null)
                query.setFirstResult(offset);
        }

        return query.list();
    }

    @Override
    public Bed getByUuid(String uuid) {
        return (Bed) sessionFactory.getCurrentSession()
                .createQuery("from Bed b where b.uuid = :uuid")
                .setString("uuid", uuid)
                .uniqueResult();
    }

    @Override
    public Bed save(Bed bed) {
        Session session = this.sessionFactory.getCurrentSession();
        session.saveOrUpdate(bed);
        session.flush();
        return bed;
    }

    @Override
    public List<Bed> getByLocationUuid(String uuid) {
        String hql = "select blm.bed " +
                "from BedLocationMapping blm " +
                "where blm.bed.voided=:voided and blm.location.uuid=:uuid";

        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("voided", false);
        query.setParameter("uuid", uuid);

        return query.list();
    }

    @Override
    public Long getTotalBedByLocationUuid(String uuid) {
        String hql = "select count(blm.bed) " +
                "from BedLocationMapping blm " +
                "where blm.bed.voided=:voided and blm.location.uuid=:uuid";

        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        query.setParameter("voided", false);
        query.setParameter("uuid", uuid);

        return (Long) query.uniqueResult();
    }
}

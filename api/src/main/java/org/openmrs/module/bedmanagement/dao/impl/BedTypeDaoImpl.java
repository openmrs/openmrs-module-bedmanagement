package org.openmrs.module.bedmanagement.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openmrs.module.bedmanagement.entity.BedType;
import org.openmrs.module.bedmanagement.dao.BedTypeDao;

import java.util.List;

public class BedTypeDaoImpl implements BedTypeDao {

    SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public BedType getBedTypeById(Integer id) {
        return (BedType) sessionFactory.getCurrentSession()
                .createQuery("from BedType bt where bt.id = :id")
                .setInteger("id", id)
                .uniqueResult();
    }

    @Override
    public List<BedType> getBedTypes(Integer limit, Integer offset) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BedType.class);
        if (limit != null) {
            criteria.setMaxResults(limit);
            if (offset != null)
                criteria.setFirstResult(offset);
        }

        return criteria.list();
    }

    @Override
    public List<BedType> getBedTypesByName(String name, Integer limit, Integer offset) {
        String hql = "select bt " +
                "from BedType bt " +
                "where bt.name=:name";

        return sessionFactory.getCurrentSession().createQuery(hql).setParameter("name", name).list();
    }

    @Override
    public BedType saveBedType(BedType bedType) {
        Session session = this.sessionFactory.getCurrentSession();
        session.saveOrUpdate(bedType);
        session.flush();
        return bedType;
    }

    @Override
    public void deleteBedType(BedType bedType) {
        Session session = this.sessionFactory.getCurrentSession();
        session.delete(bedType);
        session.flush();
    }
}

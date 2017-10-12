package org.openmrs.module.bedmanagement;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class HibernateBedTypeDAO implements BedTypeDAO {

    SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public BedType getById(int id) {
        return (BedType) sessionFactory.getCurrentSession()
                .createQuery("from BedType bt where bt.id = :id")
                .setInteger("id", id)
                .uniqueResult();
    }

    @Override
    public List<BedType> getAll(String name, Integer limit, Integer offset) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BedType.class);

        if(name != null)
            criteria.add(Restrictions.eq("name", name));

        if (limit != null) {
            criteria.setMaxResults(limit);
            if (offset != null)
                criteria.setFirstResult(offset);
        }

        return criteria.list();
    }

    @Override
    public BedType getByName(String name) {
        String hql = "select bt " +
                "from BedType bt " +
                "where bt.name=:name";

        Query query = sessionFactory.getCurrentSession().createQuery(hql);

        return (BedType)  query.setParameter("name", name).uniqueResult();
    }

    @Override
    public BedType save(BedType bedType) {
        Session session = this.sessionFactory.getCurrentSession();
        session.saveOrUpdate(bedType);
        session.flush();
        return bedType;
    }

    @Override
    public void delete(BedType bedType) {
        Session session = this.sessionFactory.getCurrentSession();
        session.delete(bedType);
        session.flush();
    }
}

package org.openmrs.module.bedmanagement;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

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
    public List<BedType> getAll(Integer limit, Integer offset) {
        String hql = "select bt " +
                "from BedType bt ";

        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        if (limit != null) {
            query.setMaxResults(limit);
            if (offset != null)
                query.setFirstResult(offset);
        }

        return query.list();
    }

    @Override
    public BedType getByName(String name) {
        String hql = "select bt " +
                "from BedType bt " +
                "where bt.name=:name";

        Query query = sessionFactory.getCurrentSession().createQuery(hql);

        return (BedType)  query.setParameter("name", name).uniqueResult();
    }
}

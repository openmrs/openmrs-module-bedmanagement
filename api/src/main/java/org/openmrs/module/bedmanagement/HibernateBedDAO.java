package org.openmrs.module.bedmanagement;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

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
}

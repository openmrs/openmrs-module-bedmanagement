/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.bedmanagement;

import org.hibernate.SessionFactory;
import org.hibernate.Session;


public class HibernateBedTagMapDAO implements BedTagMapDAO {
    SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public BedTagMap saveOrUpdate(BedTagMap bedTagMap) {
        Session session = this.sessionFactory.getCurrentSession();
        session.saveOrUpdate(bedTagMap);
        session.flush();
        return bedTagMap;
    }

    @Override
    public BedTagMap getBedTagMapByUuid(String bedTagMapUuid) {
        return (BedTagMap) sessionFactory.getCurrentSession()
                .createQuery("from BedTagMap where uuid = :uuid and voided =:voided")
                .setParameter("uuid", bedTagMapUuid)
                .setParameter("voided", false)
                .uniqueResult();
    }

    @Override
    public BedTag getBedTagByUuid(String bedTagUuid) {
        return (BedTag) sessionFactory.getCurrentSession()
                .createQuery("from BedTag where uuid = :uuid")
                .setParameter("uuid", bedTagUuid).uniqueResult();
    }

    @Override
    public BedTagMap getBedTagMapWithBedAndTag(Bed bed, BedTag bedTag) {
        return (BedTagMap) sessionFactory.getCurrentSession()
                .createQuery("from BedTagMap where bed = :bed and bedTag = :bedTag and voided =:voided")
                .setParameter("bed", bed)
                .setParameter("bedTag", bedTag)
                .setParameter("voided", false)
                .uniqueResult();
    }
}

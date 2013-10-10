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

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.transform.Transformers;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HibernateBedManagementDAO implements BedManagementDAO {
    SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    public List<AdmissionLocation> getAdmissionLocationsBy(String locationTagName) {
        Session session = sessionFactory.getCurrentSession();

        List<Location> physicalLocations = getPhysicalLocationsByLocationTag(locationTagName, session);

        String hql = "select  blm.location.parentLocation as ward ,count(blm.bed) as totalBeds ," +
                " sum(CASE WHEN blm.bed.status = :occupied THEN 1 ELSE 0 END) as occupiedBeds" +
                " from BedLocationMapping blm where blm.location in (:physicalLocationList) " +
                " group by blm.location.parentLocation";

        List<AdmissionLocation> admissionLocations = session.createQuery(hql)
                .setParameterList("physicalLocationList", physicalLocations)
                .setParameter("occupied", BedStatus.OCCUPIED.toString())
                .setResultTransformer(Transformers.aliasToBean(AdmissionLocation.class))
                .list();

        return admissionLocations;
    }

    private List<Location> getPhysicalLocationsByLocationTag(String locationTagName, Session session) {
        return session.createQuery("select ward.childLocations from Location ward where exists (from ward.tags tag where tag.name = :locationTag)")
                    .setParameter("locationTag", locationTagName).list();
    }

    public Bed getL(int id) {
        Bed bed = (Bed) sessionFactory.getCurrentSession().createQuery("from Bed b where b.id = :id").setInteger("id", id).uniqueResult();
        return bed;
    }

    @Override
    @Transactional
    public AdmissionLocation getLayoutForWard(Location location) {
        Session session = sessionFactory.getCurrentSession();
        List<Location> physicalLocations = getPhysicalLocationsByLocationTagAndParentLocation(BedManagementApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION, location, session);

        String hql = "select blm.row as rowNumber, blm.column as columnNumber, bed.id as bedId, bed.number as bedNumber, bed.status as status from BedLocationMapping blm " +
                "left outer join blm.bed  bed " +
                "where blm.location in (:physicalLocations)";

        List<BedLayout> bedLayouts = sessionFactory.getCurrentSession().createQuery(hql)
                .setParameterList("physicalLocations", physicalLocations)
                .setResultTransformer(Transformers.aliasToBean(BedLayout.class))
                .list();

        AdmissionLocation admissionLocation = new AdmissionLocation();
        admissionLocation.setBedLayouts(bedLayouts);
        return admissionLocation;
    }

    private List<Location> getPhysicalLocationsByLocationTagAndParentLocation(String locationTagName, Location location, Session session) {
        return session.createQuery("from Location physicalLocation where exists (from physicalLocation.tags tag where tag.name = :locationTag) " +
                "and physicalLocation.parentLocation = :ward")
                .setParameter("locationTag", locationTagName)
                .setParameter("ward", location).list();
    }

    @Override
    @Transactional
    public Bed assignPatientToBed(Patient patient, Bed bed) {

        Session session = sessionFactory.getCurrentSession();

        BedPatientAssignment bedPatientAssignment = new BedPatientAssignment();
        bedPatientAssignment.setPatient(patient);
        bedPatientAssignment.setBed(bed);
        bedPatientAssignment.setStartDatetime(Calendar.getInstance().getTime());

        Set<BedPatientAssignment> bedPatientAssignments = new HashSet<BedPatientAssignment>();
        bedPatientAssignments.add(bedPatientAssignment);

        bed.setBedPatientAssignment(bedPatientAssignments);
        bed.setStatus(BedStatus.OCCUPIED.toString());

        session.saveOrUpdate(bed);
        return bed;
    }

    @Override
    public Bed getBedById(int id) {
        Bed bed = null;
        bed = (Bed) sessionFactory.getCurrentSession().createQuery("from Bed b where b.id = :id").setInteger("id", id).uniqueResult();
        return bed;
    }
}

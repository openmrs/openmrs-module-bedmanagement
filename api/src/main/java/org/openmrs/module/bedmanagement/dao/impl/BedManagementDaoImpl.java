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
package org.openmrs.module.bedmanagement.dao.impl;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.module.bedmanagement.AdmissionLocation;
import org.openmrs.module.bedmanagement.BedDetails;
import org.openmrs.module.bedmanagement.BedLayout;
import org.openmrs.module.bedmanagement.BedLayoutWithDetails;
import org.openmrs.module.bedmanagement.constants.BedStatus;
import org.openmrs.module.bedmanagement.dao.BedManagementDao;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedLocationMapping;
import org.openmrs.module.bedmanagement.entity.BedPatientAssignment;
import org.openmrs.module.bedmanagement.entity.BedTag;

import java.util.*;


public class BedManagementDaoImpl implements BedManagementDao {
    SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private List<Location> getPhysicalLocationsByLocationTagAndParentLocation(String locationTagName, Location location, Session session) {
        return session.createQuery("from Location physicalLocation where exists (from physicalLocation.tags tag where tag.name = :locationTag) " +
                "and physicalLocation.parentLocation = :ward")
                .setParameter("locationTag", locationTagName)
                .setParameter("ward", location).list();
    }

    @Override
    public BedDetails assignPatientToBed(Patient patient, Encounter encounter, Bed bed) {

        Session session = sessionFactory.getCurrentSession();

        BedPatientAssignment bedPatientAssignment = new BedPatientAssignment();
        bedPatientAssignment.setPatient(patient);
        bedPatientAssignment.setEncounter(encounter);
        bedPatientAssignment.setBed(bed);
        bedPatientAssignment.setStartDatetime(Calendar.getInstance().getTime());

        Set<BedPatientAssignment> bedPatientAssignments = new HashSet<BedPatientAssignment>();
        bedPatientAssignments.add(bedPatientAssignment);

        bed.setBedPatientAssignment(bedPatientAssignments);
        bed.setStatus(BedStatus.OCCUPIED.toString());

        session.saveOrUpdate(bed);

        BedDetails bedDetails = new BedDetails();
        bedDetails.setBed(bed);
        bedDetails.setBedNumber(bed.getBedNumber());
        bedDetails.addCurrentAssignment(bedPatientAssignment);
        return bedDetails;
    }

    @Override
    public Bed getBedById(int id) {
        Bed bed = null;
        bed = (Bed) sessionFactory.getCurrentSession().createQuery("from Bed b where b.id = :id").setInteger("id", id).uniqueResult();
        return bed;
    }

    @Override
    public Bed getBedByUuid(String uuid) {
        return (Bed) sessionFactory.getCurrentSession().createQuery("from Bed b where b.uuid = :uuid").setString("uuid", uuid).uniqueResult();
    }

    @Override
    public Bed getBedByPatient(Patient patient) {
        Session session = sessionFactory.getCurrentSession();
        Bed bed = (Bed) session.createQuery("select bpa.bed.bedNumber as bedNumber,bpa.bed.id as id from BedPatientAssignment bpa " +
                "where bpa.patient = :patient and bpa.endDatetime is null")
                .setParameter("patient", patient)
                .setResultTransformer(Transformers.aliasToBean(Bed.class))
                .uniqueResult();
        return bed;
    }

    @Override
    public Location getWardForBed(Bed bed) {
        Session session = sessionFactory.getCurrentSession();
        BedLocationMapping bedLocationMapping = (BedLocationMapping) session.createQuery("select blm.location as location from BedLocationMapping blm where blm.bed = :bed")
                .setParameter("bed", bed)
                .setResultTransformer(Transformers.aliasToBean(BedLocationMapping.class))
                .uniqueResult();
        if (bedLocationMapping != null) {
            return bedLocationMapping.getLocation();
        }
        return null;
    }

    @Override
    public BedDetails unassignPatient(Patient patient, Bed bed) {
        Session session = sessionFactory.getCurrentSession();
        BedPatientAssignment currentBedPatientAssignment = (BedPatientAssignment) session.createQuery("from BedPatientAssignment where bed=:bed and patient=:patient and endDatetime is null")
                .setParameter("bed", bed)
                .setParameter("patient", patient)
                .uniqueResult();
        currentBedPatientAssignment.setEndDatetime(new Date());
        session.saveOrUpdate(currentBedPatientAssignment);

        Bed bedFromSession = (Bed) session.get(Bed.class, bed.getId());
        List<BedPatientAssignment> activeBedPatientAssignment = filterActiveBedPatientAssignments(bedFromSession);
        if(activeBedPatientAssignment.size() == 0) {
            bedFromSession.setStatus(BedStatus.AVAILABLE.toString());
        }
        session.saveOrUpdate(bedFromSession);

        session.flush();

        BedDetails bedDetails = new BedDetails();
        bedDetails.setBed(bedFromSession);
        bedDetails.setBedNumber(bedFromSession.getBedNumber());
        bedDetails.setLastAssignment(currentBedPatientAssignment);
        return bedDetails;
    }

    private List<BedPatientAssignment> filterActiveBedPatientAssignments(Bed bedFromSession) {
        List<BedPatientAssignment> activeBedPatientAssignment = new ArrayList<BedPatientAssignment>();
        for(BedPatientAssignment bedPatientAssignment: bedFromSession.getBedPatientAssignment()){
            if(bedPatientAssignment.getEndDatetime() == null){
                activeBedPatientAssignment.add(bedPatientAssignment);
            }
        }
        return activeBedPatientAssignment;
    }

    @Override
    public BedPatientAssignment getBedPatientAssignmentByUuid(String uuid) {
        Session session = sessionFactory.getCurrentSession();
        return  (BedPatientAssignment) session.createQuery("from BedPatientAssignment bpa " +
                "where bpa.uuid = :uuid")
                .setParameter("uuid", uuid)
                .uniqueResult();
    }

    @Override
    public List<BedPatientAssignment> getCurrentAssignmentsByBed(Bed bed) {
        Session session = sessionFactory.getCurrentSession();
        List<BedPatientAssignment> assignments = session.createQuery("from BedPatientAssignment where bed=:bed and endDatetime is null")
                .setParameter("bed", bed)
                .list();
        return assignments;
    }

    @Override
    public Bed getLatestBedByVisit(String visitUuid) {
        Session session = sessionFactory.getCurrentSession();
        Bed bed = (Bed) session.createQuery("select bpa.bed from BedPatientAssignment bpa " +
                "inner join bpa.encounter enc " +
                "inner join enc.visit v where v.uuid = :visitUuid order by bpa.startDatetime DESC")
                .setParameter("visitUuid", visitUuid)
                .setMaxResults(1)
                .uniqueResult();
        return bed;
    }

    @Override
    public List<BedTag> getAllBedTags() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from BedTag where voided =:voided")
                .setParameter("voided", false)
                .list();
    }

    @Override
    public List<AdmissionLocation> getAdmissionLocations(List<Location> locations) {
        String sql = "select l from Location l " +
                "where l in :locations and " +
                "(l.parentLocation not in :locations or l.parentLocation is null) and " +
                "l.retired=0";
        Query query = sessionFactory.getCurrentSession().createQuery(sql);
        query.setParameterList("locations", locations);
        List<Location> locationList = query.list();

        List<AdmissionLocation> admissionLocations = new ArrayList<>();
        for(Location location : locationList){
            admissionLocations.add(this.getAdmissionLocationForLocation(location));
        }

        return admissionLocations;
    }

    @Override
    public AdmissionLocation getAdmissionLocationForLocation(Location location) {
        Session session = sessionFactory.getCurrentSession();
        Set<Location> locations = new HashSet<Location>(Arrays.asList(location));
        Set<Location> childLocations = location.getChildLocations();
        if (!CollectionUtils.isEmpty(childLocations)) {
            locations.addAll(childLocations);
        }

        String hql = "select count(blm.bed) as totalBeds ," +
                " COALESCE(sum(CASE WHEN blm.bed IS NOT NULL AND blm.bed.status = :occupied THEN 1 ELSE 0 END), 0) as occupiedBeds" +
                " from BedLocationMapping blm where blm.location in (:locations)";

        AdmissionLocation admissionLocation = (AdmissionLocation) session.createQuery(hql)
                .setParameterList("locations", locations)
                .setParameter("occupied", BedStatus.OCCUPIED.toString())
                .setResultTransformer(Transformers.aliasToBean(AdmissionLocation.class))
                .uniqueResult();
        List<BedLayout> bedLayouts = getBedLayoutByLocation(location);

        admissionLocation.setWard(location);
        admissionLocation.setBedLayouts(bedLayouts);
        return admissionLocation;
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

    @Override
    public BedLocationMapping saveBedLocationMapping(BedLocationMapping bedLocationMapping) {
        Session session = this.sessionFactory.getCurrentSession();
        session.saveOrUpdate(bedLocationMapping);
        session.flush();
        return bedLocationMapping;
    }

    @Override
    public List<BedLayout> getBedLayoutByLocation(Location location) {
        Set<Location> locations = new HashSet<Location>(Arrays.asList(location));
        Set<Location> childLocations = location.getChildLocations();
        if (!CollectionUtils.isEmpty(childLocations)) {
            locations.addAll(childLocations);
        }

        String hql = "select blm.row as rowNumber, blm.column as columnNumber, " +
                "bed as bed, blm.location.name as location " +
                "from BedLocationMapping blm " +
                "left outer join blm.bed bed " +
                "where blm.location in (:locations) ";

        List<BedLayoutWithDetails> bedLayoutWithDetailsList = sessionFactory.getCurrentSession().createQuery(hql)
                .setParameterList("locations", locations)
                .setResultTransformer(Transformers.aliasToBean(BedLayoutWithDetails.class))
                .list();

        List<BedLayout> bedLayouts = new ArrayList<>();
        for (BedLayoutWithDetails bedLayoutWithDetails : bedLayoutWithDetailsList) {
            bedLayouts.add(bedLayoutWithDetails.convertToBedLayout());
        }

        return bedLayouts;
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
}

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
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;

import java.util.*;


public class HibernateBedManagementDAO implements BedManagementDAO {
    SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
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
    public AdmissionLocation getLayoutForWard(Location location) {
        Session session = sessionFactory.getCurrentSession();
        List<Location> physicalLocations = getPhysicalLocationsByLocationTagAndParentLocation(BedManagementApiConstants.LOCATION_TAG_SUPPORTS_ADMISSION, location, session);

        String hql = "select blm.row as rowNumber, blm.column as columnNumber, " +
                "bed as bed, blm.location.name as location " +
                "from BedLocationMapping blm " +
                "left outer join blm.bed bed " +
                "where blm.location in (:physicalLocations) ";

        List<BedLayoutWithDetails> bedLayoutWithDetailsList = sessionFactory.getCurrentSession().createQuery(hql)
                .setParameterList("physicalLocations", physicalLocations)
                .setResultTransformer(Transformers.aliasToBean(BedLayoutWithDetails.class))
                .list();

        List<BedLayout> bedLayouts = new ArrayList<>();
        for(BedLayoutWithDetails bedLayoutWithDetails : bedLayoutWithDetailsList) {
            bedLayouts.add(bedLayoutWithDetails.convertToBedLayout());
        }
        AdmissionLocation admissionLocation = new AdmissionLocation();
        admissionLocation.setBedLayouts(bedLayouts);
        return admissionLocation;
    }

    @Override
    public List<Integer> getAdmissionLocationIds(){
        Session session = sessionFactory.getCurrentSession();
        String sql = "SELECT ltm.location_id\n" +
                "  FROM location_tag_map ltm\n" +
                "    LEFT JOIN location_tag lt ON ltm.location_tag_id = lt.location_tag_id\n" +
                "  WHERE lt.name = 'Admission Location'";

        SQLQuery query = session.createSQLQuery(sql);
        return query.list();
    }

    @Override
    public List<Location> getWards(){
        List<Integer> admissionLocationIds =  getAdmissionLocationIds();
        String sql = "select l from Location l " +
                "where l.locationId in :admissionLocationIds and " +
                "(l.parentLocation.locationId not in :admissionLocationIds or l.parentLocation.locationId is null) and " +
                "l.retired=0";
        Query query = sessionFactory.getCurrentSession().createQuery(sql);
        query.setParameterList("admissionLocationIds", admissionLocationIds);
        return query.list();
    }

    @Override
    public List<Location> searchWardByName(String name) {
        List<Integer> admissionLocationIds =  getAdmissionLocationIds();
        String sql = "select l from Location l " +
                "where l.locationId in :admissionLocationIds and " +
                "(l.parentLocation.locationId not in :admissionLocationIds or l.parentLocation.locationId is null) and " +
                "l.retired=0 and l.name=:name";
        Query query = sessionFactory.getCurrentSession().createQuery(sql);
        query.setParameter("name", name);
        query.setParameterList("admissionLocationIds", admissionLocationIds);
        return query.list();
    }

    @Override
    public Location getWardByUuid(String uuid) {
        List<Integer> admissionLocationIds =  getAdmissionLocationIds();
        String sql = "select l from Location l " +
                "where l.locationId in :admissionLocationIds and " +
                "(l.parentLocation.locationId not in :admissionLocationIds or l.parentLocation.locationId is null) and " +
                "l.retired=0 and l.uuid=:uuid";
        Query query = sessionFactory.getCurrentSession().createQuery(sql);
        query.setParameter("uuid", uuid);
        query.setParameterList("admissionLocationIds", admissionLocationIds);
        return (Location) query.uniqueResult();
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
}

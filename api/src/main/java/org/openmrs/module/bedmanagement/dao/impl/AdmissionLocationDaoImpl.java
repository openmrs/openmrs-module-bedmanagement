package org.openmrs.module.bedmanagement.dao.impl;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.openmrs.Location;
import org.openmrs.module.bedmanagement.constants.BedManagementApiConstants;
import org.openmrs.module.bedmanagement.constants.BedStatus;
import org.openmrs.module.bedmanagement.dao.AdmissionLocationDao;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedLocationMapping;
import org.openmrs.module.bedmanagement.pojo.AdmissionLocation;
import org.openmrs.module.bedmanagement.pojo.BedLayout;
import org.openmrs.module.bedmanagement.pojo.BedLayoutWithDetails;

import java.util.ArrayList;
import java.util.List;

public class AdmissionLocationDaoImpl implements AdmissionLocationDao {
    SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<AdmissionLocation> getAdmissionLocationsByLocationTagName(String locationTagName) {
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
    public List<Integer> getAdmissionLocationIds() {
        Session session = sessionFactory.getCurrentSession();
        String sql = "SELECT ltm.location_id\n" +
                "  FROM location_tag_map ltm\n" +
                "    LEFT JOIN location_tag lt ON ltm.location_tag_id = lt.location_tag_id\n" +
                "  WHERE lt.name = 'Admission Location'";

        SQLQuery query = session.createSQLQuery(sql);
        return query.list();
    }

    @Override
    public List<Location> getWards() {
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
    public Location getWardByLocationUuid(String locationUuid) {
        List<Integer> admissionLocationIds =  getAdmissionLocationIds();
        String sql = "select l from Location l " +
                "where l.locationId in :admissionLocationIds and " +
                "(l.parentLocation.locationId not in :admissionLocationIds or l.parentLocation.locationId is null) and " +
                "l.retired=0 and l.uuid=:locationUuid";
        Query query = sessionFactory.getCurrentSession().createQuery(sql);
        query.setParameter("locationUuid", locationUuid);
        query.setParameterList("admissionLocationIds", admissionLocationIds);
        return (Location) query.uniqueResult();
    }

    @Override
    public List<Location> getWardsByName(String name) {
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

    private List<Location> getPhysicalLocationsByLocationTag(String locationTagName, Session session) {
        return session.createQuery("select ward.childLocations from Location ward where exists (from ward.tags tag where tag.name = :locationTag)")
                .setParameter("locationTag", locationTagName).list();
    }

    private List<Location> getPhysicalLocationsByLocationTagAndParentLocation(String locationTagName, Location location, Session session) {
        return session.createQuery("from Location physicalLocation where exists (from physicalLocation.tags tag where tag.name = :locationTag) " +
                "and physicalLocation.parentLocation = :ward")
                .setParameter("locationTag", locationTagName)
                .setParameter("ward", location).list();
    }

}

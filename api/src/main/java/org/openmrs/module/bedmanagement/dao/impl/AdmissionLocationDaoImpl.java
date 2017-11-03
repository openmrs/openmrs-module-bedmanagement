package org.openmrs.module.bedmanagement.dao.impl;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.openmrs.Location;
import org.openmrs.module.bedmanagement.constants.BedStatus;
import org.openmrs.module.bedmanagement.dao.AdmissionLocationDao;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedLocationMapping;
import org.openmrs.module.bedmanagement.AdmissionLocation;
import org.openmrs.module.bedmanagement.BedLayout;
import org.openmrs.module.bedmanagement.BedLayoutWithDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AdmissionLocationDaoImpl implements AdmissionLocationDao {
    SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<BedLayout> getBedLayoutByLocation(Location location) {
        List<Integer> locationIds = new ArrayList<>();
        locationIds.add(location.getId());
        Set<Location> childLocations = location.getChildLocations();
        if(childLocations != null)
            for (Location childLocation : childLocations) {
                locationIds.add(childLocation.getId());
            }

        String hql = "select blm.row as rowNumber, blm.column as columnNumber, " +
                "bed as bed, blm.location.name as location " +
                "from BedLocationMapping blm " +
                "left outer join blm.bed bed " +
                "where blm.location.locationId in (:locationIds) ";

        List<BedLayoutWithDetails> bedLayoutWithDetailsList = sessionFactory.getCurrentSession().createQuery(hql)
                .setParameterList("locationIds", locationIds)
                .setResultTransformer(Transformers.aliasToBean(BedLayoutWithDetails.class))
                .list();

        List<BedLayout> bedLayouts = new ArrayList<>();
        for (BedLayoutWithDetails bedLayoutWithDetails : bedLayoutWithDetailsList) {
            bedLayouts.add(bedLayoutWithDetails.convertToBedLayout());
        }

        return bedLayouts;
    }

    @Override
    public AdmissionLocation getAdmissionLocationsByLocation(Location location) {
        Session session = sessionFactory.getCurrentSession();
        List<Integer> locationIds = new ArrayList<>();
        locationIds.add(location.getId());
        Set<Location> childLocations = location.getChildLocations();
        if(childLocations != null)
            for (Location childLocation : childLocations) {
                locationIds.add(childLocation.getId());
            }

        String hql = "select count(blm.bed) as totalBeds ," +
                " COALESCE(sum(CASE WHEN blm.bed IS NOT NULL AND blm.bed.status = :occupied THEN 1 ELSE 0 END), 0) as occupiedBeds" +
                " from BedLocationMapping blm where blm.location.locationId in (:locationIds)";

        AdmissionLocation admissionLocation = (AdmissionLocation) session.createQuery(hql)
                .setParameterList("locationIds", locationIds)
                .setParameter("occupied", BedStatus.OCCUPIED.toString())
                .setResultTransformer(Transformers.aliasToBean(AdmissionLocation.class))
                .uniqueResult();
        List<BedLayout> bedLayouts = getBedLayoutByLocation(location);

        admissionLocation.setWard(location);
        admissionLocation.setBedLayouts(bedLayouts);
        return admissionLocation;
    }

    @Override
    public List<AdmissionLocation> getAdmissionLocations() {
        List<Integer> admissionLocationIds = getAdmissionLocationIds();
        String sql = "select l from Location l " +
                "where l.locationId in :admissionLocationIds and " +
                "(l.parentLocation.locationId not in :admissionLocationIds or l.parentLocation.locationId is null) and " +
                "l.retired=0";
        Query query = sessionFactory.getCurrentSession().createQuery(sql);
        query.setParameterList("admissionLocationIds", admissionLocationIds);
        List<Location> locations = (List<Location>) query.list();

        List<AdmissionLocation> admissionLocations = new ArrayList<AdmissionLocation>();
        for(Location location : locations){
            admissionLocations.add(this.getAdmissionLocationsByLocation(location));
        }

        return admissionLocations;
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

}

package org.openmrs.module.bedmanagement.dao;

import org.openmrs.Location;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.AdmissionLocation;
import org.openmrs.module.bedmanagement.BedLayout;

import java.util.List;

public interface AdmissionLocationDao {
    /**
     * Get bed layout by location
     *
     * @param location {@link Location}
     * @return {@link List<BedLayout>}
     */
    List<BedLayout> getBedLayoutByLocation(Location location);

    /**
     * Get admission location by location
     *
     * @param location {@link Location}
     * @return {@link AdmissionLocation} return null if not exist
     */
    AdmissionLocation getAdmissionLocationsByLocation(Location location);

    /**
     * Get all admission locations
     *
     * @return {@link List<AdmissionLocation>}
     */
    List<AdmissionLocation> getAdmissionLocations();

    /**
     * Get admissionLocation Ids
     * @return {@link List<Integer>}
     */
    List<Integer> getAdmissionLocationIds();

    /**
     * Get ward location by bed
     * @param bed {@link Bed}
     * @return {@link Location}
     */
    Location getWardForBed(Bed bed);
}

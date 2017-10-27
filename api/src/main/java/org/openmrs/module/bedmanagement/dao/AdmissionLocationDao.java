package org.openmrs.module.bedmanagement.dao;

import org.openmrs.Location;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.pojo.AdmissionLocation;

import java.util.List;

public interface AdmissionLocationDao {
    /**
     * Get admissionLocation by location tag name
     * @param locationTagName {@link String} location tag name
     * @return {@link AdmissionLocation}
     */
    List<AdmissionLocation> getAdmissionLocationsByLocationTagName(String locationTagName);

    /**
     * Get layout for ward
     * @param location {@link Location} ward's room location
     * @return {@link AdmissionLocation}
     */
    AdmissionLocation getLayoutForWard(Location location);

    /**
     * Get ward location by bed
     * @param bed {@link Bed}
     * @return {@link Location}
     */
    Location getWardForBed(Bed bed);

    /**
     * Get admissionLocation Ids
     * @return {@link List<Integer>}
     */
    List<Integer> getAdmissionLocationIds();

    /**
     * Get wards {@link Location}
     * @return {@link Location}
     */
    List<Location> getWards();

    /**
     * Get ward by locationUuid
     * @param locationUuid {@link String} location uuid
     * @return {@link Location} null if not exist
     */
    Location getWardByLocationUuid(String locationUuid);

    /**
     * Get ward by ward name
     * @param name {@link String} ward name
     * @return {@link Location}
     */
    List<Location> getWardsByName(String name);
}

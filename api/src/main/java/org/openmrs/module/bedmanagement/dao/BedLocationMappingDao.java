package org.openmrs.module.bedmanagement.dao;

import org.openmrs.Location;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedLocationMapping;

import java.util.List;

public interface BedLocationMappingDao {

    /**
     * Save / Update Bed location mapping
     *
     * @param bedLocationMapping {@link BedLocationMapping}
     * @return {@link BedLocationMapping}
     */
    BedLocationMapping saveBedLocationMapping(BedLocationMapping bedLocationMapping);

    /**
     * Get bed location mapping {@link BedLocationMapping} by bed
     *
     * @param bed {@link Bed} bed
     * @return {@link BedLocationMapping}
     */
    BedLocationMapping getBedLocationMappingByBed(Bed bed);

    /**
     * Get bed location mappings by location
     *
     * @param location {@link Location}
     * @return {@link List<BedLocationMapping>}
     */
    List<BedLocationMapping> getBedLocationMappingByLocation(Location location);

    /**
     * Get bed location mapping by location and row and column
     *
     * @param location {@link Location}  ward's room location
     * @param row {@link Integer} bed row
     * @param column {@link Integer} bed column
     * @return {@link BedLocationMapping}
     */
    BedLocationMapping getBedLocationMappingByLocationAndRowAndColumn(Location location, Integer row, Integer column);
}

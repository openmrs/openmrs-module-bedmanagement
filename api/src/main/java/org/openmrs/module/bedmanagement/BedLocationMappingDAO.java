package org.openmrs.module.bedmanagement;

import java.util.List;

public interface BedLocationMappingDAO {

    BedLocationMapping save(BedLocationMapping bedLocationMapping);

    List<Bed> listBedByLocationUuid(String locationUuid, Integer limit, Integer offset);

    Bed getBedByLocationAndLayout(String locationUuid, Integer row, Integer column);

    BedLocationMapping getByLocationAndLayout(String locationUuid, Integer row, Integer column);
}

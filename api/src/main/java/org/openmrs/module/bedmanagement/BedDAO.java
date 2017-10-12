package org.openmrs.module.bedmanagement;

import java.util.List;
import java.util.Map;

public interface BedDAO {
    Bed getById(int id);

    List<Bed> getAll(String locationUuid, Integer limit, Integer offset);

    List<Bed> searchByBedType(String locationUuid, String bedType, Integer limit, Integer offset);

    List<Bed> searchByBedStatus(String locationUuid, String status, Integer limit, Integer offset);

    List<Bed> searchByBedTypeAndStatus(String locationUuid, String bedType, String status, Integer limit, Integer offset);

    Bed getByUuid(String uuid);

    Bed save(Bed bed);

    List<Bed> getByLocationUuid(String uuid);

    Long getTotalBedByLocationUuid(String uuid);

    BedLocationMapping getBedLocationMappingByBedId(Integer bedId);

    BedLocationMapping getBedLocationMappingByLocationAndLayout(String locationUuid, Integer row, Integer column);
}

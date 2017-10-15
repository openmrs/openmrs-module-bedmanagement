package org.openmrs.module.bedmanagement;

import java.util.List;

public interface BedDAO {
    Bed getById(int id);

    List<Bed> getAll(Integer limit, Integer offset);

    List<Bed> searchByBedType(String bedType, Integer limit, Integer offset);

    List<Bed> searchByBedStatus(String status, Integer limit, Integer offset);

    List<Bed> searchByBedTypeAndStatus(String bedType, String status, Integer limit, Integer offset);

    Bed getByUuid(String uuid);

    Bed save(Bed bed);
}

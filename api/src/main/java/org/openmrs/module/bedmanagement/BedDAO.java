package org.openmrs.module.bedmanagement;

public interface BedDAO {
    Bed getById(int id);

    Bed getByUuid(String uuid);

    Bed save(Bed bed);
}

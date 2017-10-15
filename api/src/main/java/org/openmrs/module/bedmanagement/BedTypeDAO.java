package org.openmrs.module.bedmanagement;

import java.util.List;

public interface BedTypeDAO {
    BedType getById(int id);

    List<BedType> getAll(Integer limit, Integer offset);

    BedType getByName(String name);
}

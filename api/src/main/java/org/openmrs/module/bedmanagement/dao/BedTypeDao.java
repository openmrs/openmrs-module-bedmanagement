package org.openmrs.module.bedmanagement.dao;

import org.openmrs.module.bedmanagement.entity.BedType;

import java.util.List;

public interface BedTypeDao {
    /**
     * Get bed type by Id
     * @param id {@link Integer} bed Id
     * @return {@link BedType}
     */
    BedType getBedTypeById(Integer id);

    /**
     * Get bed sypes
     *
     * @param limit {@link Integer} limit result set, return all result set if limit is null
     * @param offset {@link Integer} specify the starting row offset into the result set
     * @return {@link List< BedType >}
     */
    List<BedType> getBedTypes(Integer limit, Integer offset);

    /**
     * Get bed types by bed type name
     *
     * @param name {@link String} bed type name
     * @param limit {@link Integer} limit result set, return all result set if limit is null
     * @param offset {@link Integer} specify the starting row offset into the result set
     * @return {@link List< BedType >}
     */
    List<BedType> getBedTypesByName(String name, Integer limit, Integer offset);

    /**
     * Save/Update bed type
     *
     * @param bedType {@link BedType}
     * @return {@link BedType}
     */
    BedType saveBedType(BedType bedType);

    /**
     * Delete Bed type
     *
     * @param bedType {@link BedType}
     */
    void deleteBedType(BedType bedType);
}

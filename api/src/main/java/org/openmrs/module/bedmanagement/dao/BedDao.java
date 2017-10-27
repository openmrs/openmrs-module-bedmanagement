package org.openmrs.module.bedmanagement.dao;

import org.openmrs.Encounter;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.module.bedmanagement.constants.BedStatus;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedTag;
import org.openmrs.module.bedmanagement.entity.BedType;
import org.openmrs.module.bedmanagement.pojo.BedDetails;

import java.util.List;

public interface BedDao {
    /**
     * Get Bed By bed Id, Return null if not exist
     *
     * @param id {@link Integer}
     * @return {@link Bed}
     */
    Bed getBedById(Integer id);

    /**
     * Get Bed by bed uuid, Return null if not exist
     *
     * @param uuid {@link String} bedUuid
     * @return {@link Bed}
     */
    Bed getBedByUuid(String uuid);

    /**
     * Get beds.
     *
     * @param limit {@link Integer} limit result set, return all result set if limit is null
     * @param offset {@link Integer} specify the starting row offset into the result set
     * @return {@link List<Bed>}
     */
    List<Bed> getBeds(Integer limit, Integer offset);

    /**
     * Get beds by location
     *
     * @param location {@link Location} ward's room location
     * @param limit {@link Integer} limit result set, return all result set if limit is null
     * @param offset {@link Integer} specify the starting row offset into the result set
     * @return {@link List<Bed>}
     */
    List<Bed> getBedsByLocation(Location location, Integer limit, Integer offset);

    /**
     * Get beds by bed type
     *
     * @param bedType {@link BedType} bed type
     * @param limit {@link Integer} limit result set, return all result set if limit is null
     * @param offset {@link Integer} specify the starting row offset into the result set
     * @return {@link List<Bed>}
     */
    List<Bed> getBedsByBedType(BedType bedType, Integer limit, Integer offset);

    /**
     * Get beds by location & bed type
     *
     * @param location {@link Location} ward's room location
     * @param bedType {@link BedType} bed type
     * @param limit {@link Integer} limit result set, return all result set if limit is null
     * @param offset {@link Integer} specify the starting row offset into the result set
     * @return {@link List<Bed>}
     */
    List<Bed> getBedsByLocationAndBedType(Location location, BedType bedType, Integer limit, Integer offset);

    /**
     * Get beds by status
     *
     * @param status {@link BedStatus} bed status
     * @param limit {@link Integer} limit result set, return all result set if limit is null
     * @param offset {@link Integer} specify the starting row offset into the result set
     * @return {@link List<Bed>}
     */
    List<Bed> getBedsByStatus(BedStatus status, Integer limit, Integer offset);

    /**
     * Get beds by location and status
     *
     * @param location {@link Location} ward's room location
     * @param status {@link BedStatus} bed status
     * @param limit {@link Integer} limit result set, return all result set if limit is null
     * @param offset {@link Integer} specify the starting row offset into the result set
     * @return {@link List<Bed>}
     */
    List<Bed> getBedsByLocationAndStatus(Location location, BedStatus status, Integer limit, Integer offset);

    /**
     * Get beds by bed type and status
     *
     * @param bedType {@link BedType} bed type
     * @param status {@link BedStatus} bed status
     * @param limit {@link Integer} limit result set, return all result set if limit is null
     * @param offset {@link Integer} specify the starting row offset into the result set
     * @return {@link List<Bed>}
     */
    List<Bed> getBedsByBedTypeAndStatus(BedType bedType, BedStatus status, Integer limit, Integer offset);

    /**
     * Get beds by location and bed type and status
     *
     * @param location {@link Location} ward's room location
     * @param bedType {@link BedType} bed type
     * @param status {@link BedStatus} bed status
     * @param limit {@link Integer} limit result set, return all result set if limit is null
     * @param offset {@link Integer} specify the starting row offset into the result set
     * @return {@link List<Bed>}
     */
    List<Bed> getBedsByLocationAndBedTypeAndStatus(Location location, BedType bedType, BedStatus status, Integer limit, Integer offset);

    /**
     * Get beds by location{@link Location} uuid
     *
     * @param LocationUuid {@link String} location uuid
     * @return {@link List<Bed>}
     */
    List<Bed> getBedsByLocationUuid(String LocationUuid);

    /**
     * Get total bed number by location {@link Location} uuid
     *
     * @param location {@link Location} ward's room location
     * @return {@link Long} total number of beds
     */
    Long getBedCountByLocation(Location location);

    /**
     * Save / update bed
     *
     * @param bed {@link Bed}
     * @return {@link Bed}
     */
    Bed saveBed(Bed bed);

    /**
     * Get bed by patient
     *
     * @param patient {@link Patient}
     * @return {@link Bed}
     */
    Bed getBedByPatient(Patient patient);

    /**
     * Get bed by Location and row and column
     *
     * @param location {@link Location} ward's room location
     * @param row {@link Integer} bed row
     * @param column {@link Integer} bed column
     * @return {@link Bed}
     */
    Bed getBedByLocationAndRowColumn(Location location, Integer row, Integer column);

    /**
     * Get bed details of encounter by patient and bed
     *
     * @param patient {@link Patient}
     * @param encounter {@link Encounter}
     * @param bed {@link Bed}
     * @return {@link BedDetails}
     */
    BedDetails assignPatientToBed(Patient patient, Encounter encounter, Bed bed);

    /**
     * Unassign patient form bed
     *
     * @param patient {@link Patient}
     * @param bed {@link Bed}
     * @return {@link BedDetails}
     */
    BedDetails unassignPatient(Patient patient, Bed bed);

    /**
     * Get last visit bed by visit uuid
     *
     * @param visitUuid {@link String} visit Uuid
     * @return {@link Bed}
     */
    Bed getLatestBedByVisitUuid(String visitUuid);

    /**
     * Get all bed tags
     *
     * @return {@link List<BedTag>}
     */
    List<BedTag> getAllBedTags();
}

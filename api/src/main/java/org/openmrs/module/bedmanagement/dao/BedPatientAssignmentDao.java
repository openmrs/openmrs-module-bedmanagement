package org.openmrs.module.bedmanagement.dao;


import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedPatientAssignment;

import java.util.List;

public interface BedPatientAssignmentDao {
    /**
     * Get bed patient assignment by Uuid
     *
     * @param uuid {@link String} uuid
     * @return {@link BedPatientAssignment}
     */
    BedPatientAssignment getBedPatientAssignmentByUuid(String uuid);

    /**
     * Get current bed patient assignment for bed
     *
     * @param bed {@link Bed}
     * @return {@link BedPatientAssignment}
     */
    List<BedPatientAssignment> getCurrentAssignmentsForBed(Bed bed);
}

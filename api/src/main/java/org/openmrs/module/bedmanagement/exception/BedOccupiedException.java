package org.openmrs.module.bedmanagement.exception;

import org.openmrs.api.APIException;
import org.openmrs.module.bedmanagement.entity.Bed;

/**
 * Indicates the user tried to perform some action that is invalid because the bed is occupied
 */
public class BedOccupiedException extends APIException {
	
	private Bed bed;
	
	public BedOccupiedException(Bed bed) {
		this.bed = bed;
	}
	
	@Override
	public String getMessage() {
		return "Cannot perform action on an occupied bed: " + bed.getBedNumber() + " (" + bed.getUuid() + ")";
	}
}

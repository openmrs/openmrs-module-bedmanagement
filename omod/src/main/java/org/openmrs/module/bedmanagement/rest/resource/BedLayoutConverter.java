/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.bedmanagement.rest.resource;

import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.BedLayout;
import org.openmrs.module.bedmanagement.entity.BedTagMap;
import org.openmrs.module.bedmanagement.entity.BedType;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.CustomRepresentation;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Resource;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingConverter;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PatientResource1_8;

import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of Converter to be able to create a representation for a BedLayout
 */
@Handler(supports = BedLayout.class, order = 0)
public class BedLayoutConverter extends BaseDelegatingConverter<BedLayout> {
	
	private static final String DEFAULT_PATIENT_REP = "(uuid,person:(gender,age,preferredName:(givenName,familyName),preferredAddress:default),identifiers:(identifier))";
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof CustomRepresentation) {
			return null;
		}
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("rowNumber");
		description.addProperty("columnNumber");
		description.addProperty("bedNumber");
		description.addProperty("bedId");
		description.addProperty("bedUuid");
		description.addProperty("status");
		description.addProperty("location");
		description.addProperty("bedType", rep);
		description.addProperty("patients", rep);
		description.addProperty("bedTagMaps", rep);
		return description;
	}
	
	@Override
	public BedLayout newInstance(String type) {
		return new BedLayout();
	}
	
	@Override
	public BedLayout getByUniqueId(String string) {
		return null;
	}
	
	@Override
	public SimpleObject asRepresentation(BedLayout bedLayout, Representation rep) throws ConversionException {
		SimpleObject ret = new SimpleObject();
		ret.add("rowNumber", bedLayout.getRowNumber());
		ret.add("columnNumber", bedLayout.getColumnNumber());
		ret.add("bedNumber", bedLayout.getBedNumber());
		ret.add("bedId", bedLayout.getBedId());
		ret.add("bedUuid", bedLayout.getBedUuid());
		ret.add("status", bedLayout.getStatus());
		ret.add("location", bedLayout.getLocation());
		
		Representation bedTypeRepresentation = null;
		BedTypeResource bedTypeResource = (BedTypeResource) getResourceForSupportedClass(BedType.class);
		if (rep instanceof CustomRepresentation) {
			bedTypeRepresentation = getRepresentationForProperty(rep, "bedType");
		} else {
			bedTypeRepresentation = rep;
		}
		if (bedTypeRepresentation != null) {
			ret.add("bedType", bedTypeResource.asRepresentation(bedLayout.getBedType(), bedTypeRepresentation));
		}
		
		PatientResource1_8 patientResource = (PatientResource1_8) getResourceForSupportedClass(Patient.class);
		Representation patientRepresentation = null;
		if (rep instanceof DefaultRepresentation) {
			patientRepresentation = new CustomRepresentation(DEFAULT_PATIENT_REP);
		} else if (rep instanceof FullRepresentation) {
			patientRepresentation = rep;
		} else if (rep instanceof CustomRepresentation) {
			patientRepresentation = getRepresentationForProperty(rep, "patients");
		}
		if (patientRepresentation != null) {
			List<SimpleObject> patients = new ArrayList<>();
			if (bedLayout.getPatients() != null) {
				for (Patient p : bedLayout.getPatients()) {
					patients.add(patientResource.asRepresentation(p, patientRepresentation));
				}
			}
			ret.add("patients", patients);
		}
		
		Representation bedTagMapRep = null;
		BedTagMapResource bedTagMapResource = (BedTagMapResource) getResourceForSupportedClass(BedTagMap.class);
		if (rep instanceof CustomRepresentation) {
			bedTagMapRep = getRepresentationForProperty(rep, "bedTagMaps");
		} else {
			bedTagMapRep = rep;
		}
		if (bedTagMapRep != null) {
			List<SimpleObject> bedTagMaps = new ArrayList<>();
			if (bedLayout.getBedTagMaps() != null) {
				for (BedTagMap bedTagMap : bedLayout.getBedTagMaps()) {
					bedTagMaps.add(bedTagMapResource.asRepresentation(bedTagMap, bedTagMapRep));
				}
			}
			ret.add("bedTagMaps", bedTagMaps);
		}
		
		return ret;
	}
	
	protected Resource getResourceForSupportedClass(Class<?> supportedClass) {
		return Context.getService(RestService.class).getResourceBySupportedClass(supportedClass);
	}
	
	protected Representation getRepresentationForProperty(Representation rep, String property) {
		DelegatingResourceDescription repDescription = getRepresentationDescription(rep);
		if (repDescription != null) {
			DelegatingResourceDescription.Property propDesc = repDescription.getProperties().get(property);
			if (propDesc != null) {
				return propDesc.getRep();
			}
		}
		return null;
	}
}

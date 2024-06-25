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

import org.openmrs.annotation.Handler;
import org.openmrs.module.bedmanagement.BedLayout;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.representation.CustomRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * An implementation of Converter to be able to create a representation for a BedLayout
 */
@Handler(supports = BedLayout.class, order = 0)
public class BedLayoutResource extends BaseDelegatingResource<BedLayout> {

	@Override
	public BedLayout getByUniqueId(String uniqueId) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	protected void delete(BedLayout delegate, String reason, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public void purge(BedLayout delegate, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
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
		description.addProperty("bedType");
		description.addProperty("patients");
		description.addProperty("bedTagMaps");
		return description;
	}
	
	@Override
	public BedLayout newDelegate() {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public BedLayout save(BedLayout delegate) {
		throw new ResourceDoesNotSupportOperationException();
	}
}

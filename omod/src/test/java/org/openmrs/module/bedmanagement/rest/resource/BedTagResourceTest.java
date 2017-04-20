package org.openmrs.module.bedmanagement.rest.resource;


import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BedTagResourceTest extends MainResourceControllerTest {

    @Before
    public void init() throws Exception {
        executeDataSet("bedManagementDAOComponentTestDataset.xml");
    }

    @Override
    public String getURI() {
        return "bedTag";
    }

    @Override
    public String getUuid() {
        return null;
    }

    @Override
    public long getAllCount() {
        return 0;
    }

    @Test
    public void shouldGelAllNonVoidedBedTags() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.GET, getURI());
        SimpleObject object = deserialize(handle(request));

        List results = (List) object.get("results");
        LinkedHashMap brokenBedTg = (LinkedHashMap) results.get(0);
        LinkedHashMap isolationBedTag = (LinkedHashMap) results.get(2);

        assertEquals(4, results.size());

        assertEquals(1, brokenBedTg.get("id"));
        assertEquals("Broken", brokenBedTg.get("name"));
        assertEquals("73e846d6-ed5f-11e6-a3c9-0800274a5156", brokenBedTg.get("uuid"));

        assertEquals(3, isolationBedTag.get("id"));
        assertEquals("Isolation", isolationBedTag.get("name"));
        assertEquals("73e846d6-ed5f-33e6-a3c9-0800274a5156", isolationBedTag.get("uuid"));
    }
}

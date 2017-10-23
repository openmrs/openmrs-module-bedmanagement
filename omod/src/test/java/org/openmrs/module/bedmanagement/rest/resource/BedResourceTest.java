package org.openmrs.module.bedmanagement.rest.resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by sanish on 10/23/17.
 */
public class BedResourceTest extends MainResourceControllerTest {

    @Before
    public void init() throws Exception {
        executeDataSet("bedManagementDAOComponentTestDataset.xml");
    }

    @Override
    public String getURI() {
        return "bed";
    }

    @Override
    public String getUuid() {
        return "1";
    }

    @Override
    public long getAllCount() {
        return 0;
    }

    @Test
    public void shouldReturnAllBeds() throws Exception {
        MockHttpServletRequest request = request(RequestMethod.GET, getURI());
        SimpleObject object = deserialize(handle(request));
        List results = (ArrayList) object.get("results");
        System.out.println(results);
        HashMap bed1 = (LinkedHashMap) results.get(0);
        HashMap bed2 = (LinkedHashMap) results.get(10);

        Assert.assertEquals(16, results.size());
        Assert.assertEquals("304-a", bed1.get("bedNumber"));
        Assert.assertEquals(1, bed1.get("row"));
        Assert.assertEquals(1, bed1.get("column"));
        Assert.assertEquals("307-a", bed2.get("bedNumber"));
    }

}

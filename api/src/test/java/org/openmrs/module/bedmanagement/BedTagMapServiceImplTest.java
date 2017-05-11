package org.openmrs.module.bedmanagement;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.response.IllegalPropertyException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class BedTagMapServiceImplTest {
    BedTagMapServiceImpl bedTagMapService;

    @Mock
    BedTagMapDAO bedTagMapDAO;

    @Mock
    Context context;

    @Mock
    private User authenticatedUser;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        initMocks(this);
        PowerMockito.mockStatic(Context.class);
        when(Context.getAuthenticatedUser()).thenReturn(authenticatedUser);
        bedTagMapService = new BedTagMapServiceImpl();
        bedTagMapService.setDao(bedTagMapDAO);
    }

    @Test
    public void shouldGetBedTagMapByUuid() {
        String bedTagMapUuid = "bedTagMapUuid";
        BedTagMap bedTagMap = new BedTagMap();
        when(bedTagMapDAO.getBedTagMapByUuid(bedTagMapUuid)).thenReturn(bedTagMap);

        BedTagMap actualBedTagMap = bedTagMapService.getBedTagMapByUuid(bedTagMapUuid);

        verify(bedTagMapDAO, times(1)).getBedTagMapByUuid(bedTagMapUuid);
        assertEquals(bedTagMap, actualBedTagMap);
    }

    @Test
    public void shouldGetBedTagByUuid() {
        String bedTagUuid = "bedTagUuid";
        BedTag bedTag = new BedTag();
        when(bedTagMapDAO.getBedTagByUuid(bedTagUuid)).thenReturn(bedTag);

        BedTag actualBedTag = bedTagMapService.getBedTagByUuid(bedTagUuid);

        verify(bedTagMapDAO, times(1)).getBedTagByUuid(bedTagUuid);
        assertEquals(bedTag, actualBedTag);
    }


    @Test
    public void shouldThrowExceptionIfGivenBedAlreadyAssignedGivenBedTag() throws Exception {
        exception.expect(IllegalPropertyException.class);
        exception.expectMessage("Tag Already Present For Bed");

        Bed bed = new Bed();
        BedTag bedTag = new BedTag();
        BedTagMap bedTagMap = new BedTagMap();
        bedTagMap.setBed(bed);
        bedTagMap.setBedTag(bedTag);
        when(bedTagMapDAO.getBedTagMapWithBedAndTag(bed, bedTag)).thenReturn(bedTagMap);
        bedTagMapService.save(bedTagMap);
    }

    @Test
    public void shouldAssignGivenBedWithGivenBedTag() throws Exception {
        Bed bed = new Bed();
        BedTag bedTag = new BedTag();
        BedTagMap bedTagMap = new BedTagMap();
        bedTagMap.setBed(bed);
        bedTagMap.setBedTag(bedTag);
        when(bedTagMapDAO.getBedTagMapWithBedAndTag(bed, bedTag)).thenReturn(null);
        bedTagMapService.save(bedTagMap);
        verify(bedTagMapDAO, times(1)).saveOrUpdate(any(BedTagMap.class));
    }

    @Test
    public void shouldVoidGivenBedTagMap() throws Exception {
        String reason = "some reason";
        BedTagMap bedTagMap = new BedTagMap();
        bedTagMapService.delete(bedTagMap, reason);
        verify(bedTagMapDAO, times(1)).saveOrUpdate(any(BedTagMap.class));
    }
}
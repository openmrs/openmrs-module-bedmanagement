package org.openmrs.module.bedmanagement;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.bedmanagement.dao.BedTagMapDao;
import org.openmrs.module.bedmanagement.entity.Bed;
import org.openmrs.module.bedmanagement.entity.BedTag;
import org.openmrs.module.bedmanagement.entity.BedTagMap;
import org.openmrs.module.bedmanagement.service.impl.BedTagMapServiceImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BedTagMapServiceImplTest {
	
	BedTagMapServiceImpl bedTagMapService;
	
	@Mock
	BedTagMapDao bedTagMapDao;
	
	@Mock
	private User authenticatedUser;
	
	@BeforeEach
	public void setup() {
		bedTagMapService = new BedTagMapServiceImpl();
		bedTagMapService.setDao(bedTagMapDao);
	}
	
	@Test
	public void shouldGetBedTagMapByUuid() {
		String bedTagMapUuid = "bedTagMapUuid";
		BedTagMap bedTagMap = new BedTagMap();
		when(bedTagMapDao.getBedTagMapByUuid(bedTagMapUuid)).thenReturn(bedTagMap);
		
		BedTagMap actualBedTagMap = bedTagMapService.getBedTagMapByUuid(bedTagMapUuid);
		
		verify(bedTagMapDao, times(1)).getBedTagMapByUuid(bedTagMapUuid);
		assertEquals(bedTagMap, actualBedTagMap);
	}
	
	@Test
	public void shouldGetBedTagByUuid() {
		String bedTagUuid = "bedTagUuid";
		BedTag bedTag = new BedTag();
		when(bedTagMapDao.getBedTagByUuid(bedTagUuid)).thenReturn(bedTag);
		
		BedTag actualBedTag = bedTagMapService.getBedTagByUuid(bedTagUuid);
		
		verify(bedTagMapDao, times(1)).getBedTagByUuid(bedTagUuid);
		assertEquals(bedTag, actualBedTag);
	}
	
	@Test
	public void shouldThrowExceptionIfGivenBedAlreadyAssignedGivenBedTag() throws Exception {
		assertThrows("Tag Already Present For Bed", APIException.class, () -> {
			Bed bed = new Bed();
			BedTag bedTag = new BedTag();
			BedTagMap bedTagMap = new BedTagMap();
			bedTagMap.setBed(bed);
			bedTagMap.setBedTag(bedTag);
			when(bedTagMapDao.getBedTagMapWithBedAndTag(bed, bedTag)).thenReturn(bedTagMap);
			bedTagMapService.save(bedTagMap);
			
		});
	}
	
	@Test
	public void shouldAssignGivenBedWithGivenBedTag() throws Exception {
		Bed bed = new Bed();
		BedTag bedTag = new BedTag();
		BedTagMap bedTagMap = new BedTagMap();
		bedTagMap.setBed(bed);
		bedTagMap.setBedTag(bedTag);
		when(bedTagMapDao.getBedTagMapWithBedAndTag(bed, bedTag)).thenReturn(null);
		bedTagMapService.save(bedTagMap);
		verify(bedTagMapDao, times(1)).saveOrUpdate(any(BedTagMap.class));
	}
	
	@Test
	public void shouldVoidGivenBedTagMap() throws Exception {
		try (MockedStatic<Context> mockedContext = Mockito.mockStatic(Context.class)) {
			String reason = "some reason";
			BedTagMap bedTagMap = new BedTagMap();
			User user = mock(User.class);
			mockedContext.when(Context::getAuthenticatedUser).thenReturn(user);
			bedTagMapService.delete(bedTagMap, reason);
			verify(bedTagMapDao, times(1)).saveOrUpdate(any(BedTagMap.class));
		}
	}
}

package dao;


import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import testutils.TestUtils;

public class ResourceInfoRowTest {

	@Test
	public void test_equals()
	{
		Assert.assertEquals(TestUtils.getResourceInfoRow("class1", 0, 0, 0, null), TestUtils.getResourceInfoRow("class1", 0, 0, 0, null));
		Assert.assertNotEquals(TestUtils.getResourceInfoRow("class", 0, 0, 0, null), TestUtils.getResourceInfoRow("class1", 0, 0, 0, null));
		Assert.assertNotEquals(TestUtils.getResourceInfoRow("class1", 1, 0, 0, null), TestUtils.getResourceInfoRow("class1", 0, 0, 0, null));
		Assert.assertNotEquals(TestUtils.getResourceInfoRow("class1", 0, 0, 1, null), TestUtils.getResourceInfoRow("class1", 0, 0, 0, null));
		Assert.assertNotEquals(TestUtils.getResourceInfoRow("class1", 0, 0, 0, ImmutableMap.of("r1", 0)), TestUtils.getResourceInfoRow("class1", 0, 0, 0, null));
		Assert.assertEquals(TestUtils.getResourceInfoRow("class1", 0, 0, 0, ImmutableMap.of("r1", 0)), TestUtils.getResourceInfoRow("class1", 0, 0, 0, ImmutableMap.of("r1", 0)));
	}
}

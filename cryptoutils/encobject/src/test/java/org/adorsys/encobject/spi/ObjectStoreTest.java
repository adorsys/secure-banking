package org.adorsys.encobject.spi;

import java.io.IOException;

import org.adorsys.encobject.utils.TestFsBlobStoreFactory;
import org.apache.commons.io.IOUtils;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.Assert;

public class ObjectStoreTest {
	private static BlobStoreContext storeContext;
	private static String container = ObjectStoreTest.class.getSimpleName();
	
	@BeforeClass
	public static void beforeClass(){
		storeContext = TestFsBlobStoreFactory.getTestBlobStoreContext();
		Assume.assumeNotNull(storeContext);
	}
	
	@AfterClass
	public static void afterClass(){
		storeContext.getBlobStore().deleteContainer(container);
		storeContext.close();
	} 

	@Test
	public void test() throws IOException {
		BlobStore blobStore = storeContext.getBlobStore();
		Assert.assertNotNull(blobStore);
		
		blobStore.createContainerInLocation(null, container);

		
		// add blob
		Blob blob = blobStore.blobBuilder("test")
		.payload("test data")
		.contentLength("test data".length())
		.build();

		blobStore.putBlob(container, blob);
		
		Assert.assertTrue(TestFsBlobStoreFactory.existsOnFs(container, "test"));

		// retrieve blob
		Blob blobRetrieved = blobStore.getBlob(container, "test");
		Assert.assertEquals("test data", IOUtils.toString(blobRetrieved.getPayload().openStream(), "UTF-8") );
	}

}

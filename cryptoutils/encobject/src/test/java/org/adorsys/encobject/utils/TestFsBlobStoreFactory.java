package org.adorsys.encobject.utils;

import java.io.File;
import java.util.Properties;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.filesystem.reference.FilesystemConstants;

public class TestFsBlobStoreFactory {
	
	private static final String STORE_BASEDIR = "./target/filesystemstorage";

	public static BlobStoreContext getTestBlobStoreContext(){
		// setup where the provider must store the files
		Properties properties = new Properties();
		properties.setProperty(FilesystemConstants.PROPERTY_BASEDIR, STORE_BASEDIR);

		// get a context with filesystem that offers the portable BlobStore api
		return ContextBuilder.newBuilder("filesystem")
		                 .overrides(properties)
		                 .buildView(BlobStoreContext.class);
	}
	
	public static boolean existsOnFs(String container, String name){
		File file = new File(STORE_BASEDIR + "/" + container + "/" + name );
		return file.exists();
	}
}

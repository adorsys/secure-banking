package org.adorsys.encobject.utils;

import java.io.File;
import java.util.Properties;

import org.adorsys.encobject.service.BlobStoreContextFactory;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.filesystem.reference.FilesystemConstants;

public class TestFsBlobStoreFactory implements BlobStoreContextFactory {
	
	private static final String STORE_BASEDIR = "./target/filesystemstorage";
	Properties properties = new Properties();
	
	public TestFsBlobStoreFactory(){
		properties.setProperty(FilesystemConstants.PROPERTY_BASEDIR, STORE_BASEDIR);
	}
	
	public static boolean existsOnFs(String container, String name){
		File file = new File(STORE_BASEDIR + "/" + container + "/" + name );
		return file.exists();
	}

	@Override
	public BlobStoreContext alocate() {
		 return ContextBuilder.newBuilder("filesystem")
         .overrides(properties)
         .buildView(BlobStoreContext.class);
	}

	@Override
	public void dispose(BlobStoreContext blobStoreContext) {
		blobStoreContext.close();
	}
}

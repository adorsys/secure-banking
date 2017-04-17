package org.adorsys.encobject.service;

import java.io.IOException;

import org.adorsys.encobject.domain.ObjectHandle;
import org.apache.commons.io.IOUtils;
import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.domain.Blob;

/**
 * Provides connection to the blob store. Particularly implements routine for opening
 * anc closing connection to the blob store.
 * 
 * @author fpo
 *
 */
public class BlobStoreConnection {

	private BlobStoreContextFactory blobStoreContextFactory;

	public BlobStoreConnection(BlobStoreContextFactory blobStoreContextFactory) {
		this.blobStoreContextFactory = blobStoreContextFactory;
	}
	
	public void createContainer(String container) throws ContainerExistsException{
		BlobStoreContext blobStoreContext = blobStoreContextFactory.alocate();
		try {
			BlobStore blobStore = blobStoreContext.getBlobStore();
			if(blobStore.containerExists(container)){
				throw new ContainerExistsException(container);
			}
			blobStoreContext.getBlobStore().createContainerInLocation(null, container);
		} finally {
			blobStoreContextFactory.dispose(blobStoreContext);			
		}
	}

	public boolean containerExists(String container) {
		BlobStoreContext blobStoreContext = blobStoreContextFactory.alocate();
		try {
			BlobStore blobStore = blobStoreContext.getBlobStore();
			return blobStore.containerExists(container);
		} finally {
			blobStoreContextFactory.dispose(blobStoreContext);			
		}
	}

	public void deleteContainer(String container) throws UnknownContainerException {
		BlobStoreContext blobStoreContext = blobStoreContextFactory.alocate();
		try {
			BlobStore blobStore = blobStoreContext.getBlobStore();
			if(!blobStore.containerExists(container)){
				throw new UnknownContainerException(container);
			}
			blobStoreContext.getBlobStore().deleteContainer(container);
		} finally {
			blobStoreContextFactory.dispose(blobStoreContext);			
		}
	}
	
	public void putBlob(ObjectHandle handle, byte[] bytes) throws UnknownContainerException {
		BlobStoreContext blobStoreContext = blobStoreContextFactory.alocate();
		try {
			BlobStore blobStore = blobStoreContext.getBlobStore();

			// add blob
			Blob blob = blobStore.blobBuilder(handle.getName())
						.payload(bytes)
						.build();
			blobStore.putBlob(handle.getContainer(), blob);
		} catch (ContainerNotFoundException ex){
			throw new UnknownContainerException(handle.getContainer());
		} finally {
			blobStoreContextFactory.dispose(blobStoreContext);			
		}
	}
	
	public byte[] getBlob(ObjectHandle handle) throws UnknownContainerException, ObjectNotFoundException{
		BlobStoreContext blobStoreContext = blobStoreContextFactory.alocate();
		try {
			BlobStore blobStore = blobStoreContext.getBlobStore();
			Blob blob;
			try {
				blob = blobStore.getBlob(handle.getContainer(), handle.getName());
				if(blob==null)  throw new ObjectNotFoundException(handle.getContainer() + "/" + handle.getName());
			} catch (ContainerNotFoundException e){
				throw new UnknownContainerException(handle.getContainer());			
			}
			
			try {
				return IOUtils.toByteArray(blob.getPayload().openStream());
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}

		} finally {
			blobStoreContextFactory.dispose(blobStoreContext);
		}
	}
}

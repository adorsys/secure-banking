package org.adorsys.jtstamp.service;

import java.security.KeyStore;

import org.adorsys.jjwk.keystore.JwkExport;
import org.adorsys.jtstamp.exception.TsMissingFieldException;
import org.adorsys.jtstamp.exception.TsSignatureException;
import org.adorsys.jtstamp.model.TsData;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nimbusds.jose.jwk.JWKSet;

public class TsServiceTest {
	private static TsService tsService;
	
	@BeforeClass
	public static void beforeClass(){
		KstUtils.turnOffEncPolicy();
		KeyStore testKeystore = KstUtils.testKeystore();
		Assume.assumeNotNull(testKeystore);
		JWKSet serverKeys = JwkExport.exportKeys(testKeystore, KstUtils.callbackHandlerBuilder().build());
		Assume.assumeNotNull(serverKeys);
		tsService = new TsService(serverKeys);
	}

	@Test
	public void testSimpleTs() {
		TsData data = new TsData();
		data.setHalg("RSA256");
		data.setHval("sfdasdfasdfasd");
		data.setInclIss(true);
		data.setInclKid(true);
		data.setSub("Francis");
		try {
			tsService.stamp(data, "Test Service");
		} catch (TsMissingFieldException | TsSignatureException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testMissingHAlg() {
		TsData data = new TsData();
//		data.setHalg("RSA256");
		data.setHval("sfdasdfasdfasd");
		data.setInclIss(true);
		data.setInclKid(true);
		data.setSub("Francis");
		try {
			tsService.stamp(data, "Test Service");
		} catch (TsMissingFieldException e) {
			Assert.assertEquals(TsService.FIELD_HALG,e.getMessage());
		} catch (TsSignatureException e) {
			Assert.fail("TsMissingFieldException expected");
		}
	}

	@Test
	public void testMissingHVal() {
		TsData data = new TsData();
		data.setHalg("RSA256");
//		data.setHval("sfdasdfasdfasd");
		data.setInclIss(true);
		data.setInclKid(true);
		data.setSub("Francis");
		try {
			tsService.stamp(data, "Test Service");
		} catch (TsMissingFieldException e) {
			Assert.assertEquals(TsService.FIELD_HVAL,e.getMessage());
		} catch (TsSignatureException e) {
			Assert.fail("TsMissingFieldException expected");
		}
	}
}

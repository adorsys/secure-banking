package org.adorsys.jtstamp.service;

import java.util.Date;
import java.util.List;

import org.adorsys.jjwk.keystore.JwkExport;
import org.adorsys.jjwk.selector.JWSSignerAndAlgorithm;
import org.adorsys.jjwk.selector.JWSSignerAndAlgorithmBuilder;
import org.adorsys.jtstamp.exception.TsMissingFieldException;
import org.adorsys.jtstamp.exception.TsSignatureException;
import org.adorsys.jtstamp.model.TsData;
import org.apache.commons.lang3.StringUtils;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;
import com.nimbusds.jwt.SignedJWT;

/**
 * Generate a timestamp and sign it toghether with the hash provided by the caller.
 *
 * @author fpo
 */
public class TsService {

	public static final String FIELD_OID = "oid";
	public static final String FIELD_HALG = "halg";
	public static final String FIELD_HVAL = "hval";
	
	public static final String JOSE_OBJECT_TYPE_STAMP = "STAMP";

	private final JWKSet serverKeys;

	public TsService(JWKSet serverKeys) {
		super();
		this.serverKeys = serverKeys;
	}

	/**
	 * Generates a timestap jwt for the caller, using one of the keys of the server.
	 * 
	 * @param data : data sent by the caller for time stamping.
	 * @param iss : the issuer. Set by the server using this routine. Might be retrieved from the 
	 *   URIInfo object in a REST application.
	 * @return
	 * @throws TsMissingFieldException 
	 * @throws TsSignatureException 
	 */
    public String stamp(TsData data, String iss) throws TsMissingFieldException, TsSignatureException {
        Builder builder = new JWTClaimsSet.Builder();

        if (StringUtils.isNotBlank(data.getSub()))builder.subject(data.getSub());
        if (StringUtils.isNotBlank(data.getOid())) builder.claim(FIELD_OID, data.getOid());
        
        if (StringUtils.isBlank(data.getHalg())) throw new TsMissingFieldException(FIELD_HALG);
        builder.claim(FIELD_HALG, data.getHalg());

        if (StringUtils.isBlank(data.getHval())) throw new TsMissingFieldException(FIELD_HVAL);
        builder.claim(FIELD_HVAL, data.getHval());
        builder.issueTime(new Date());

        // Set the issuer.
        if (data.isInclIss()) {
            builder.issuer(iss);
        }

        JWTClaimsSet claimsSet = builder.build();
        
        JOSEObjectType typ = new JOSEObjectType(JOSE_OBJECT_TYPE_STAMP);
        List<JWK> keypairs = JwkExport.selectKeypairs(serverKeys);
        JWK jwk = JwkExport.randomKey(keypairs);
        JWSSignerAndAlgorithm signerAndAlgorithm = JWSSignerAndAlgorithmBuilder.build(jwk);
        JWSHeader jwsHeader = null;
        if(data.isInclKid()){
        	jwsHeader = new JWSHeader(signerAndAlgorithm.getJwsAlgorithm(), typ , null, null, 
				null, null, null, null, null, null, jwk.getKeyID(), null, null);
        } else {
        	jwsHeader = new JWSHeader(signerAndAlgorithm.getJwsAlgorithm(), typ , null, null, 
				null, null, null, null, null, null, null, null, null);
        }
        SignedJWT signedJWT = new SignedJWT(jwsHeader,claimsSet);
        try {
        	signedJWT.sign(signerAndAlgorithm.getSigner());
        } catch (JOSEException e) {
            throw new TsSignatureException(e);
        }
        return signedJWT.serialize();
    }
}

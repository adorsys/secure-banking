package de.adorsys.cse.jwt;

import java.util.Optional;

public interface JWT {

    String encode();

    Optional<String> getClaim(String claimName);
}

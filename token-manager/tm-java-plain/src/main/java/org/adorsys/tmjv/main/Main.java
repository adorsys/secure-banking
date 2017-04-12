package org.adorsys.tmjv.main;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.jaxrs.JAXRSArchive;
import org.wildfly.swarm.swagger.SwaggerArchive;

public class Main {

    public static void main(String[] args) throws Exception {
        // Instantiate the container
        Swarm swarm = new Swarm();

        final JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class);

        deployment.addPackages(true, "org.adorsys.tmjv");
        deployment.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        deployment.addAllDependencies();

        // Enable the swagger bits

        final SwaggerArchive archive = deployment.as(SwaggerArchive.class);

        // Tell swagger where our resources are
        archive.setResourcePackages("org.adorsys.tmjv.token","org.adorsys.tmjv.ts");

        archive.setTitle("Token Manager Application");

        swarm.start();

        swarm.deploy(deployment);
    }

}

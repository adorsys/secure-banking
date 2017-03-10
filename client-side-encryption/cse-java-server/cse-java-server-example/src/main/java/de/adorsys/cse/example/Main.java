package de.adorsys.cse.example;

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

        deployment.addPackages(true, "de.adorsys.cse");
        deployment.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        deployment.addAllDependencies();

        // Enable the swagger bits
        final SwaggerArchive archive = deployment.as(SwaggerArchive.class);

        // Tell swagger where our resources are
        archive.setResourcePackages("de.adorsys.cse.example");
        archive.setTitle("Client-side encryption demo");

        swarm.start();
        swarm.deploy(deployment);
    }

}

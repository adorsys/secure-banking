package org.adorsys.psd2.xs2a.main;

import org.adorsys.psd2.iso20022.camt052.BankToCustomerAccountReportV06;
import org.adorsys.psd2.iso20022.camt053.BankToCustomerStatementV06;
import org.adorsys.psd2.xs2a.domain.BankAccess;
import org.adorsys.psd2.xs2a.resource.BankingResource;
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

        deployment.addPackages(true, "org.adorsys.psd2");
        deployment.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        deployment.addAllDependencies();

        // Enable the swagger bits

        final SwaggerArchive archive = deployment.as(SwaggerArchive.class);

        // Tell swagger where our resources are
        archive.setResourcePackages(BankingResource.class.getPackage().getName(), 
        		BankAccess.class.getPackage().getName(),
        		BankToCustomerAccountReportV06.class.getPackage().getName(),
        		BankToCustomerStatementV06.class.getPackage().getName());
        archive.setTitle("Access to Account Demo");

        swarm.start();

        swarm.deploy(deployment);
    }

}

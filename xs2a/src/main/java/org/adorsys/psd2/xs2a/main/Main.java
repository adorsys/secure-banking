package org.adorsys.psd2.xs2a.main;

import org.adorsys.psd2.hbci.resource.HbciResource;
import org.adorsys.psd2.pop.PoPResource;
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
				HbciResource.class.getPackage().getName(), PoPResource.class.getPackage().getName());
		archive.setTitle("PSD2 Access to Account Demo");
		archive.setContact("adorsys GmbH & Co. KG");
//		String description = IOUtils.toString(Main.class.getResource("APIDescription.txt"), "UTF-8");
//		archive.setDescription(description);
		archive.setDescription(
				"This is our attempt to implements the RTS recommendations of EBA’s revised Payment Services Directive (EU) 2015/2366 (PSD2). Beside providing an Interface to account servicing payment service providers (ASPSPs) for the supply of payment initiation service providers (PISPs), account information service providers (AISPs), payers, payees and other payment service providers (PSPs) with payment service users (PSU) payment data. We hereby suggest robust way of implementing the required common and secure open standards of communication (CSC) by making sure customer data exchanged between PSPs are well protected alongside the communication path. Using the POP-Key, a client application can encrypt all data sent to a server and can also specify.");
		archive.setLicense("Apache License, Version 2.0");
		archive.setLicenseUrl("http://www.apache.org/licenses/LICENSE-2.0");
		archive.setPrettyPrint(true);
		archive.setVersion("V1");

		swarm.start();

		swarm.deploy(deployment);
	}

}

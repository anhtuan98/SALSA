package at.ac.tuwien.dsg.cloud.salsa.cloudconnector.multiclouds;

public enum SalsaCloudProviders {

	DSG_OPENSTACK("dsg@openstack"),	
	LAL_STRATUSLAB("lal@stratuslab"),
	CELAR_FLEXIANT("celar@flexiant"),	
	LOCALHOST("localhost"),
	LOCAL_DOCKERHOST("local@dockerhost");

	private String providerId;

	private SalsaCloudProviders(String providerId) {
		this.providerId = providerId;
	}

	public String getCloudProviderString() {
		return providerId;
	}

	public static SalsaCloudProviders fromString(String text) {
		if (text != null) {
			for (SalsaCloudProviders b : SalsaCloudProviders.values()) {
				if (text.equalsIgnoreCase(b.getCloudProviderString())) {
					return b;
				}
			}
		}
		return null;
	}

}

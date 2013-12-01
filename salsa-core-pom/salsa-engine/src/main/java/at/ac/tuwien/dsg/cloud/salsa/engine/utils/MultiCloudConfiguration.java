package at.ac.tuwien.dsg.cloud.salsa.engine.utils;

public class MultiCloudConfiguration {
	public static String getSshKeyName(String CloudProvider){
		switch (CloudProvider){
		case "OPENSTACK": 
			return "Hungld";
		case "STRATUSLAB":
			return "ssh-rsa AAAAB3NzaC1yc2EAAAABIwAAAQEAwL428f/Jw9f3AQh3NKPrWDbrOvEdb4EpSgTgEwEtupgysYM6YosHWdB1KopPxTHt8rlD3Q7NWBsXGUBqskucDjvK0aF8eLLM2ZnwBkJqCSLGxUcLtGS+kZ/d1kH8M6lrRzzK4CwA7Mt26hKcyYmqGiLvM9X4hdzPDyeCbar+skGeG+n81tI3UHDcD9I4JOkbu9eScVP8fTB2Th9qKH28f2eSkNy/1fecUYphwtVEqy5AFAwBg16An9VEAgLjUTzgum5Q+56iMy0t7NjozWBkPgGC7N7NUaupDRTImKbx10VHt6L3ze0fBUpUR9+6wStfduzNkKO1QFGOy/GEVw8PsQ==";
		default:
			return "";		
		}
	}
}

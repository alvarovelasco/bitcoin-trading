package org.sonar.challenge.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GSonBuilder {

	public final static Gson buildStandardGson() {
		return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").create();
	}
	
}

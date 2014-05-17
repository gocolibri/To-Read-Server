package com.colibri.toread.api;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.restlet.resource.ServerResource;

import com.colibri.toread.entities.TRLogger;
import com.colibri.toread.persistence.TRLoggerDAO;

public class LoggableResource extends ServerResource{
	Logger logger = Logger.getLogger(LoggableResource.class);
	
	public void logRequest(String data) throws JSONException, IOException {
		new TRLoggerDAO().save(new TRLogger(data));
	}
}

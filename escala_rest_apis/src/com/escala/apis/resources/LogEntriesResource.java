package com.escala.apis.resources;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import com.escala.apis.dao.DAOFactory;
import com.escala.apis.dao.ILogEntryDAO;
import com.escala.xfer.ILogEntries;
import com.escala.xfer.LogEntries;
import com.escala.xfer.LogEntry;

@Path("/logentries")
public class LogEntriesResource {
	// Allows to insert contextual objects into the class,
		// e.g. ServletContext, Request, Response, UriInfo
		@Context
		UriInfo uriInfo;
		@Context
		Request request;
		
		@GET
		@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
		public LogEntries getInstances(@QueryParam("start") @DefaultValue("0") int start,
				@QueryParam("end") @DefaultValue("-1") int end,
				@QueryParam("clearModified") @DefaultValue("false") boolean clearModified) {
			LogEntries result = null;
			ILogEntryDAO logEntryDAO = DAOFactory.getInstance().getLogEntryDAO();
			if (logEntryDAO != null) {
				result = new LogEntries(logEntryDAO.getEntries(start, end));
			}
			if (clearModified) {
				logEntryDAO.getModified(clearModified);
			}
			return result;
		}
		
		@GET
		@Path("modified")
		@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
		public LogEntries getModifiedInstances(@QueryParam("start") @DefaultValue("0") int start,
				@QueryParam("end") @DefaultValue("-1") int end,
				@QueryParam("clear") @DefaultValue("false") boolean clear) {
			LogEntries result = null;
			ILogEntryDAO logEntryDAO = DAOFactory.getInstance().getLogEntryDAO();
			if (logEntryDAO != null) {
				result = new LogEntries(logEntryDAO.getModified(start, end, clear));
			}
			
			return result;
		}
				
		@GET
		@Path("instance/{index}")
		@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
		public LogEntry getInstance(@PathParam("index") @DefaultValue("0") int index) {
			System.out.println("LogEntriesResource::getInstance");
			ILogEntryDAO logEntryDAO = DAOFactory.getInstance().getLogEntryDAO();			
			return (LogEntry) logEntryDAO.getLogEntry(index);
		}
		
		@GET
		@Path("count")
		@Produces(MediaType.TEXT_PLAIN)
		public String getCount() {
			int count = 0;
			System.out.println("LogEntriesResource::getCount");
			ILogEntryDAO logEntryDAO = DAOFactory.getInstance().getLogEntryDAO();
			count = logEntryDAO.getCount();
			return String.valueOf(count);
		}
		
		@POST
		@Produces(MediaType.TEXT_HTML)
		@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
		public void newLogEntry(@FormParam("logData") String logData,
				@Context HttpServletResponse servletResponse) throws IOException {

			System.out.println("LogEntriesResource::newLogEntry");
			ILogEntryDAO logEntryDAO = DAOFactory.getInstance().getLogEntryDAO();
			boolean result = logEntryDAO.addLogEntry(logData);
			if (result) {
				System.out.println("Added new log: " + logData);
			} else {
				System.out.println("FAILED to add new log:  " + logData);
			}

			servletResponse.sendRedirect("../apis/logentries");
		}
		
//		@PUT
//		@Consumes(MediaType.APPLICATION_XML)
//		public Response newLogEntry(JAXBElement<LogEntry> LogEntryParam) {
//			System.out.println("LogEntriesResource::newLogEntry[PUT]");
//			ILogEntryDAO logEntryDAO = DAOFactory.getInstance().getLogEntryDAO();
//			LogEntry logEntry = LogEntryParam.getValue();
//			Response result = null;
//			
//			if (logEntryDAO.addLogEntry(logEntry)) {
//				System.out.println("Added new log: " + logEntry.getLogData());
//				result = Response.created(uriInfo.getAbsolutePath()).build();
//				
//			} else {
//				System.out.println("FAILED to add new log: " + logEntry.getLogData());
//				result = Response.notModified().build();
//			}
//			return result;
//		}
		
		@PUT
		@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
		public Response newLogEntries(JAXBElement<LogEntries> LogEntryParam) {
			System.out.println("LogEntriesResource::newLogEntries[PUT]");
			ILogEntryDAO logEntryDAO = DAOFactory.getInstance().getLogEntryDAO();
			ILogEntries logEntries = LogEntryParam.getValue();
			Response result = null;
			
			if (logEntryDAO.addLogEntries(logEntries)) {
				System.out.println("Added new logs");
				result = Response.created(uriInfo.getAbsolutePath()).build();
				
			} else {
				System.out.println("FAILED to add new logs");
				result = Response.notModified().build();
			}
			return result;
		}
		
		
		@DELETE
		public Response deleteLogEntry(@QueryParam("start") @DefaultValue("0") int start,
				@QueryParam("end") @DefaultValue("-1") int end) {
			System.out.println("LogEntriesResource::deleteLogEntry");
			Response result = null;
			ILogEntryDAO logEntryDAO = DAOFactory.getInstance().getLogEntryDAO();
					
			if (logEntryDAO.removeLogRange(start, end)) {
				result = Response.ok().build();
			} else {
				result = Response.notModified().build();
			}
			
			return result;
		}
}

package com.escala.apis.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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

import com.escala.apis.dao.DAOFactory;
import com.escala.apis.dao.ILogConfigDAO;
import com.escala.xfer.ILogConfig;
import com.escala.xfer.LogConfig;
import com.escala.xfer.LogConfigs;

@Path("/logconfigs")
public class LogConfigsResource {
	// Allows to insert contextual objects into the class,
	// e.g. ServletContext, Request, Response, UriInfo
	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public LogConfigs getAllInstances() {
		LogConfigs result = null;
		ILogConfigDAO logConfigDAO = DAOFactory.getInstance().getLogConfigDAO();
		if (logConfigDAO != null) {
			result = new LogConfigs(logConfigDAO.getAll());
		}
		return result;
	}

	@GET
	@Path("modified")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public LogConfigs getModifiedInstances(@QueryParam("clear") boolean clear) {
		LogConfigs result = null;
		ILogConfigDAO logConfigDAO = DAOFactory.getInstance().getLogConfigDAO();
		if (logConfigDAO != null) {
			result = new LogConfigs(logConfigDAO.getModified(clear));
		}

		return result;
	}

	@GET
	@Path("{classInfo}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public LogConfigs getClassInstances(@PathParam("classInfo") String classInfo) {
		LogConfigs result = null;
		boolean validClassInfo = classInfo != null && classInfo != "";
		ILogConfigDAO logConfigDAO = DAOFactory.getInstance().getLogConfigDAO();

		if (validClassInfo) {
			result = new LogConfigs(logConfigDAO.getAllForClass(classInfo));
		}

		return result;
	}

	@GET
	@Path("{classInfo}/{methodInfo}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public LogConfigs getClassMethodInstance(
			@PathParam("classInfo") String classInfo,
			@PathParam("methodInfo") String methodInfo) {
		LogConfigs result = null;
		List<LogConfig> entries = null;
		boolean validClassInfo = classInfo != null && classInfo != "";
		boolean validMethodInfo = methodInfo != null && methodInfo != "";
		ILogConfigDAO logConfigDAO = DAOFactory.getInstance().getLogConfigDAO();

		if (validClassInfo && validMethodInfo) {
			entries = new ArrayList<LogConfig>();
			ILogConfig entry = logConfigDAO.getLogConfig(classInfo, methodInfo);
			if (entry != null) {
				// only add to the list if the element exists
				entries.add((LogConfig) entry);
			}
			result = new LogConfigs(entries);
		}

		return result;
	}

	@GET
	@Path("instance")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public LogConfig getInstance(@QueryParam("class") String classInfo,
			@QueryParam("method") String methodInfo) {
		LogConfig result = null;
		System.out.println("LogEntriesResource::getInstance");
		boolean validClassInfo = classInfo != null && classInfo != "";
		boolean validMethodInfo = methodInfo != null && methodInfo != "";
		ILogConfigDAO logConfigDAO = DAOFactory.getInstance().getLogConfigDAO();

		if (validClassInfo && validMethodInfo) {
			result = (LogConfig) logConfigDAO.getLogConfig(classInfo,
					methodInfo);
			if (result != null) {
				System.out.println("Found an entry for class " + classInfo
						+ " and method " + methodInfo);
			} else {
				System.out.println("Did not find an entry for class "
						+ classInfo + " and method " + methodInfo);
			}
		}

		return result;
	}

	@GET
	@Path("count")
	@Produces(MediaType.TEXT_PLAIN)
	public String getCount() {
		int count = 0;
		System.out.println("LogConfigsResource::getCount");
		ILogConfigDAO logConfigDAO = DAOFactory.getInstance().getLogConfigDAO();
		count = logConfigDAO.getCount();
		return String.valueOf(count);
	}

	@POST
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void newLogConfig(@FormParam("classInfo") String classInfo,
			@FormParam("methodInfo") String methodInfo,
			@FormParam("handlerClass") String handlerClass,
			@Context HttpServletResponse servletResponse) throws IOException {

		System.out.println("LogEntriesResource::newLogConfig[POST]");
		System.out.println("Arguments: " + handlerClass);
		ILogConfigDAO logConfigDAO = DAOFactory.getInstance().getLogConfigDAO();
		boolean result = logConfigDAO.addLogConfig(classInfo, methodInfo,
				handlerClass);
		if (result) {
			System.out.println("Added new entry for class " + classInfo
					+ " and method " + methodInfo + " with a classHandler.");
		} else {
			System.out.println("FAILED to add new entry for class " + classInfo
					+ " and method " + methodInfo + " with a classHandler.");
		}

		servletResponse.sendRedirect("../apis/logconfigs");
	}

	@PUT
	@Path("{classInfo}/{methodInfo}")
	public Response newLogConfig(@PathParam("classInfo") String classInfo,
			@PathParam("methodInfo") String methodInfo) {
		System.out.println("LogConfigsResource::newLogConfig[PUT]");
		ILogConfigDAO logConfigDAO = DAOFactory.getInstance().getLogConfigDAO();
		ILogConfig logConfig = new LogConfig(classInfo, methodInfo);
		Response result = null;

		if (logConfigDAO.addLogConfig(logConfig)) {
			System.out.println("Added new entry for class "
					+ logConfig.getClassInfo() + " and method "
					+ logConfig.getMethodInfo() + " and a classHandler.");
			result = Response.created(uriInfo.getAbsolutePath()).build();

		} else {
			System.out.println("FAILED to add new entry for class "
					+ logConfig.getClassInfo() + " and method "
					+ logConfig.getMethodInfo() + " and a classHandler.");
			result = Response.notModified().build();
		}
		return result;
	}

	// @PUT
	// @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	// public Response newLogConfigs(JAXBElement<LogConfig> LogConfigParam) {
	// System.out.println("LogConfigsResource::newLogConfigs[PUT]");
	// ILogConfigDAO logConfigDAO = DAOFactory.getInstance().getLogConfigDAO();
	// ILogConfig logConfig = LogConfigParam.getValue();
	// Response result = null;
	//
	// if (logConfigDAO.addLogConfig(logConfig)) {
	// System.out.println("Added new log config");
	// result = Response.created(uriInfo.getAbsolutePath()).build();
	//
	// } else {
	// System.out.println("FAILED to add new confif");
	// result = Response.notModified().build();
	// }
	// return result;
	// }

	@DELETE
	@Path("{classInfo}/{methodInfo}")
	public Response deleteLogConfig(@PathParam("classInfo") String classInfo,
			@PathParam("methodInfo") String methodInfo) {
		System.out.println("LogConfigsResource::deleteLogConfig");
		Response result = null;
		ILogConfigDAO logConfigDAO = DAOFactory.getInstance().getLogConfigDAO();

		if (logConfigDAO.removeLogConfig(classInfo, methodInfo)) {
			result = Response.ok().build();
		} else {
			result = Response.notModified().build();
		}

		return result;
	}
}

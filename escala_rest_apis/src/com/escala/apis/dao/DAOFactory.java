package com.escala.apis.dao;

public class DAOFactory implements IDAOFactory {

	private static final IDAOFactory instance = new DAOFactory();
	private ILogConfigDAO logConfigDAO = null;
	private ILogEntryDAO logEntryDAO = null;
	
	protected DAOFactory () {
		logConfigDAO = new LogConfigDAO();
		logEntryDAO = new LogEntryDAO();
	}		
	
	
	@Override
	public ILogConfigDAO getLogConfigDAO() {
		return logConfigDAO;
	}

	public static IDAOFactory getInstance() {
		return instance;
	}


	@Override
	public ILogEntryDAO getLogEntryDAO() {
		return logEntryDAO;
	}
}

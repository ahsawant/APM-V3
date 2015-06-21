package com.escala.apis.dao;

public interface IDAOFactory {
	ILogConfigDAO getLogConfigDAO();
	ILogEntryDAO getLogEntryDAO();
}

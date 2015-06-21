App = Ember.Application.create({
	LOG_TRANSITIONS: true
});

App.ApplicationAdapter = DS.FixtureAdapter.extend();

var baseLogConfigUrl = '../escala/apis/logconfigs';
var baseLogsUrl = '../escala/apis/logentries?clearModified=true';
var baseLogsModifiedUrl = '../escala/apis/logentries/modified?clear=true';

//var baseLogsUrl = 'http://192.168.1.76:8080/escala/apis/logentries';
//var baseLogsModifiedUrl = 'http://192.168.1.76:8080/escala/apis/logentries/modified?clear=true';

App.ConfigController = Ember.ObjectController.extend({
		
	actions : {
		newFile : function() {
			// Retrieve the value from the hidden file selector
			var fileName = $("#fileSelector").val();

			// See if there is valid content
			if (!fileName.trim()) {
				return;
			}

			// Call the upload function for the file and pass the context
			// for the ajax call back
			uploadFile(this);

			// Clear the "New File" text field
			$("#fileSelector").val("");
		},
		
		clicked : function() {
			alert('Clicked ' + this.get('model').get('methodName'));
		}, 
			
		selected: function(state) {
			var myModel = this.get('model');
			if (state === undefined) { return myModel.get('isSelected'); }
			myModel.set('isSelected', state);
			return myModel.get('isSelected');
		}, 
		
		instrument : function(newState, className, method) {
	
			// If it is a request to instrument the method
			if (method.isInstrumented) {
				addMethodInstrumentation(className, method);				
			} else {
				deleteMethodIntrumentation(className, method);
			}
		}

	}
});


function addMethodInstrumentation (className, method) {
	
	// Let's update the server with the new method instrumentation
	var putUrl = baseLogConfigUrl + "/" + className + "/" + method.methodName;
	
	// Issue the ajax request to the server
	$.ajax({
		  url: putUrl,
		  type: "PUT",
		  data: "",
		  processData: false,  // tell jQuery not to process the data
		  contentType: false,  // tell jQuery not to set contentType
		  context: method,
		  success: function (data) {	
				// Nothing to do... Isn't this great?
		  },		  
		  error: function(XMLHttpRequest, textStatus, errorThrown) {
			  Ember.set(this, 'isInstrumented', false);
			  alert("Failed to configure instrumentation settings!");  
		  }    
		});
}

function deleteMethodIntrumentation (className, method) {
	var deleteUrl = baseLogConfigUrl + "/" + className + "/" + method.methodName;
	// Let's issue the ajax request to update the server
	$.ajax({
		  url: deleteUrl,
		  type: "DELETE",
		  data: "",
		  processData: false,  // tell jQuery not to process the data
		  contentType: false,  // tell jQuery not to set contentType
		  context: method, 
		  success: function (data) {	
			// Nothing to do... Isn't this great?
		  },		  
		  error: function(XMLHttpRequest, textStatus, errorThrown) {
			  Ember.set(this, 'isInstrumented', true);
			  alert("Failed to remove instrumentation settings!");  
		  } 
		});
}
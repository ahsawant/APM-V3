App.ConfigsController = Ember.ArrayController.extend({
		
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

		openFile : function() {
			// Simply open the invisible file selector
			$("#fileSelector").click();			
		},
		
		removeFile: function(file) {
			// First fetch the model
			var model = this.get('model');
			model.removeObject(file);
			this.send('clearSelections');
			this.transitionToRoute('configs');
		},
		
		clearSelections : function() {
			// Clear the selection for all the other selected files
			var selected = this.filterBy('isSelected', true);
			selected.setEach('isSelected', false);
		}
	}
});

function uploadFile(controllerContext) {

	// Create a new FormData object.
	var fd = new FormData();
	var selected_file = $('#fileSelector').get(0).files[0];
	
	// Add the file to the request.
	fd.append('file', selected_file);
	
	var uploadUrl = '../escala-util/FileParser';
	
	$.ajax({
	  url: uploadUrl,
	  type: "POST",
	  data: fd,
	  processData: false,  // tell jQuery not to process the data
	  contentType: false,  // tell jQuery not to set contentType
	  dataType: 'json',
	  context: controllerContext
	}) .done (function(data) {	
		// Since we provided the controllerContext to the ajax
		// this will point to the content controller
		processUploadResponse(data, this);
		
	});
	
}

function processUploadResponse(response, controller) {
	
	if (response === null) {
		alert('Could not parse server response');
		return;
	}	
	
	var fileName = response.name;
	//alert(fileName);
	
	// Clear the selection for all the other selected files
	controller.send('clearSelections');
	
	
	// Get a reference to the object model from the controller
	var model = controller.get('model');
	
	// First create the new file object with the fileName received
	var newFile = App.File.create({
		fileName: fileName,
		isSelected : false,
		classes: []
	});
	
	model.pushObject(newFile);
	
	// Let's now read all the classes
	if (response.classes.length > 0) {
		var classCount = response.classes.length;
		for (var i = 0; i < classCount; i++) {
			// read the class from the response
			var myClass = response.classes[i];
			// And create a new corresponding class from the model
			var newClass = App.Class.create({
				className: myClass.name,
				methods: []
			});
				
			newFile.classes.pushObject(newClass);
				
			for (var j = 0; j < myClass.methods.length; j++) {
				var myMethod = myClass.methods[j];
				var newMethod = App.Method.create({
					methodName : myMethod.name,
					isInstrumented : false,
					parameters : []
				});
				newClass.methods.pushObject(newMethod);
				
				for (var k = 0; k < myMethod.params.length; k++) {
					newMethod.parameters.pushObject(myMethod.params[k]);
				}
			}
		}
	}
	
	// After adding the new object to the model, transition to the new route
	controller.transitionToRoute('config', newFile);
	
}

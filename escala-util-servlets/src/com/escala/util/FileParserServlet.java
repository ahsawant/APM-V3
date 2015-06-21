package com.escala.util;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

import com.escala.util.parser.CompUnitVisitor;
import com.escala.xfer.parser.JavaFile;

/**
 * Servlet implementation class FileParserServlet
 */
public class FileParserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private boolean isMultipart;
	private int maxMemSize = 10000 * 1024;
	private int maxFileSize = 100000 * 1024;

	public void init() {
		// Nothing to do here
	}

	/**
	 * Default constructor.
	 */
	public FileParserServlet() {
		isMultipart = false;

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		throw new ServletException("GET method used with "
				+ getClass().getName() + ": POST method required.");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// Check that we have a file upload request
		isMultipart = ServletFileUpload.isMultipartContent(request);
		java.io.PrintWriter out = response.getWriter();
		
		if (!isMultipart) {
			response.setContentType("text/html");
			out.println("<html>");
			out.println("<head>");
			out.println("<title>Servlet upload</title>");
			out.println("</head>");
			out.println("<body>");
			out.println("<p>No file uploaded</p>");
			out.println("</body>");
			out.println("</html>");
			return;
		}
		
		response.setContentType("application/json");

		DiskFileItemFactory factory = new DiskFileItemFactory();
		// maximum size that will be stored in memory
		factory.setSizeThreshold(maxMemSize);
		// Location to save data that is larger than maxMemSize.
		factory.setRepository(new File("temp"));

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);
		// maximum file size to be uploaded.
		upload.setSizeMax(maxFileSize);

		try {
			// Parse the request to get file items.
			List<FileItem> fileItems = upload.parseRequest(request);

			// Process the uploaded file items
			for (FileItem fi : fileItems) {
				if (!fi.isFormField()) {
					// Get the uploaded file parameters
					String fieldName = fi.getFieldName();
					String fileName = fi.getName();
					String contentType = fi.getContentType();
					boolean isInMemory = fi.isInMemory();
					long sizeInBytes = fi.getSize();

					System.out.println("Received File: " + 
							"\nField Name: " + fieldName + 
							"\nFile Name: " + fileName +
							"\nContent Type: " + contentType +
							"\nFile Size: " + sizeInBytes +
							"\nIn Memory: " + isInMemory);

					String json = parseFile(fileName, fi.getInputStream());
					out.println(json);
					System.out.println(json);
				}
			}
	
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

	private String parseFile(String fileName, InputStream stream) {
		String result = "";
		CompilationUnit cu;
		try {
			// parse the file
			cu = JavaParser.parse(stream);
			JavaFile javaFile = new JavaFile(fileName);
			CompUnitVisitor visitor = new CompUnitVisitor();
			visitor.visit(cu, javaFile);
			result = getJson(javaFile);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}

	private String getJson(JavaFile javaFile) {
		String result = null;
		ObjectMapper mapper = new ObjectMapper();
		AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
		mapper.setAnnotationIntrospector(introspector);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			mapper.writeValue(os, javaFile);
			result = os.toString();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}

}

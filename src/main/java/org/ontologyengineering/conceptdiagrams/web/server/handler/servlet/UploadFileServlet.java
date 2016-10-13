package org.ontologyengineering.conceptdiagrams.web.server.handler.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;


/**
 * Author: Michael Compton<br>
 * Date: August 2016<br>
 * See license information in base directory.
 */
public class UploadFileServlet extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");


        boolean isMultipart = ServletFileUpload.isMultipartContent(new ServletRequestContext(request));

        if (isMultipart) {
            FileItem uploadItem = getFileItem(request);
            if (uploadItem == null) {
                super.service(request, response);
            } else {
                UUID ontologyID = UUID.randomUUID();

                String fileName = "diagrams_2_OWL_output" + ontologyID + ".cd.hst";

                File saveFile = new File("/tmp/org.ontologyengineering.conceptdiagrams." + fileName);

                try {
                    uploadItem.write(saveFile);
                    response.getWriter().write(fileName);
                } catch (Exception e) {
                    // why??
                    super.service( request, response );
                }
            }
        } else { // not a mulitpart
            super.service( request, response );
        }
    }

    private FileItem getFileItem(HttpServletRequest request) {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);

        try {
            List<FileItem> items = upload.parseRequest(request);
            for ( FileItem item : items )
            {
                if (!item.isFormField()
                        && item.getFieldName().equals("uploadFileName")) {
                    return item;
                }
            }
        } catch (FileUploadException e) {
            return null;
        }
        return null;
    }


}

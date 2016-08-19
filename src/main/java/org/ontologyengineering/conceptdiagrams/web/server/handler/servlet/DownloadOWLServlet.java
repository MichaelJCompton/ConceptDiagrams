package org.ontologyengineering.conceptdiagrams.web.server.handler.servlet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Author: Michael Compton<br>
 * Date: August 2016<br>
 * See license information in base directory.
 */
public class DownloadOWLServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp ) throws ServletException, IOException
    {
        String fileName = req.getParameter( "filename" );
        File downloadableFile = new File("/tmp/org.ontologyengineering.conceptdiagrams." + fileName);

        resp.setContentType( "application/octet-stream" );
        resp.setHeader( "Content-Disposition:", "attachment;filename=" + "\"" + fileName + ".owl\"" );
        ServletOutputStream os = resp.getOutputStream();

        resp.setContentLength( Long.valueOf( downloadableFile.length() ).intValue() );

        try {
            InputStream is = FileUtils.openInputStream(downloadableFile);
            try {
                IOUtils.copy(is, os);
            } finally {
                is.close();
            }
        } finally {
            os.close();
        }

    }
}


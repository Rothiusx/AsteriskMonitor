/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import java.util.ArrayList;

/**
 *
 * @author rothi
 */
public class HtmlBuilder {
    private final FileReader fr;
    private String document;

    public HtmlBuilder() {
        fr = new FileReader();
        document = "";
    }
    
    public void setContent(String fileName, String htmlContent) {
        String htmlFile = fr.readFile(fileName);
        String startIndex = "<!-- Table Start -->";
        String endIndex = "<!-- Table End -->";
        
        if(htmlContent == null) {
            htmlContent = "";
        }
        
        document = htmlFile.substring(0, htmlFile.indexOf(startIndex) + startIndex.length()) 
                + htmlContent + htmlFile.substring(htmlFile.indexOf(endIndex));
    }
    
    public String getTableHeader(String tableTitle, ArrayList<String> headers) {
        
        String tableHeader = "<h2>" + tableTitle + "</h2>"
                + "<div class=\"table-responsive\">"
                + "<table class=\"table table-striped table-sm\">"
                + "<thead>"
                + "<tr>";

        for (int i = 0; i < headers.size(); i++) {
            tableHeader += "<th>" + headers.get(i) + "</th>";
        }

        tableHeader += "</tr>"
                + "</thead>"
                + "<tbody>";
            
        return tableHeader;
    }
    
    public String getFile(String FileName) {
        String content = fr.readFile(FileName);
        return content;
    }

    public String getDocument() {
        return document;
    }
}

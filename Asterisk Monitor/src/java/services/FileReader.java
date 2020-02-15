/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author rothi
 */
public class FileReader {
    private String webPath = "D:/Users/rothi/Documents/NetBeansProjects/Asterisk Monitor/web/";

    public FileReader() {
    }
    
    public FileReader(String webPath) {
        this.webPath = webPath;
    }

    private String getWebPath() {
        return webPath;
    }
    
    public String readFile(String fileName) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(getWebPath() + fileName)));
        }
        catch (IOException e) {
            System.out.println(e);
        }
        return content;
    }
}
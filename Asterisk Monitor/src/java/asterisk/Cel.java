/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asterisk;

import java.util.ArrayList;

/**
 *
 * @author rothi
 */
public class Cel {
    private ArrayList<String> celHeaders;

    public Cel() {
        celHeaders = new ArrayList<>();
        initCelTable();
    }
    
    private void initCelTable() {
        celHeaders.add(0, "id");
        celHeaders.add(1, "eventtype");
        celHeaders.add(2, "eventtime");
        celHeaders.add(3, "userdeftype");
        celHeaders.add(4, "cid_name");
        celHeaders.add(5, "cid_number");
        celHeaders.add(6, "cid_ani");
        celHeaders.add(7, "cid_rdnis");
        celHeaders.add(8, "cid_dnid");
        celHeaders.add(9, "exten");
        celHeaders.add(10, "context");
        celHeaders.add(11, "channame");
        celHeaders.add(12, "appname");
        celHeaders.add(13, "appdata");
        celHeaders.add(14, "amaflags");
        celHeaders.add(15, "accountcode");
        celHeaders.add(16, "peeraccount");
        celHeaders.add(17, "uniqueid");
        celHeaders.add(18, "linkedid");
        celHeaders.add(19, "userfield");
        celHeaders.add(20, "peer");
    }
    
    public String getCelColumn(int index) {
        return celHeaders.get(index);
    }

    public ArrayList<String> getCelHeaders() {
        return celHeaders;
    }
}

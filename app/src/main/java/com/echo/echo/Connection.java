package com.echo.echo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Kevin on 11/14/2016.
 */

public class Connection implements Serializable {
    public String username;
    public ArrayList<String> linkKeys = new ArrayList<String>();

    public Connection(){

    }

    public Connection(String username){
        this.username = username;
    }

    //add key to list of connections
    public void addLinkKey(String newKey){
        linkKeys.add(newKey);
    }
}

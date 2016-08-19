package org.ontologyengineering.conceptdiagrams.web.client.ui;


import com.google.gwt.core.client.GWT;

/**
 * Author: Michael Compton<br>
 * Date: July 2016<br>
 * See license information in base directory.
 */
public class InterfaceGlobals {

    public static InterfaceGlobals INSTANCE = new InterfaceGlobals();

    private MenuIcons iconImages = GWT.create(MenuIcons.class);

    private InterfaceGlobals() {

    }

    public MenuIcons getIconImages() {
        return iconImages;
    }

}

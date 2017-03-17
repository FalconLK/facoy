package org.eoti.rei.ndfcmd;

import org.eoti.io.xls.XLSReader;
import java.util.List;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;

public class MapCmd extends NDFBrowserCommand {

    protected static String googleFmt = "http://www.google.com/maps?f=q&hl=en&q=%s&ie=UTF8&z=15&om=1&iwloc=addr";

    public String[] commands() {
        return new String[] { "map" };
    }

    public void process(List<String> commandLine) throws Exception {
        XLSReader reader = browser.getReader();
        if (reader == null) {
            format("No data loaded. Please try 'load filename'.\n");
            return;
        }
        if (commandLine.size() == 2) {
            int rowNum = Integer.parseInt(commandLine.get(1));
            if (commandLine.get(0).equalsIgnoreCase("house")) displayHouse(rowNum); else if (commandLine.get(0).equalsIgnoreCase("auction")) displayAuction(rowNum); else displayHelp();
            return;
        }
        format("\n");
        for (int rowNum : reader.rows()) format("%s. %s\n", rowNum, getAddress(rowNum));
        String input = readLine("Choose House Number> ");
        int rowNum = Integer.parseInt(input);
        input = readLine("Display map for [H]ouse or [A]uction Location? > ");
        if (input.equalsIgnoreCase("H")) displayHouse(rowNum); else if (input.equalsIgnoreCase("A")) displayAuction(rowNum);
    }

    public String getAuctionAddress(int rowNum) {
        XLSReader reader = browser.getReader();
        return String.format("%s, %s, OR", reader.getColumn("Foreclosure Auction Address").getString(rowNum), reader.getColumn("Foreclosure Auction City").getString(rowNum));
    }

    protected void displayAuction(int rowNum) throws URISyntaxException, IOException {
        if (!Desktop.isDesktopSupported()) {
            format("'map' is not supported on this platofrm.\n");
            return;
        }
        Desktop.getDesktop().browse(new URI(String.format(googleFmt, getAuctionAddress(rowNum).replaceAll(" ", "+"))));
    }

    protected void displayHouse(int rowNum) throws URISyntaxException, IOException {
        if (!Desktop.isDesktopSupported()) {
            format("'map' is not supported on this platofrm.\n");
            return;
        }
        Desktop.getDesktop().browse(new URI(String.format(googleFmt, getAddress(rowNum).replaceAll(" ", "+"))));
    }

    public void displayHelp() {
        if (!Desktop.isDesktopSupported()) {
            format("'map' is not supported on this platofrm.\n");
            return;
        }
        format("map: display menu and prompt for map to display.\n");
        format("map house number: display a map to a specific house\n");
        format("map auction number: display a map to a specific auction\n");
    }
}

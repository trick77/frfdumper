package com.trick77.inspector;

import java.util.ArrayList;

public class Container {

    private String revision;
    private String name;
    private String date;
    private String sa2;
    private ArrayList<String> nameIdents = new ArrayList<>();
    private ArrayList<String> versionIdents = new ArrayList<>();

    public ArrayList<Security> getSecuritys() {
        return securitys;
    }

    public void addSecurity(Security security) {
        this.securitys.add(security);
    }

    private ArrayList<Security> securitys = new ArrayList<>();

    public String getSa2() {
        return sa2;
    }

    public void setSa2(String sa2) {
        this.sa2 = sa2;
    }

    public ArrayList<String> getNameIdents() {
        return nameIdents;
    }

    public void addNameIdent(String ident) {
        this.nameIdents.add(ident);
    }

    public ArrayList<String> getVersionIdents() {
        return versionIdents;
    }

    public void addVersionIdent(String ident) {
        this.versionIdents.add(ident);
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static String getNameWithoutSpaces(final String name) {
        if (name != null && name.length() > 0) {
            return name.replaceAll("\\s","");
        }
        return name;
    }

    public static String getIndex(final String name) {
        String nameWithoutSpaces = getNameWithoutSpaces(name);
        if (nameWithoutSpaces != null && nameWithoutSpaces.length() > 9) {
            return nameWithoutSpaces.substring(9);
        }
        return "";
    }

    public static String getNameWithoutRevision(final String name, final String revision) {
        String nameWithoutRevision = null;
        if (name != null && name.length() > 0 && revision != null && revision.length() > 0) {
            if (name.endsWith(revision)) {
                nameWithoutRevision = name.substring(0, name.length() - revision.length()).trim();
            }
        }
        if (nameWithoutRevision.endsWith("_")) {
            nameWithoutRevision = nameWithoutRevision.substring(0, nameWithoutRevision.length() - 1);
        }
        return nameWithoutRevision;
    }

    public static String getBaseName(final String name) {
        String nameWithoutSpaces = getNameWithoutSpaces(name);
        if (nameWithoutSpaces != null && nameWithoutSpaces.length() > 9) {
            return nameWithoutSpaces.substring(0, 9);
        }
        return name;

    }

}

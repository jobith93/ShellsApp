package com.three38inc.apps.shellsapp;

import java.util.ArrayList;

/**
 * Created by jobith on 20/01/17.
 */

public class EventItem {



    private int id;
    private String name;
    private String nickName;
    private String tagLine;
    private String description;
    private String icon;
    private String iconSq;
    private String members;
    private String teams;
    private ArrayList<Coordinator> coordinators;
    private ArrayList<Rules> rules;

    public EventItem(){}

    public EventItem(int id, String name, String nickName, String tagLine, String description, String icon, String iconSq, String members, String teams, ArrayList<Coordinator> coordinators, ArrayList<Rules> rules) {
        this.id = id;
        this.name = name;
        this.nickName = nickName;
        this.tagLine = tagLine;
        this.description = description;
        this.icon = icon;
        this.iconSq = iconSq;
        this.members = members;
        this.teams = teams;
        this.coordinators = coordinators;
        this.rules = rules;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getTagLine() {
        return tagLine;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIconSq() {
        return iconSq;
    }

    public void setIconSq(String iconSq) {
        this.iconSq = iconSq;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public String getTeams() {
        return teams;
    }

    public void setTeams(String teams) {
        this.teams = teams;
    }

    public ArrayList<Coordinator> getCoordinators() {
        return coordinators;
    }

    public void setCoordinators(ArrayList<Coordinator> coordinators) {
        this.coordinators = coordinators;
    }

    public ArrayList<Rules> getRules() {
        return rules;
    }

    public void setRules(ArrayList<Rules> rules) {
        this.rules = rules;
    }


    private class Coordinator{

        private String name;
        private String designation;
        private String contact;
        private String email;

        public Coordinator(String name, String designation, String contact, String email) {
            this.name = name;
            this.designation = designation;
            this.contact = contact;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesignation() {
            return designation;
        }

        public void setDesignation(String designation) {
            this.designation = designation;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }


    }

    private class Rules{
        private String line;

        public Rules(String line) {
            this.line = line;
        }

        public String getLine() {
            return line;
        }

        public void setLine(String line) {
            this.line = line;
        }
    }
}

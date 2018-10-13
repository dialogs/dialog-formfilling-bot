package im.dlg.botsdk.domain;

import dialog.UsersOuterClass;

public class User {

    public enum Sex {
        MALE,
        FEMALE;

        public static Sex fromServerModel(UsersOuterClass.Sex sex) {
            return sex.getNumber() == 3 ? MALE : FEMALE;
        }
    }

    private Peer peer;
    private String name;
    private String nick;
    private String about;
    private Sex sex;
    private String language;
    private String timeZone;

    public User(Peer peer, String name, String nick, Sex sex, String about, String language, String timeZone) {
        this.peer = peer;
        this.name = name;
        this.nick = nick;
        this.about = about;
        this.sex = sex;
        this.language = language;
        this.timeZone = timeZone;
    }

    public Peer getPeer() {
        return peer;
    }

    public String getName() {
        return name;
    }

    public String getNick() {
        return nick;
    }

    public String getAbout() {
        return about;
    }

    public Sex getSex() {
        return sex;
    }

    public String getLanguage() {
        return language;
    }

    public String getTimeZone() {
        return timeZone;
    }

    @Override
    public String toString() {
        return "User{" +
                "peer=" + peer +
                ", name='" + name + '\'' +
                ", nick='" + nick + '\'' +
                ", about='" + about + '\'' +
                ", sex=" + sex +
                ", language='" + language + '\'' +
                ", timeZone='" + timeZone + '\'' +
                '}';
    }
}

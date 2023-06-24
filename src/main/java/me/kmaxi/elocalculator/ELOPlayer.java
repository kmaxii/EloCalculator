package me.kmaxi.elocalculator;

public class ELOPlayer
{
    public String name;
    public int place     = 0;
    public int eloPre    = 0;
    public int eloPost   = 0;
    public int eloChange = 0;
    public int elo;

    public ELOPlayer teamRequest;

    public ELOPlayer(String name, int elo, ELOPlayer teamRequest) {
        this.name = name;
        this.elo = elo;
        this.teamRequest = teamRequest;
    }

    @Override
    public String toString() {
        return "me.kmaxi.elocalculator.ELOPlayer{" +
                "name='" + name + '\'' +
                ", elo=" + elo +
                '}';
    }
}
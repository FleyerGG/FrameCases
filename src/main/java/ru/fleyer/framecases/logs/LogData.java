package ru.fleyer.framecases.logs;

public class LogData {
    private final String caseName;
    private final String playerName;
    private final String prize;
    private final long date;

    public LogData(String caseName, String playerName, String prize, long date) {
        this.caseName = caseName;
        this.playerName = playerName;
        this.prize = prize;
        this.date = date;
    }

    public String getCaseName() {
        return this.caseName;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public String getPrize() {
        return this.prize;
    }

    public long getDate() {
        return this.date;
    }
}

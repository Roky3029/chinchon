package utilities;

public class GameData {
    private boolean discard, keepPlaying;
    private int playerDebt, cpuDebt;

    public GameData(boolean d, boolean kp, int pd, int cd){
        this.discard = d;
        this.keepPlaying = kp;
        this.playerDebt = pd;
        this.cpuDebt = cd;
    }

    /////////////////////// SETTERS ///////////////////////

    public void setDiscard(boolean d){
        this.discard = d;
    }

    public void setKeepPlaying(boolean kp){
        this.keepPlaying = kp;
    }

    public void setPlayerDebt(int pd){
        this.playerDebt = pd;
    }

    public void setCpuDebt(int cd){
        this.cpuDebt = cd;
    }

    /////////////////////// GETTERS ///////////////////////

    public boolean getDiscard(){
        return this.discard;
    }

    public boolean getKeepPlaying(){
        return this.keepPlaying;
    }

    public int getPlayerDebt(){
        return this.playerDebt;
    }

    public int getCpuDebt(){
        return this.cpuDebt;
    }
}

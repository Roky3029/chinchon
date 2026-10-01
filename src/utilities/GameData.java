package utilities;

public class GameData {
    private boolean keepPlaying;
    private int playerDebt, cpuDebt;

    public GameData(boolean kp, int pd, int cd){
        this.keepPlaying = kp;
        this.playerDebt = pd;
        this.cpuDebt = cd;
    }

    /////////////////////// SETTERS ///////////////////////

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

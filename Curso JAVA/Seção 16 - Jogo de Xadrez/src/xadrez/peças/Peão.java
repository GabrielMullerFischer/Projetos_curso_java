package xadrez.peças;

import tabuleiro.Posição;
import tabuleiro.Tabuleiro;
import xadrez.Cor;
import xadrez.PartidaXadrez;
import xadrez.PeçaXadrez;

public class Peão extends PeçaXadrez {

    private PartidaXadrez partidaXadrez;

    public Peão(Tabuleiro tabuleiro, Cor cor, PartidaXadrez partidaXadrez) {
        super(tabuleiro, cor);
        this.partidaXadrez = partidaXadrez;
    }

    @Override
    public boolean[][] possivelMover() {
        boolean[][] mat = new boolean[getTabuleiro().getLinhas()][getTabuleiro().getColunas()];
        
        Posição p = new Posição(0, 0);

        if(getCor() == Cor.Branco){
            p.setValor(posição.getLinha() - 1, posição.getColuna());
            if(getTabuleiro().posiçãoExistente(p) && !getTabuleiro().temPeça(p)){
                mat[p.getLinha()][p.getColuna()] = true;
            }
            p.setValor(posição.getLinha() - 2, posição.getColuna());
            Posição p2 = new Posição(posição.getLinha() - 1, posição.getColuna());
            if(getTabuleiro().posiçãoExistente(p) && !getTabuleiro().temPeça(p) && getTabuleiro().posiçãoExistente(p2) && !getTabuleiro().temPeça(p2) && getMoviCont() == 0){
                mat[p.getLinha()][p.getColuna()] = true;
            }
            p.setValor(posição.getLinha() - 1, posição.getColuna() - 1);
            if(getTabuleiro().posiçãoExistente(p) && possivelComer(p)){
                mat[p.getLinha()][p.getColuna()] = true;
            }
            p.setValor(posição.getLinha() -1, posição.getColuna() + 1);
            if(getTabuleiro().posiçãoExistente(p) && possivelComer(p)){
                mat[p.getLinha()][p.getColuna()] = true;
            }
            //Especial
            if(posição.getLinha() == 3){
                Posição esqu = new Posição(posição.getLinha(), posição.getColuna() - 1);
                if(getTabuleiro().posiçãoExistente(esqu) && possivelComer(esqu) && getTabuleiro().peça(esqu) == partidaXadrez.getVulneravel()){
                    mat[esqu.getLinha() - 1][esqu.getColuna()] = true;
                }
                Posição dir = new Posição(posição.getLinha(), posição.getColuna() + 1);
                if(getTabuleiro().posiçãoExistente(dir) && possivelComer(dir) && getTabuleiro().peça(dir) == partidaXadrez.getVulneravel()){
                    mat[dir.getLinha() - 1][dir.getColuna()] = true;
                }
            }
        }else{
            p.setValor(posição.getLinha() + 1, posição.getColuna());
            if(getTabuleiro().posiçãoExistente(p) && !getTabuleiro().temPeça(p)){
                mat[p.getLinha()][p.getColuna()] = true;
            }
            p.setValor(posição.getLinha() + 2, posição.getColuna());
            Posição p2 = new Posição(posição.getLinha() + 1, posição.getColuna());
            if(getTabuleiro().posiçãoExistente(p)&& !getTabuleiro().temPeça(p) && getTabuleiro().posiçãoExistente(p2) && !getTabuleiro().temPeça(p2) && getMoviCont() == 0){
                mat[p.getLinha()][p.getColuna()] = true;
            }
            p.setValor(posição.getLinha() + 1, posição.getColuna() - 1);
            if(getTabuleiro().posiçãoExistente(p) && possivelComer(p)){
                mat[p.getLinha()][p.getColuna()] = true;
            }
            p.setValor(posição.getLinha() + 1, posição.getColuna() + 1);
            if(getTabuleiro().posiçãoExistente(p) && possivelComer(p)){
                mat[p.getLinha()][p.getColuna()] = true;
            }
            //Especial
            if(posição.getLinha() == 4){
                Posição esqu = new Posição(posição.getLinha(), posição.getColuna() - 1);
                if(getTabuleiro().posiçãoExistente(esqu) && possivelComer(esqu) && getTabuleiro().peça(esqu) == partidaXadrez.getVulneravel()){
                    mat[esqu.getLinha() + 1][esqu.getColuna()] = true;
                }
                Posição dir = new Posição(posição.getLinha(), posição.getColuna() + 1);
                if(getTabuleiro().posiçãoExistente(dir) && possivelComer(dir) && getTabuleiro().peça(dir) == partidaXadrez.getVulneravel()){
                    mat[dir.getLinha() + 1][dir.getColuna()] = true;
                }
            }            
        }
        return mat;
    }
    
    @Override
    public String toString(){
        return "P";
    }
}

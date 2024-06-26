package xadrez;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import tabuleiro.*;
import xadrez.peças.*;

public class PartidaXadrez {

    private int turno;
    private Cor corPlayer;
    private Tabuleiro tabuleiro;
    private boolean check;
    private boolean checkMate;
    private PeçaXadrez vulneravel;
    private PeçaXadrez promo;

    private List<Peça> peçasNoTabuleiro = new ArrayList<>();
	private List<Peça> capturaPeças = new ArrayList<>();

    public PartidaXadrez() {
        tabuleiro = new Tabuleiro(8, 8);
        turno = 1;
        corPlayer = Cor.Branco;
        iniciaPartida();
    }

    public int getTurno() {
        return turno;
    }

    public Cor getCorPlayer() {
        return corPlayer;
    }

    public boolean getCheck() {
		return check;
	}

    public boolean getCheckMate() {
        return checkMate;
    }

    public PeçaXadrez getPromo() {
        return promo;
    }

    public PeçaXadrez[][] getpeças() {
        PeçaXadrez[][] mat = new PeçaXadrez[tabuleiro.getLinhas()][tabuleiro.getColunas()];
        for (int i = 0; i < tabuleiro.getLinhas(); i++) {
            for (int j = 0; j < tabuleiro.getColunas(); j++) {
                mat[i][j] = (PeçaXadrez) tabuleiro.peça(i, j);
            }
        }
        return mat;
    }

    public PeçaXadrez getVulneravel(){
        return vulneravel;
    }

    public boolean[][] possivelMover(XadrezPosição origemPosição){
        Posição posição = origemPosição.toPosição();
        validadeAtualPosição(posição);
        return tabuleiro.peça(posição).possivelMover();
    }

    public PeçaXadrez movimentaçãoXadrez(XadrezPosição origemPosição, XadrezPosição alvoPosição){
        Posição origem = origemPosição.toPosição();
        Posição alvo = alvoPosição.toPosição();
        validadeAtualPosição(origem);
        validadeAlvoPosição(origem, alvo);
        Peça capituraPeça = movimentaPeça(origem, alvo);
        if (testeCheck(corPlayer)) {
			desfazMovi(origem, alvo, capituraPeça);
			throw new XadrezExeção("Você não pode se colocar em cheque!");
		}

        PeçaXadrez moviPeça = (PeçaXadrez)tabuleiro.peça(alvo);

        promo = null;
        if(moviPeça instanceof Peão){
            if((moviPeça.getCor() == Cor.Branco && alvo.getLinha() == 0) || (moviPeça.getCor() == Cor.Branco && alvo.getLinha() == 7)){
                promo = (PeçaXadrez)tabuleiro.peça(alvo);
                promo = substituirPromo("Q");
            }
        }

		check = (testeCheck(oponente(corPlayer))) ? true : false;

        if(testeCheckMate(oponente(corPlayer))){
            checkMate = true;
        }else{
            nextTurno();
        }

        if(moviPeça instanceof Peão && (alvo.getLinha() == origem.getLinha() - 2 || alvo.getLinha() == origem.getLinha() + 2)){
            vulneravel = moviPeça;
        }else{
            vulneravel = null;
        }

        return (PeçaXadrez) capituraPeça;
    }

    public PeçaXadrez substituirPromo(String type){
        if(promo == null){
            throw new IllegalStateException("Não a peças para promoção");
        }
        if( !type.equals("B") && !type.equals("N") && !type.equals("R") && !type.equals("Q")){
            return promo;
        }

        Posição posi = promo.getXadrezPosição().toPosição();
        Peça p = tabuleiro.removePeça(posi);
        peçasNoTabuleiro.remove(p);

        PeçaXadrez novaPeça = novaPeça(type, promo.getCor());
        tabuleiro.posiçãoPeça(novaPeça, posi);
        peçasNoTabuleiro.add(novaPeça);

        return novaPeça;
    }

    private PeçaXadrez novaPeça(String type, Cor cor){
        if(type.equals("B")) return new Bispo(tabuleiro, cor);
        if(type.equals("N")) return new Cavalo(tabuleiro, cor);
        if(type.equals("Q")) return new Rainha(tabuleiro, cor);
        return new Torre(tabuleiro, cor);
    }

    private Peça movimentaPeça(Posição origem, Posição alvo){
        PeçaXadrez p = (PeçaXadrez)tabuleiro.removePeça(origem);
        p.incrementaNMovimento();
        Peça capturaPeça = tabuleiro.removePeça(alvo);
        tabuleiro.posiçãoPeça(p, alvo);
        if(capturaPeça != null)
            peçasNoTabuleiro.remove(capturaPeça);
            capturaPeças.add(capturaPeça);

        //Movimento especial
        if(p instanceof Rei && alvo.getColuna() == origem.getColuna() + 2){
            Posição origemT = new Posição(origem.getLinha(), origem.getColuna() + 3);
            Posição alvoT = new Posição(origem.getLinha(), origem.getColuna() + 1);
            PeçaXadrez torre = (PeçaXadrez)tabuleiro.removePeça(origemT);
            tabuleiro.posiçãoPeça(torre, alvoT);
            torre.incrementaNMovimento();
        }

        if(p instanceof Rei && alvo.getColuna() == origem.getColuna() - 2){
            Posição origemT = new Posição(origem.getLinha(), origem.getColuna() - 4);
            Posição alvoT = new Posição(origem.getLinha(), origem.getColuna() - 1);
            PeçaXadrez torre = (PeçaXadrez)tabuleiro.removePeça(origemT);
            tabuleiro.posiçãoPeça(torre, alvoT);
            torre.incrementaNMovimento();
        }

        if (p instanceof Peão){
            if(origem.getColuna() != alvo.getColuna() && capturaPeça == null){
                Posição peaoPosição;
                if(p.getCor() == Cor.Branco){
                    peaoPosição = new Posição(alvo.getLinha() + 1, alvo.getColuna());
                }else{
                    peaoPosição = new Posição(alvo.getLinha() - 1, alvo.getColuna());
                }
                capturaPeça = tabuleiro.removePeça(peaoPosição);
                capturaPeças.add(capturaPeça);
                peçasNoTabuleiro.remove(capturaPeça);
            }
        }

        return capturaPeça;
    }

    private void desfazMovi(Posição origem, Posição alvo, Peça capturaPeça) {
		PeçaXadrez p = (PeçaXadrez)tabuleiro.removePeça(alvo);
        p.decrementaNMovimento();
		tabuleiro.posiçãoPeça(p, origem);
		if (capturaPeça != null) {
			tabuleiro.posiçãoPeça(capturaPeça, alvo);
			capturaPeças.remove(capturaPeça);
			peçasNoTabuleiro.add(capturaPeça);
		}

        //Movimento especial
        if(p instanceof Rei && alvo.getColuna() == origem.getColuna() + 2){
            Posição origemT = new Posição(origem.getLinha(), origem.getColuna() + 3);
            Posição alvoT = new Posição(origem.getLinha(), origem.getColuna() + 1);
            PeçaXadrez torre = (PeçaXadrez)tabuleiro.removePeça(alvoT);
            tabuleiro.posiçãoPeça(torre, origemT);
            torre.decrementaNMovimento();
        }

        if(p instanceof Rei && alvo.getColuna() == origem.getColuna() - 2){
            Posição origemT = new Posição(origem.getLinha(), origem.getColuna() - 4);
            Posição alvoT = new Posição(origem.getLinha(), origem.getColuna() - 1);
            PeçaXadrez torre = (PeçaXadrez)tabuleiro.removePeça(alvoT);
            tabuleiro.posiçãoPeça(torre, origemT);
            torre.decrementaNMovimento();
        }

        if (p instanceof Peão){
            if(origem.getColuna() != alvo.getColuna() && capturaPeça == vulneravel){
                PeçaXadrez peao = (PeçaXadrez)tabuleiro.removePeça(alvo);
                Posição peaoPosição;
                if(p.getCor() == Cor.Branco){
                    peaoPosição = new Posição(3, alvo.getColuna());
                }else{
                    peaoPosição = new Posição(4, alvo.getColuna());
                }
                tabuleiro.posiçãoPeça(peao, peaoPosição);
            }
        }
	}

    private void validadeAtualPosição(Posição posição){
        if(!tabuleiro.temPeça(posição)){
            throw new XadrezExeção("Não a peça nessa posição.");
        }
        if(corPlayer != ((PeçaXadrez)tabuleiro.peça(posição)).getCor()){
            throw new XadrezExeção("Esta peça não é sua.");
        }
        if(!tabuleiro.peça(posição).possivelUmMovimento()){
            throw new XadrezExeção("Não á movimentações livres para essa peça.");
        }
    }

    private void validadeAlvoPosição(Posição origem, Posição alvo){
        if(!tabuleiro.peça(origem).possivelMove(alvo)){
            throw new XadrezExeção("Essa peça não pode se mover para a posição de destino!");
        }
    }

    private void nextTurno(){
        turno++;
        corPlayer = (corPlayer == Cor.Branco) ? Cor.Preto : Cor.Branco;
    }

    private void lugarPeça(char coluna, int linha, PeçaXadrez peça) {
        tabuleiro.posiçãoPeça(peça, new XadrezPosição(coluna, linha).toPosição());
        peçasNoTabuleiro.add(peça);
    }

    private Cor oponente(Cor cor) {
		return (cor == Cor.Branco) ? Cor.Preto : Cor.Branco;
	}

	private PeçaXadrez rei(Cor cor) {
		List<Peça> list = peçasNoTabuleiro.stream().filter(x -> ((PeçaXadrez)x).getCor() == cor).collect(Collectors.toList());
		for (Peça p : list) {
			if (p instanceof Rei) {
				return (PeçaXadrez)p;
			}
		}
		throw new IllegalStateException("There is no " + cor + " king on the board");
	}

	private boolean testeCheck(Cor cor) {
		Posição reiPosição = rei(cor).getXadrezPosição().toPosição();
		List<Peça> oponentePeças = peçasNoTabuleiro.stream().filter(x -> ((PeçaXadrez)x).getCor() == oponente(cor)).collect(Collectors.toList());
		for (Peça p : oponentePeças) {
			boolean[][] mat = p.possivelMover();
			if (mat[reiPosição.getLinha()][reiPosição.getColuna()]) {
				return true;
			}
		}
		return false;
	}

    private boolean testeCheckMate(Cor cor){
        if (!testeCheck(cor)) {
			return false;
		}
		List<Peça> list = peçasNoTabuleiro.stream().filter(x -> ((PeçaXadrez)x).getCor() == cor).collect(Collectors.toList());
		for (Peça p : list) {
			boolean[][] mat = p.possivelMover();
			for (int i=0; i<tabuleiro.getLinhas(); i++) {
				for (int j=0; j<tabuleiro.getColunas(); j++) {
					if (mat[i][j]) {
						Posição origem = ((PeçaXadrez)p).getXadrezPosição().toPosição();
						Posição alvo = new Posição(i, j);
						Peça capturaPeça = movimentaPeça(origem, alvo);
						boolean testeCheck = testeCheck(cor);
						desfazMovi(origem, alvo, capturaPeça);
						if (!testeCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
    }

    private void iniciaPartida() {
        lugarPeça('h', 1, new Torre(tabuleiro, Cor.Branco));
        lugarPeça('a', 1, new Torre(tabuleiro, Cor.Branco));
        lugarPeça('f', 1, new Bispo(tabuleiro, Cor.Branco));
        lugarPeça('c', 1, new Bispo(tabuleiro, Cor.Branco));
        lugarPeça('b', 1, new Cavalo(tabuleiro, Cor.Branco));
        lugarPeça('g', 1, new Cavalo(tabuleiro, Cor.Branco));
        lugarPeça('e', 1, new Rei(tabuleiro, Cor.Branco, this));
        lugarPeça('d', 1, new Rainha(tabuleiro, Cor.Branco));
        lugarPeça('a', 2, new Peão(tabuleiro, Cor.Branco, this));
        lugarPeça('b', 2, new Peão(tabuleiro, Cor.Branco, this));
        lugarPeça('c', 2, new Peão(tabuleiro, Cor.Branco, this));
        lugarPeça('d', 2, new Peão(tabuleiro, Cor.Branco, this));
        lugarPeça('e', 2, new Peão(tabuleiro, Cor.Branco, this));
        lugarPeça('f', 2, new Peão(tabuleiro, Cor.Branco, this));
        lugarPeça('g', 2, new Peão(tabuleiro, Cor.Branco, this));
        lugarPeça('h', 2, new Peão(tabuleiro, Cor.Branco, this));


        lugarPeça('h', 8, new Torre(tabuleiro, Cor.Preto));
        lugarPeça('a', 8, new Torre(tabuleiro, Cor.Preto));
        lugarPeça('f', 8, new Bispo(tabuleiro, Cor.Preto));
        lugarPeça('c', 8, new Bispo(tabuleiro, Cor.Preto));
        lugarPeça('b', 8, new Cavalo(tabuleiro, Cor.Preto));
        lugarPeça('g', 8, new Cavalo(tabuleiro, Cor.Preto));
        lugarPeça('e', 8, new Rei(tabuleiro, Cor.Preto, this));
        lugarPeça('d', 8, new Rainha(tabuleiro, Cor.Preto));
        lugarPeça('a', 7, new Peão(tabuleiro, Cor.Preto, this));
        lugarPeça('b', 7, new Peão(tabuleiro, Cor.Preto, this));
        lugarPeça('c', 7, new Peão(tabuleiro, Cor.Preto, this));
        lugarPeça('d', 7, new Peão(tabuleiro, Cor.Preto, this));
        lugarPeça('e', 7, new Peão(tabuleiro, Cor.Preto, this));
        lugarPeça('f', 7, new Peão(tabuleiro, Cor.Preto, this));
        lugarPeça('g', 7, new Peão(tabuleiro, Cor.Preto, this));
        lugarPeça('h', 7, new Peão(tabuleiro, Cor.Preto, this)); 
    }
}

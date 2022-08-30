package com.mycompany.noc_mesh;

import java.util.Objects;

/**
 *
 * @author Computação
 */
public class Nodo {

    private String id;
    private Integer qtdPacotes = 0;
    private Integer posicaoX;
    private Integer posicaoY;
    private Nodo nodoDireita;
    private Nodo nodoEsquerda;
    private Nodo nodoCima;
    private Nodo nodoBaixo;

    public Nodo() {
    }

    public Nodo(Integer posicao) {
        this.posicaoX = posicao;
    }

    public Nodo(Integer posicaoX, Integer posicaoY) {
        this.posicaoX = posicaoX;
        this.posicaoY = posicaoY;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getQtdPacotes() {
        return qtdPacotes;
    }

    public void setQtdPacotes(Integer qtdPacotes) {
        this.qtdPacotes = qtdPacotes;
    }

    public Integer getPosicaoX() {
        return posicaoX;
    }

    public void setPosicaoX(Integer posicaoX) {
        this.posicaoX = posicaoX;
    }

    public Integer getPosicaoY() {
        return posicaoY;
    }

    public void setPosicaoY(Integer posicaoY) {
        this.posicaoY = posicaoY;
    }

    public Nodo getNodoDireita() {
        return nodoDireita;
    }

    public void setNodoDireita(Nodo nodoDireita) {
        this.nodoDireita = nodoDireita;
    }

    public Nodo getNodoEsquerda() {
        return nodoEsquerda;
    }

    public void setNodoEsquerda(Nodo nodoEsquerda) {
        this.nodoEsquerda = nodoEsquerda;
    }

    public Nodo getNodoCima() {
        return nodoCima;
    }

    public void setNodoCima(Nodo nodoCima) {
        this.nodoCima = nodoCima;
    }

    public Nodo getNodoBaixo() {
        return nodoBaixo;
    }

    public void setNodoBaixo(Nodo nodoBaixo) {
        this.nodoBaixo = nodoBaixo;
    }

    public void criarMensagem(int pacotes) {
        System.out.println("O nodo " + getIdentificacaoNodo() + " criou a mensagem");
        this.qtdPacotes += pacotes;
    }

    public void enviarMensagem(Nodo nodo) {
        System.out.println("O nodo " + getIdentificacaoNodo() + " enviou a mensagem para o nodo " + nodo.getIdentificacaoNodo());
    }

    public void receberMensagem(Nodo nodo, int pacotes) {
        System.out.println("O nodo " + getIdentificacaoNodo() + " recebeu a mensagem do nodo " + nodo.getIdentificacaoNodo());
        this.qtdPacotes += pacotes;
    }

    public void consumirMensagem() {
        System.out.println("O nodo " + getIdentificacaoNodo() + " consumiu a mensagem");
    }

    public void nodoEhDestino() {
        System.out.println("O nodo " + getIdentificacaoNodo() + " é o destino");
    }

    public void nodoNaoEhDestino() {
        System.out.println("O nodo " + getIdentificacaoNodo() + " não é o destino");
    }

    public String getIdentificacaoNodo() {
        if (this.posicaoY == null) {
            return String.valueOf(this.posicaoX);
        } else {
            return this.posicaoX + "-" + this.posicaoY;
        }
    }

    public boolean isTarget(Integer target) {
        return this.equals(target);
    }

    public Integer getDistanciaAteNodoDireita(Integer target) {
        int count = 0;
        if (!this.equals(target)) {
            Nodo nodo = this.getNodoDireita();
            if (nodo.getPosicaoX().equals(target)) {
                return 1;
            }
            while (!nodo.getPosicaoX().equals(target)) {
                count++;
                nodo = nodo.getNodoDireita();
            }
        }
        return count;
    }

    public Integer getDistanciaAteNodoEsquerda(Integer target) {
        int count = 0;
        if (!this.equals(target)) {
            Nodo nodo = this.getNodoEsquerda();
            if (nodo.getPosicaoX().equals(target)) {
                return 1;
            }
            while (!nodo.getPosicaoX().equals(target)) {
                count++;
                nodo = nodo.getNodoEsquerda();
            }
        }
        return count;
    }

    public String getPercUso(Integer qtdTotalPacotes) {
        Double perc = 100.0 * qtdPacotes / qtdTotalPacotes;
        return String.format("%% %.2f", perc);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.posicaoX);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Nodo other = (Nodo) obj;
        if (!Objects.equals(this.posicaoX, other.posicaoX)) {
            return false;
        }
        if (!Objects.equals(this.posicaoY, other.posicaoY)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Nodo{" + "posicao=" + posicaoX + '}';
    }
}

package com.mycompany.noc_mesh;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Computação
 */
public class Main {

    public static void nocAnel() {
        Scanner lt = new Scanner(System.in);
        Integer qtdNodos;
        do {
            System.out.println("Insira quantos nodos terão (2-10):");
            qtdNodos = lt.nextInt();
        } while (qtdNodos < 2 || qtdNodos > 10);
        Nodo[] nodos = new Nodo[qtdNodos];
        for (int i = 0; i < qtdNodos; i++) {
            Nodo nodo = new Nodo(i);
            if (i > 0) {
                nodo.setNodoEsquerda(nodos[i - 1]);
            }
            if (i == qtdNodos - 1) {
                nodos[0].setNodoEsquerda(nodo);
            }
            nodos[i] = nodo;
        }

        for (int i = 0; i < qtdNodos; i++) {
            if (i == qtdNodos - 1) {
                nodos[i].setNodoDireita(nodos[0]);
            } else {
                nodos[i].setNodoDireita(nodos[i + 1]);
            }
        }

        while (true) {
            System.out.println("Quem é o Source?");
            Integer ltSource = lt.nextInt();
            while (ltSource < 0 || ltSource >= qtdNodos) {
                System.out.println("Não é um nodo válido, escolha um nodo entre 0 e " + (qtdNodos - 1));
                System.out.println("");
                System.out.println("Quem é o source?");
                ltSource = lt.nextInt();
            }

            System.out.println("Quem é o Target?");
            Integer ltTarget = lt.nextInt();
            while (ltTarget < 0 || ltTarget >= qtdNodos) {
                System.out.println("Não é um nodo válido, escolha um nodo entre 0 e " + (qtdNodos - 1));
                System.out.println("Quem é o target?");
                ltTarget = lt.nextInt();
            }

            Nodo source = nodos[ltSource];
            Nodo target = nodos[ltTarget];

            source.criarMensagem();
            if (source.equals(target)) {
                source.nodoEhDestino();
                source.consumirMensagem();
            } else {
                boolean direita = source.getDistanciaAteNodoDireita(target.getPosicaoX()) <= source.getDistanciaAteNodoEsquerda(target.getPosicaoX());
                Nodo nodo;
                if (direita) {
                    nodo = source.getNodoDireita();
                } else {
                    nodo = source.getNodoEsquerda();
                }
                source.enviarMensagem(nodo);
                nodo.receberMensagem(source);

                while (!nodo.equals(target)) {
                    nodo.nodoNaoEhDestino();
                    if (direita) {
                        nodo.enviarMensagem(nodo.getNodoDireita());
                        nodo = nodo.getNodoDireita();
                        nodo.receberMensagem(nodo.getNodoEsquerda());
                    } else {
                        nodo.enviarMensagem(nodo.getNodoEsquerda());
                        nodo = nodo.getNodoEsquerda();
                        nodo.receberMensagem(nodo.getNodoDireita());
                    }
                }
                nodo.nodoEhDestino();
                nodo.consumirMensagem();
                System.out.println("");
            }

        }
    }

    public static void nocMesh(String caminhoJson) throws FileNotFoundException, IOException, ParseException {
        JSONParser parser = new JSONParser();

        Object obj = parser.parse(new FileReader(caminhoJson));
        JSONObject json = (JSONObject) obj;

        JSONObject size = (JSONObject) json.get("Size");
        Integer nodosX = Integer.valueOf(size.get("X").toString());
        Integer nodosY = Integer.valueOf(size.get("Y").toString());

        System.out.println("Tamanho escolhido (X-Y): " + nodosX + " - " + nodosY);

        Nodo[][] nodos = new Nodo[nodosX][nodosY];
        for (int i = 0; i < nodosX; i++) {
            for (int j = 0; j < nodosY; j++) {
                Nodo nodo = new Nodo(i, j);
                nodos[i][j] = nodo;
            }
        }

        for (int i = 0; i < nodosX; i++) {
            for (int j = 0; j < nodosY; j++) {
                Nodo nodo = nodos[i][j];
                if (j > 0) {
                    nodo.setNodoEsquerda(nodos[i][j - 1]);
                }
                if (j < nodosY - 1) {
                    nodo.setNodoDireita(nodos[i][j + 1]);
                }
                if (i > 0) {
                    nodo.setNodoCima(nodos[i - 1][j]);
                }
                if (i < nodosX - 1) {
                    nodo.setNodoBaixo(nodos[i + 1][j]);
                }
            }
        }

        JSONArray packages = (JSONArray) json.get("Packages");
        Iterator<JSONObject> it = packages.iterator();

        while (it.hasNext()) {
            JSONObject cenario = it.next();

            JSONObject jsonSource = (JSONObject) cenario.get("Source");
            Integer sourceX = Integer.valueOf(jsonSource.get("X").toString());
            Integer sourceY = Integer.valueOf(jsonSource.get("Y").toString());

            JSONObject jsonTarget = (JSONObject) cenario.get("Target");
            Integer targetX = Integer.valueOf(jsonTarget.get("X").toString());
            Integer targetY = Integer.valueOf(jsonTarget.get("Y").toString());

            System.out.println("Indo de (X,Y): " + sourceX + ", " + sourceY);
            System.out.println("Para (X,Y): " + targetX + ", " + targetY + "\n");

            Nodo source = nodos[sourceX][sourceY];
            Nodo target = nodos[targetX][targetY];

            source.criarMensagem();
            if (source.equals(target)) {
                source.nodoEhDestino();
                source.consumirMensagem();
            } else {
                Nodo nodoAtual = source;
                if (target.getPosicaoY() > source.getPosicaoY()) {
                    // Direita
                    Nodo nodo = nodoAtual.getNodoDireita();
                    nodoAtual.nodoNaoEhDestino();
                    nodoAtual.enviarMensagem(nodo);
                    nodo.receberMensagem(nodoAtual);
                    while (!nodo.getPosicaoY().equals(target.getPosicaoY())) {
                        nodo.nodoNaoEhDestino();
                        nodo.enviarMensagem(nodo.getNodoDireita());
                        nodo = nodo.getNodoDireita();
                        nodo.receberMensagem(nodo.getNodoEsquerda());
                    }
                    nodoAtual = nodo;
                } else if (target.getPosicaoY() < source.getPosicaoY()) {
                    // Esquerda
                    Nodo nodo = source.getNodoEsquerda();
                    nodoAtual.nodoNaoEhDestino();
                    nodoAtual.enviarMensagem(nodo);
                    nodo.receberMensagem(nodoAtual);
                    while (!nodo.getPosicaoY().equals(target.getPosicaoY())) {
                        nodo.nodoNaoEhDestino();
                        nodo.enviarMensagem(nodo.getNodoEsquerda());
                        nodo = nodo.getNodoEsquerda();
                        nodo.receberMensagem(nodo.getNodoDireita());
                    }
                    nodoAtual = nodo;
                }

                if (target.getPosicaoX() < source.getPosicaoX()) {
                    // Cima
                    Nodo nodo = nodoAtual.getNodoCima();
                    nodoAtual.nodoNaoEhDestino();
                    nodoAtual.enviarMensagem(nodo);
                    nodo.receberMensagem(nodoAtual);
                    while (!nodo.getPosicaoX().equals(target.getPosicaoX())) {
                        nodo.nodoNaoEhDestino();
                        nodo.enviarMensagem(nodo.getNodoCima());
                        nodo = nodo.getNodoCima();
                        nodo.receberMensagem(nodo.getNodoBaixo());
                    }
                    nodoAtual = nodo;
                } else if (target.getPosicaoX() > source.getPosicaoX()) {
                    // Baixo
                    Nodo nodo = nodoAtual.getNodoBaixo();
                    if (nodo == null) {
                        System.out.println("Nodo: " + nodo.getIdentificacaoNodo());
                    }
                    nodoAtual.nodoNaoEhDestino();
                    nodoAtual.enviarMensagem(nodo);
                    nodo.receberMensagem(nodoAtual);
                    while (!nodo.getPosicaoX().equals(target.getPosicaoX())) {
                        nodo.nodoNaoEhDestino();
                        nodo.enviarMensagem(nodo.getNodoBaixo());
                        nodo = nodo.getNodoBaixo();
                        nodo.receberMensagem(nodo.getNodoCima());
                    }
                    nodoAtual = nodo;
                }

                nodoAtual.nodoEhDestino();
                nodoAtual.consumirMensagem();
                System.out.println("---------------------------------------------------------------");
            }

        }
    }

    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {

        try {
            nocMesh("C:\\Users\\Eduar\\OneDrive\\Documentos\\NetBeansProjects\\CenariosMesh.json");
        } catch (IOException | ParseException e) {

        }

    }

}

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

            source.criarMensagem(0);
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
                nodo.receberMensagem(source, 0);

                while (!nodo.equals(target)) {
                    nodo.nodoNaoEhDestino();
                    if (direita) {
                        nodo.enviarMensagem(nodo.getNodoDireita());
                        nodo = nodo.getNodoDireita();
                        nodo.receberMensagem(nodo.getNodoEsquerda(), 0);
                    } else {
                        nodo.enviarMensagem(nodo.getNodoEsquerda());
                        nodo = nodo.getNodoEsquerda();
                        nodo.receberMensagem(nodo.getNodoDireita(), 0);
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
        JSONArray apps = (JSONArray) json.get("apps");
        Iterator<JSONObject> app = apps.iterator();

        while (app.hasNext()) {
            JSONObject aplicacao = app.next();
            System.out.println("Aplicação " + aplicacao.get("aplicacao").toString());
            Integer nodosX = Integer.valueOf(aplicacao.get("altura").toString());
            Integer nodosY = Integer.valueOf(aplicacao.get("largura").toString());

            Integer totalPacotes = 0;

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

            JSONArray tarefas = (JSONArray) aplicacao.get("tarefas");
            Iterator<JSONObject> tarefaIt = tarefas.iterator();

            while (tarefaIt.hasNext()) {
                JSONObject tarefa = (JSONObject) tarefaIt.next();
                String tarefaId = tarefa.get("tarefa_id").toString();
                Integer tarefaX = Integer.valueOf(tarefa.get("tarefa_x").toString());
                Integer tarefaY = Integer.valueOf(tarefa.get("tarefa_y").toString());

                nodos[tarefaX][tarefaY].setId(tarefaId);
            }

            System.out.println("Mapeamento");
            for (int i = 0; i < nodosX; i++) {
                for (int j = 0; j < nodosY; j++) {
                    Nodo nd = nodos[i][j];
                    if (nd.getId() == null || nd.getId().isEmpty()) {
                        System.out.print("0 ");
                    } else {
                        System.out.print(nd.getId() + " ");
                    }
                }
                System.out.println("");
            }
            System.out.println("");

            JSONArray grafoTarefas = (JSONArray) aplicacao.get("grafo_tarefas");
            Iterator<JSONObject> grafoIt = grafoTarefas.iterator();

            while (grafoIt.hasNext()) {
                JSONObject grafo = grafoIt.next();

                String tarefaOrigem = grafo.get("tarefa_origem").toString();
                String tarefaDestino = grafo.get("tarefa_destino").toString();
                Integer qtdPacotes = Integer.valueOf(grafo.get("quantidade_pacotes").toString());
                totalPacotes += qtdPacotes;

                Nodo source = getNodoPorId(nodos, nodosX, nodosY, tarefaOrigem);
                Nodo target = getNodoPorId(nodos, nodosX, nodosY, tarefaDestino);

                System.out.println("Indo de " + tarefaOrigem + " para " + tarefaDestino + " com " + qtdPacotes + " pacotes");

                source.criarMensagem(qtdPacotes);
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
                        nodo.receberMensagem(nodoAtual, qtdPacotes);
                        while (!nodo.getPosicaoY().equals(target.getPosicaoY())) {
                            nodo.nodoNaoEhDestino();
                            nodo.enviarMensagem(nodo.getNodoDireita());
                            nodo = nodo.getNodoDireita();
                            nodo.receberMensagem(nodo.getNodoEsquerda(), qtdPacotes);
                        }
                        nodoAtual = nodo;
                    } else if (target.getPosicaoY() < source.getPosicaoY()) {
                        // Esquerda
                        Nodo nodo = source.getNodoEsquerda();
                        nodoAtual.nodoNaoEhDestino();
                        nodoAtual.enviarMensagem(nodo);
                        nodo.receberMensagem(nodoAtual, qtdPacotes);
                        while (!nodo.getPosicaoY().equals(target.getPosicaoY())) {
                            nodo.nodoNaoEhDestino();
                            nodo.enviarMensagem(nodo.getNodoEsquerda());
                            nodo = nodo.getNodoEsquerda();
                            nodo.receberMensagem(nodo.getNodoDireita(), qtdPacotes);
                        }
                        nodoAtual = nodo;
                    }

                    if (target.getPosicaoX() < source.getPosicaoX()) {
                        // Cima
                        Nodo nodo = nodoAtual.getNodoCima();
                        nodoAtual.nodoNaoEhDestino();
                        nodoAtual.enviarMensagem(nodo);
                        nodo.receberMensagem(nodoAtual, qtdPacotes);
                        while (!nodo.getPosicaoX().equals(target.getPosicaoX())) {
                            nodo.nodoNaoEhDestino();
                            nodo.enviarMensagem(nodo.getNodoCima());
                            nodo = nodo.getNodoCima();
                            nodo.receberMensagem(nodo.getNodoBaixo(), qtdPacotes);
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
                        nodo.receberMensagem(nodoAtual, qtdPacotes);
                        while (!nodo.getPosicaoX().equals(target.getPosicaoX())) {
                            nodo.nodoNaoEhDestino();
                            nodo.enviarMensagem(nodo.getNodoBaixo());
                            nodo = nodo.getNodoBaixo();
                            nodo.receberMensagem(nodo.getNodoCima(), qtdPacotes);
                        }
                        nodoAtual = nodo;
                    }

                    nodoAtual.nodoEhDestino();
                    nodoAtual.consumirMensagem();
                    for (int i = 0; i < nodosX; i++) {
                        for (int j = 0; j < nodosY; j++) {
                            System.out.print(nodos[i][j].getQtdPacotes() + " ");
                        }
                        System.out.println("");
                    }
                    System.out.println("---------------------------------------------------------------");
                }
            }
            System.out.println("Mapeamento de uso dos nodos:");
            for (int i = 0; i < nodosX; i++) {
                for (int j = 0; j < nodosY; j++) {
                    System.out.print(nodos[i][j].getPercUso(totalPacotes) + " ");
                }
                System.out.println("");
            }
            System.out.println("");
            System.out.println("=========================================================================");
        }
    }

    public static Nodo getNodoPorId(Nodo[][] nodos, int x, int y, String id) {
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                Nodo nodo = nodos[i][j];
                if (id.equals(nodo.getId())) {
                    return nodo;
                }
            }
        }
        return null;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {

        try {
            nocMesh("C:\\Users\\Eduar\\OneDrive\\Documentos\\NetBeansProjects\\mpsoc.json");
        } catch (IOException | ParseException e) {

        }

    }

}

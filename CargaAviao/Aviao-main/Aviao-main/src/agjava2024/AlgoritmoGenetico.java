package agjava2024;

import java.io.*;
import java.util.*;

public class AlgoritmoGenetico {
    private int tamPopulacao;
    private int tamCromossomo = 0;
    private double capacidade;
    private int probMutacao;
    private int qtdeCruzamentos;
    private int numeroGeracoes;
    private double larguraMaxima;
    private double alturaMaxima;
    private double profundidadeMaxima;
    private ArrayList<Produto> produtos = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> populacao = new ArrayList<>();
    private ArrayList<Integer> roletaVirtual = new ArrayList<>();

    public AlgoritmoGenetico(int tamanhoPopulacao, double capacidadeMochila,
                             int probabilidadeMutacao, int qtdeCruzamentos,
                             int numGeracoes, double larguraMaxima, double alturaMaxima, double profundidadeMaxima) {
        this.tamPopulacao = tamanhoPopulacao;
        this.capacidade = capacidadeMochila;
        this.probMutacao = probabilidadeMutacao;
        this.qtdeCruzamentos = qtdeCruzamentos;
        this.numeroGeracoes = numGeracoes;
        this.larguraMaxima = larguraMaxima;
        this.alturaMaxima = alturaMaxima;
        this.profundidadeMaxima = profundidadeMaxima;
    }

    public void executar() {
        this.criarPopulacao();
        // executar por gerações
        for (int i = 0; i < this.numeroGeracoes; i++) {
            System.out.println("Geracao: " + i);
            mostraPopulacao();
            operadoresGeneticos();
            novaPopulacao();
        }
        mostrarMochila(this.populacao.get(obterMelhor()));
    }

    public void mostraPopulacao() {
        for (int i = 0; i < this.tamPopulacao; i++) {
            System.out.println("Cromossomo " + i + ":" + populacao.get(i));
            System.out.println("Avaliacao:" + fitness(populacao.get(i)));
        }
    }

    public void carregaArquivo(String fileName) {
        String csvFile = fileName;
        String line = "";
        String[] produto = null;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                produto = line.split(",");
                Produto novoProduto = new Produto();
                novoProduto.setDescricao(produto[0]);
                novoProduto.setPeso(Double.parseDouble(produto[1]));
                novoProduto.setLargura(Double.parseDouble(produto[2]));
                novoProduto.setAltura(Double.parseDouble(produto[3]));
                novoProduto.setProfundidade(Double.parseDouble(produto[4]));
                produtos.add(novoProduto);
                System.out.println(novoProduto);
                this.tamCromossomo++;
            }// fim percurso no arquivo

            System.out.println("Tamanho do cromossomo:" + this.tamCromossomo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //---------------------
    private ArrayList<Integer> criarCromossomo() {
        ArrayList<Integer> novoCromossomo = new ArrayList<>();
        for (int i = 0; i < this.tamCromossomo; i++) {
            if (Math.random() < 0.6)
                novoCromossomo.add(0);
            else
                novoCromossomo.add(1);
        }// fim for
        return novoCromossomo;
    }

    //--------------------------   
    private void criarPopulacao() {
        for (int i = 0; i < this.tamPopulacao; i++)
            this.populacao.add(criarCromossomo());
    }

    //----------------------------------
    private double fitness(ArrayList<Integer> cromossomo) {
        double pesoTotal = 0, volumeTotal = 0, valorTotal = 0;
        for (int i = 0; i < this.tamCromossomo; i++) {
            if (cromossomo.get(i) == 1) {
                Produto produto = produtos.get(i);
                if (produto.getLargura() > this.larguraMaxima || produto.getAltura() > this.alturaMaxima || produto.getProfundidade() > this.profundidadeMaxima) {
                    return 0; // Penaliza soluções que excedem dimensões individuais
                }
                pesoTotal += produto.getPeso();
                volumeTotal += produto.getVolume();
                valorTotal += produto.getVolume(); // Benefício é dado pelo volume carregado
            }// fim if teste se leva
        }
        if (pesoTotal <= this.capacidade && volumeTotal <= this.larguraMaxima * this.alturaMaxima * this.profundidadeMaxima)
            return valorTotal;
        else
            return 0;
    }

    private void gerarRoleta() {
        ArrayList<Double> fitnessIndividuos = new ArrayList<>();
        double totalFitness = 0;
        for (int i = 0; i < this.tamPopulacao; i++) {
            fitnessIndividuos.add(i, fitness(this.populacao.get(i)));
            totalFitness += fitnessIndividuos.get(i);
        }
        System.out.println("Soma total fitness:" + totalFitness);
        for (int i = 0; i < this.tamPopulacao; i++) {
            double qtdPosicoes = (fitnessIndividuos.get(i) / totalFitness) * 1000;
            for (int j = 0; j <= qtdPosicoes; j++)
                roletaVirtual.add(i);
        }// fim for i
    }// fim gerarRoleta

    private int roleta() {
        Random r = new Random();
        int selecionado = r.nextInt(roletaVirtual.size());
        return roletaVirtual.get(selecionado);
    }// fim roleta

    private ArrayList<ArrayList<Integer>> cruzamento() {
        ArrayList<Integer> filho1 = new ArrayList<>();
        ArrayList<Integer> filho2 = new ArrayList<>();
        ArrayList<ArrayList<Integer>> filhos = new ArrayList<>();
        ArrayList<Integer> pai1, pai2;
        int indice_pai1, indice_pai2;
        indice_pai1 = roleta(); // selecionados
        indice_pai2 = roleta();
        pai1 = populacao.get(indice_pai1);
        pai2 = populacao.get(indice_pai2);
        Random r = new Random();
        int pos = r.nextInt(this.tamCromossomo); // ponto de corte
        for (int i = 0; i <= pos; i++) {
            filho1.add(pai1.get(i));
            filho2.add(pai2.get(i));
        }
        for (int i = pos + 1; i < this.tamCromossomo; i++) {
            filho1.add(pai2.get(i));
            filho2.add(pai1.get(i));
        }
        filhos.add(filho1);
        filhos.add(filho2);
        return filhos;
    }

    private void mutacao(ArrayList<Integer> filho) {
        Random r = new Random();
        int v = r.nextInt(100);
        if (v < this.probMutacao) {
            int ponto = r.nextInt(this.tamCromossomo);
            filho.set(ponto, filho.get(ponto) == 1 ? 0 : 1);

            int ponto2 = r.nextInt(this.tamCromossomo);
            filho.set(ponto2, filho.get(ponto2) == 1 ? 0 : 1);

            System.out.println("Ocorreu mutação!");
        }// fim if mutacao     
    }

    private void operadoresGeneticos() {
        ArrayList<Integer> f1, f2;
        ArrayList<ArrayList<Integer>> filhos;
        gerarRoleta();
        for (int i = 0; i < this.qtdeCruzamentos; i++) {
            filhos = cruzamento();
            f1 = filhos.get(0);
            f2 = filhos.get(1);
            mutacao(f1);
            mutacao(f2);
            populacao.add(f1);
            populacao.add(f2);
        }
    }

    private void novaPopulacao() {
        ArrayList<ArrayList<Integer>> novaPopulacao = new ArrayList<>();
        ArrayList<Double> fitnessIndividuos = new ArrayList<>();
        
        // Avaliar fitness dos indivíduos na população atual
        for (int i = 0; i < this.populacao.size(); i++) {
            fitnessIndividuos.add(fitness(this.populacao.get(i)));
        }
        
        // Fazer uma cópia dos valores de fitness para ordenação
        Double[] copiaFitness = new Double[fitnessIndividuos.size()];
        copiaFitness = fitnessIndividuos.toArray(copiaFitness);
        Arrays.sort(copiaFitness, Collections.reverseOrder());
        
        // Selecionar os melhores indivíduos
        for (int i = 0; i < this.tamPopulacao; i++) {
            double melhorFitness = copiaFitness[i];
            int indice = fitnessIndividuos.indexOf(melhorFitness);
            novaPopulacao.add(new ArrayList<>(this.populacao.get(indice))); // Adiciona uma cópia do cromossomo
            fitnessIndividuos.set(indice, Double.NEGATIVE_INFINITY); // Evita selecionar o mesmo indivíduo novamente
        }
        
        // Atualizar a população com a nova população gerada
        this.populacao = novaPopulacao;
    }

    private int obterMelhor() {
        double melhorAvaliacao = 0;
        int indiceMelhor = 0;
        double avaliacaoIndividuo;
        for (int i = 0; i < this.populacao.size(); i++) {
            avaliacaoIndividuo = fitness(this.populacao.get(i));
            if (avaliacaoIndividuo > melhorAvaliacao) {
                melhorAvaliacao = avaliacaoIndividuo;
                indiceMelhor = i;
            }
        }
        return indiceMelhor;
    }

    private void mostrarMochila(ArrayList<Integer> mochila) {
        double pesoTotal = 0, volumeTotal = 0;
        System.out.println("Produtos na mochila:");
        for (int i = 0; i < mochila.size(); i++) {
            if (mochila.get(i) == 1) {
                Produto produto = produtos.get(i);
                pesoTotal += produto.getPeso();
                volumeTotal += produto.getVolume();
                System.out.println(produto.getDescricao() + " - Peso: " + produto.getPeso() + " - Volume: " + produto.getVolume());
            }
        }
        System.out.println("Peso total da mochila: " + pesoTotal);
        System.out.println("Volume total da mochila: " + volumeTotal);
    }
}

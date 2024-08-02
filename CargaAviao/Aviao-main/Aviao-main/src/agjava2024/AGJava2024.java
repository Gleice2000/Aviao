package agjava2024;

public class AGJava2024 {

    public static void main(String[] args) {
        int populacao = 20;
        double limitePeso = 400;
        int probabilidadeMutacao = 5; // 5%
        int qtdeCruzamentos = 5;
        int numeroGeracoes = 10;
        double larguraMaxima = 300;
        double alturaMaxima = 300;
        double profundidadeMaxima = 400;

        AlgoritmoGenetico meuAg = new AlgoritmoGenetico(populacao, limitePeso, probabilidadeMutacao, qtdeCruzamentos, numeroGeracoes, larguraMaxima, alturaMaxima, profundidadeMaxima);
        meuAg.carregaArquivo("carga_aviao.csv");
        meuAg.executar();
    }
}

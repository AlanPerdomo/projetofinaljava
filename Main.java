import java.util.Scanner;

import Classes.Conta;
import Classes.Pessoas;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        int opcao;
        int opcao2;

        while (true) {
            try {
                exibirMenu();
                String input = scanner.nextLine();
                opcao = Integer.parseInt(input);
                System.out.println("=====================================");

                switch (opcao) {
                    case 0:
                        System.out.println("Programa encerrado.");
                        scanner.close();
                        return;
                    case 1:
                        System.out.println("\nCADASTRAR PESSOA\n");
                        Pessoas.cadastrarPessoa(scanner);
                        break;
                    case 2:
                        System.out.println("\nCRIAR CONTA\n");
                        Conta.criarConta(scanner);
                        break;
                    case 3:
                        System.out.println("Escolha uma opção:\n");
                        System.out.println("1 - Listar Pessoas");
                        System.out.println("2 - Listar Contas");
                        System.out.print("\nOpção: ");

                        input = scanner.nextLine();
                        opcao2 = Integer.parseInt(input);
                        System.out.println("=====================================");

                        switch (opcao2) {
                            case 1:
                                Pessoas.listarPessoas();
                                break;
                            case 2:
                                Conta.listarContas();
                                break;
                            default:
                                System.out.println("\nOpção inválida. Tente novamente.");
                                break;
                        }
                        break;
                    case 4:
                        System.out.println("Escolha uma opção:\n");
                        System.out.println("1 - Deletar Pessoa");
                        System.out.println("2 - Deletar Conta");
                        System.out.print("\nOpção: ");

                        input = scanner.nextLine();
                        opcao2 = Integer.parseInt(input);
                        System.out.println("=====================================");

                        switch (opcao2) {
                            case 1:
                                System.out.println("\nDELETAR PESSOA\n");
                                Pessoas.deletarPessoa(scanner);
                                break;
                            case 2:
                                System.out.println("\nDELETAR CONTA\n");
                                Conta.deletarConta(scanner);
                                break;
                            default:
                                System.out.println("\nOpção inválida. Tente novamente.");
                                break;
                        }
                        break;
                    case 5:
                        System.out.println("\nDEPOSITO\n");
                        Conta.realizarDeposito(scanner);
                        break;
                    case 6:
                        System.out.println("\nSAQUE\n");
                        Conta.realizarSaque(scanner);
                        break;
                    default:
                        System.out.println("Opção inválida. Tente novamente.\n");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Opção inválida. Tente novamente.");
            } catch (Exception e) {
                System.out.println("Ocorreu um erro. Tente novamente.");
            }
        }
    }

    public static void exibirMenu() {
        System.out.println("\n=====================");
        System.out.println("----- Bem-vindo -----");
        System.out.println("=====================\n");
        System.out.println("Escolha uma opção:\n");
        System.out.println("1 - Cadastrar Pessoa");
        System.out.println("2 - Criar Conta");
        System.out.println("3 - Listar Pessoas ou Contas");
        System.out.println("4 - Deletar Pessoa ou Conta");
        System.out.println("5 - Depositar");
        System.out.println("6 - Sacar");
        System.out.println("0 - Sair");
        System.out.print("\nOpção: ");
    }

}
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Data.DbContext;

public class Main {
    private static List<Pessoas> listaPessoas = new ArrayList<>();
    private static List<Conta> listaContas = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);
    private static int ultimoNumeroConta = 0;

    public static void main(String[] args) {
        exibirMenu();

        int opcao;

        try {
            opcao = scanner.nextInt();
            scanner.nextLine(); // Limpar o buffer do scanner
        } catch (Exception e) {
            System.out.println("Opção inválida. Tente novamente.");
            return;
        }

        while (opcao != 0) {
            try {
                switch (opcao) {
                    case 1:
                        cadastrarPessoa();
                        break;
                    case 2:
                        listarPessoas();
                        break;
                    case 3:
                        criarConta();
                        break;
                    case 4:
                        listarContas();
                        break;
                    case 5:
                        realizarDeposito();
                        break;
                    case 6:
                        realizarSaque();
                        break;
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                        break;
                }
            } catch (Exception e) {
                System.out.println("Ocorreu um erro. Tente novamente.");
            }

            exibirMenu();

            try {
                opcao = scanner.nextInt();
                scanner.nextLine(); // Limpar o buffer do scanner
            } catch (Exception e) {
                System.out.println("Opção inválida. Tente novamente.");
                opcao = -1; // Definir opção inválida para continuar o loop
            }
        }

        System.out.println("Programa encerrado.");
        scanner.close();
    }

    public static void exibirMenu() {
        System.out.println("\n----- Bem-vindo -----\n");
        System.out.println("Escolha uma opção:");
        System.out.println("1 - Cadastrar Pessoa");
        System.out.println("2 - Listar Pessoas");
        System.out.println("3 - Criar Conta");
        System.out.println("4 - Listar Contas");
        System.out.println("5 - Depositar");
        System.out.println("6 - Sacar");
        System.out.println("0 - Sair");
        System.out.print("Opção: ");
    }

    public static void cadastrarPessoa() {
        System.out.print("Digite o nome da pessoa: ");
        String nome = scanner.nextLine();

        if (!validarNome(nome)) {
            System.out.println("Nome inválido. O nome deve conter apenas letras e espaços.");
            return;
        }

        System.out.print("Digite o CPF da pessoa: ");
        String cpf = scanner.nextLine();

        if (!validarCPF(cpf)) {
            System.out.println("CPF inválido. O CPF deve conter 11 dígitos numéricos.");
            return;
        }

        Pessoas pessoa = new Pessoas(nome, cpf);
        listaPessoas.add(pessoa);
        
        //DbContext database = new DbContext();
        //try {
        //    database.conectarBanco();
        //    boolean pessoaExistente = verificarPessoaExistente(database, cpf);
        //    if (pessoaExistente) {
        //        System.out.println("CPF já cadastrado. Não é possível cadastrar a mesma pessoa novamente.");
        //    } else {
        //        boolean statusQuery = database.executarUpdateSql(
        //                "INSERT INTO public.pessoas(nome, cpf) VALUES ('" + nome + "', '" + cpf + "')");
        //        if (statusQuery) {
        //            System.out.println("'" + nome + "' foi cadastrado(a)!");
        //        }
        //    }
        //    database.desconectarBanco();
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
    }

    public static boolean verificarPessoaExistente(DbContext database, String cpf) throws SQLException {
        String query = "SELECT COUNT(*) FROM public.pessoas WHERE cpf = '" + cpf + "'";
        ResultSet resultSet = database.executarQuerySql(query);

        if (resultSet.next()) {
            int count = resultSet.getInt(1);
            return count > 0;
        }

        return false;
    }

    public static void listarPessoas() {
        if (listaPessoas.isEmpty()) {
            System.out.println("Nenhuma pessoa cadastrada.");
        } else {
            System.out.println("Lista de Pessoas Cadastradas:");
            for (Pessoas pessoa : listaPessoas) {
                System.out.println("Nome: " + pessoa.getNome() + " | CPF: " + pessoa.getCpf());
            }
        }
    }

    public static void criarConta() {
        System.out.print("Digite o CPF da pessoa: ");
        String cpf = scanner.nextLine();

        Pessoas pessoa = buscarPessoaPorCPF(cpf);
        if (pessoa == null) {
            System.out.println("CPF não encontrado. Cadastre a pessoa antes de criar a conta.");
            return;
        }

        System.out.println("Escolha o tipo de conta:");
        System.out.println("1 - Conta Corrente");
        System.out.println("2 - Conta Poupança");
        System.out.print("Opção: ");
        int opcao;

        try {
            opcao = scanner.nextInt();
            scanner.nextLine(); // Limpar o buffer do scanner
        } catch (Exception e) {
            System.out.println("Opção inválida. A conta não foi criada.");
            return;
        }

        switch (opcao) {
            case 1:
                criarContaCorrente(pessoa);
                break;
            case 2:
                criarContaPoupanca(pessoa);
                break;
            default:
                System.out.println("Opção inválida. A conta não foi criada.");
                break;
        }
    }

    public static Pessoas buscarPessoaPorCPF(String cpf) {
        for (Pessoas pessoa : listaPessoas) {
            if (pessoa.getCpf().equals(cpf)) {
                return pessoa;
            }
        }
        return null;
    }

    public static void criarContaCorrente(Pessoas pessoa) {
        int numeroConta = gerarNumeroConta();

        if (verificarNumeroContaUnico(numeroConta)) {
            Conta contaCorrente = new ContaCorrente(numeroConta, pessoa.getCpf(), 0.0);
            listaContas.add(contaCorrente);

            System.out.println("Conta corrente criada com sucesso! Número da conta: " + numeroConta);
        } else {
            System.out.println("Erro ao criar a conta. Número da conta não é único.");
        }
    }

    public static void criarContaPoupanca(Pessoas pessoa) {
        int numeroConta = gerarNumeroConta();

        if (verificarNumeroContaUnico(numeroConta)) {
            Conta contaPoupanca = new ContaPoupanca(numeroConta, pessoa.getCpf(), 0.0);
            listaContas.add(contaPoupanca);

            System.out.println("Conta poupança criada com sucesso! Número da conta: " + numeroConta);
        } else {
            System.out.println("Erro ao criar a conta. Número da conta não é único.");
        }
    }

    public static boolean verificarNumeroContaUnico(int numeroConta) {
        for (Conta conta : listaContas) {
            if (conta.getNumeroConta() == numeroConta) {
                return false;
            }
        }
        return true;
    }

    public static int gerarNumeroConta() {
        return ++ultimoNumeroConta;
    }

    public static boolean validarNome(String nome) {
        // Verifica se o nome contém apenas letras e espaços
        return nome.matches("[a-zA-Z\\s]+");
    }

    public static boolean validarCPF(String cpf) {
        // Remove caracteres não numéricos do CPF
        cpf = cpf.replaceAll("\\D+", "");

        // Verifica se o CPF possui 11 dígitos numéricos
        return cpf.matches("\\d{11}");
    }

    public static void realizarDeposito() {
        System.out.print("Digite o número da conta: ");
        int numeroConta;

        try {
            numeroConta = scanner.nextInt();
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("Número de conta inválido. Tente novamente.");
            return;
        }

        Conta conta = buscarContaPorNumero(numeroConta);
        if (conta == null) {
            System.out.println("Conta não encontrada. Tente novamente.");
            return;
        }

        System.out.print("Digite o valor a ser depositado: R$");
        double valor;

        try {
            valor = scanner.nextDouble();
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("Valor inválido. Tente novamente.");
            return;
        }

        conta.depositar(valor);
    }

    public static Conta buscarContaPorNumero(int numeroConta) {
        for (Conta conta : listaContas) {
            if (conta.getNumeroConta() == numeroConta) {
                return conta;
            }
        }
        return null;
    }

    public static void listarContas() {
        if (listaContas.isEmpty()) {
            System.out.println("Nenhuma conta cadastrada.");
        } else {
            System.out.println("Lista de Contas Cadastradas:");
            for (Conta conta : listaContas) {
                System.out.println("Número da Conta: " + conta.getNumeroConta());
                System.out.println("CPF da Pessoa: " + conta.getCpfPessoa());
                System.out.println("Saldo: R$ " + conta.getSaldo());

                if (conta instanceof ContaCorrente) {
                    System.out.println("Tipo de Conta: Conta Corrente");
                } else if (conta instanceof ContaPoupanca) {
                    System.out.println("Tipo de Conta: Conta Poupança");
                }

                System.out.println("--------------------------");
            }
        }
    }

    public static void realizarSaque() {
        System.out.print("Digite o número da conta: ");
        int numeroConta;

        try {
            numeroConta = scanner.nextInt();
            scanner.nextLine(); // Limpar o buffer do scanner
        } catch (Exception e) {
            System.out.println("Número de conta inválido. Tente novamente.");
            return;
        }

        Conta conta = buscarContaPorNumero(numeroConta);
        if (conta == null) {
            System.out.println("Conta não encontrada. Tente novamente.");
            return;
        }

        System.out.print("Digite o valor a ser sacado: R$");
        double valor;

        try {
            valor = scanner.nextDouble();
            scanner.nextLine(); // Limpar o buffer do scanner
        } catch (Exception e) {
            System.out.println("Valor inválido. Tente novamente.");
            return;
        }

        if (conta instanceof ContaCorrente) {
            ContaCorrente contaCorrente = (ContaCorrente) conta;
            if (!contaCorrente.sacar(valor)) {
                System.out.println("Saldo insuficiente para realizar o saque.");
                return;
            }
        } else {
            if (!conta.sacar(valor)) {
                System.out.println("Saldo insuficiente para realizar o saque.");
                return;
            }
        }

        System.out.println("Saque realizado com sucesso!");
    }
}

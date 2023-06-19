import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import Classes.Conta;
import Classes.ContaCorrente;
import Classes.ContaPoupanca;
import Classes.Pessoas;
import Data.DbContext;

public class Main {
    private static List<Pessoas> listaPessoas = new ArrayList<>();
    private static List<Conta> listaContas = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

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

        DbContext database = new DbContext();

        try {
            database.conectarBanco();
            boolean pessoaExistente = verificarPessoaExistente(database, cpf);
            if (pessoaExistente) {
                System.out.println("CPF já cadastrado. Não é possível cadastrar a mesma pessoa novamente.");
            } else {
                boolean statusQuery = database.executarUpdateSql(
                        "INSERT INTO public.pessoas(nome, cpf) VALUES ('" + nome + "', '" + cpf + "')");
                if (statusQuery) {
                    System.out.println("'" + nome + "' foi cadastrado(a)!");
                }
            }
            database.desconectarBanco();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        DbContext database = new DbContext();

        try {
            database.conectarBanco();
            ResultSet resultSet = database.executarQuerySql("SELECT * FROM public.pessoas");

            if (!resultSet.next()) {
                System.out.println("Nenhuma pessoa cadastrada.");
            } else {
                System.out.println("Lista de Pessoas Cadastradas:");
                do {
                    String nome = resultSet.getString("nome");
                    String cpf = resultSet.getString("cpf");
                    System.out.println("Nome: " + nome + " | CPF: " + cpf);
                } while (resultSet.next());
            }

            database.desconectarBanco();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void criarConta() {
        System.out.print("Digite o CPF da pessoa: ");
        String cpf = scanner.nextLine();

        DbContext database = new DbContext();

        try {
            database.conectarBanco();

            // Verificar se o CPF está cadastrado no banco de dados
            boolean pessoaExistente = verificarPessoaExistente(database, cpf);
            if (!pessoaExistente) {
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

            // Gerar um número de conta único
            String numeroConta = gerarNumeroContaUnico();

            boolean statusQuery;
            switch (opcao) {
                case 1:
                    statusQuery = database.executarUpdateSql(
                            "INSERT INTO public.contas(numeroconta, cpf, saldo, tipo) VALUES ('" + numeroConta + "', '"
                                    + cpf + "', 0, 'Corrente')");
                    if (statusQuery) {
                        System.out.println("Conta corrente criada com sucesso!");
                    }
                    break;
                case 2:
                    statusQuery = database.executarUpdateSql(
                            "INSERT INTO public.contas(numeroconta, cpf, saldo, tipo) VALUES ('" + numeroConta + "', '"
                                    + cpf + "', 0, 'Poupança')");
                    if (statusQuery) {
                        System.out.println("Conta poupança criada com sucesso!");
                    }
                    break;
                default:
                    System.out.println("Opção inválida. A conta não foi criada.");
                    break;
            }

            database.desconectarBanco();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String gerarNumeroContaUnico() {
        // Gere um número de conta aleatório com 8 dígitos
        Random random = new Random();
        int numeroConta = random.nextInt(90000000) + 10000000;

        // Verifique se o número de conta já existe no banco de dados
        DbContext database = new DbContext();
        try {
            database.conectarBanco();
            ResultSet resultSet = database
                    .executarQuerySql("SELECT * FROM public.contas WHERE numeroconta = '" + numeroConta + "'");

            // Se o número de conta já existe, gere um novo número até encontrar um único
            while (resultSet.next()) {
                numeroConta = random.nextInt(90000000) + 10000000;
                resultSet = database
                        .executarQuerySql("SELECT * FROM public.contas WHERE numeroconta = '" + numeroConta + "'");
            }

            // Feche o ResultSet após o uso
            resultSet.close();

            database.desconectarBanco();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return String.valueOf(numeroConta);
    }

    public static Pessoas buscarPessoaPorCPF(String cpf) {
        for (Pessoas pessoa : listaPessoas) {
            if (pessoa.getCpf().equals(cpf)) {
                return pessoa;
            }
        }
        return null;
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

        DbContext database = new DbContext();

        try {
            database.conectarBanco();

            ResultSet resultSet = database
                    .executarQuerySql("SELECT * FROM public.contas WHERE numeroconta = '" + numeroConta + "'");

            if (!resultSet.next()) {
                System.out.println("Conta não encontrada. Tente novamente.");
                return;
            }

            String cpf = resultSet.getString("cpf");
            String tipoConta = resultSet.getString("tipo");
            double saldo = resultSet.getDouble("saldo");

            resultSet.close();

            Conta conta;
            if (tipoConta.equalsIgnoreCase("Corrente")) {
                conta = new ContaCorrente(numeroConta, cpf, saldo);
            } else if (tipoConta.equalsIgnoreCase("Poupança")) {
                conta = new ContaPoupanca(numeroConta, cpf, saldo);
            } else {
                System.out.println("Tipo de conta inválido. Tente novamente.");
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

            // Atualizar o saldo da conta no banco de dados
            boolean statusQuery = database.executarUpdateSql("UPDATE public.contas SET saldo = " + conta.getSaldo()
                    + " WHERE numeroconta = '" + numeroConta + "'");
            if (statusQuery) {
                System.out.println("Depósito de R$" + valor + " realizado. Novo saldo: R$ "
                        + String.format("%.2f", conta.getSaldo()));
            }

            database.desconectarBanco();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        DbContext database = new DbContext();

        try {
            database.conectarBanco();

            ResultSet resultSet = database.executarQuerySql("SELECT * FROM public.contas ORDER BY cpf");

            if (!resultSet.next()) {
                System.out.println("Nenhuma conta cadastrada.");
            } else {
                System.out.println("Lista de Contas Cadastradas:");

                String cpfAnterior = "";
                String nomeAnterior = "";

                do {
                    String numeroConta = resultSet.getString("numeroconta");
                    String cpfAtual = resultSet.getString("cpf");
                    String nomeAtual = "";

                    // Fetch the name of the person from the database based on the CPF
                    ResultSet resultSetPessoa = database
                            .executarQuerySql("SELECT nome FROM public.pessoas WHERE cpf = '" + cpfAtual + "'");
                    if (resultSetPessoa.next()) {
                        nomeAtual = resultSetPessoa.getString("nome");
                    }
                    resultSetPessoa.close();

                    if (!cpfAtual.equals(cpfAnterior)) {
                        cpfAnterior = cpfAtual;
                        nomeAnterior = nomeAtual;
                        System.out.println("CPF: " + cpfAtual + " | Nome: " + nomeAtual);
                    } else if (!nomeAtual.equals(nomeAnterior)) {
                        nomeAnterior = nomeAtual;
                        System.out.println("Nome: " + nomeAtual);
                    }

                    double saldo = resultSet.getDouble("saldo");
                    String tipoConta = resultSet.getString("tipo");

                    System.out.println("Número da Conta: " + numeroConta);
                    System.out.println("Saldo: R$ " + String.format("%.2f", saldo));
                    System.out.println("Tipo de Conta: " + tipoConta);
                    System.out.println("--------------------------");
                } while (resultSet.next());
            }

            resultSet.close();
            database.desconectarBanco();
        } catch (Exception e) {
            e.printStackTrace();
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

        DbContext database = new DbContext();

        try {
            database.conectarBanco();

            ResultSet resultSet = database
                    .executarQuerySql("SELECT * FROM public.contas WHERE numeroconta = '" + numeroConta + "'");

            if (!resultSet.next()) {
                System.out.println("Conta não encontrada. Tente novamente.");
                return;
            }

            String cpf = resultSet.getString("cpf");
            String tipoConta = resultSet.getString("tipo");
            double saldo = resultSet.getDouble("saldo");

            resultSet.close();

            Conta conta;
            if (tipoConta.equalsIgnoreCase("Corrente")) {
                conta = new ContaCorrente(numeroConta, cpf, saldo);
            } else if (tipoConta.equalsIgnoreCase("Poupança")) {
                conta = new ContaPoupanca(numeroConta, cpf, saldo);
            } else {
                System.out.println("Tipo de conta inválido. Tente novamente.");
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

            // Atualizar o saldo da conta no banco de dados
            boolean statusQuery = database.executarUpdateSql("UPDATE public.contas SET saldo = " + conta.getSaldo()
                    + " WHERE numeroconta = '" + numeroConta + "'");
            if (statusQuery) {
                System.out.println("Saque de R$" + valor + " realizado. Novo saldo: R$ "
                        + String.format("%.2f", conta.getSaldo()));
            }

            database.desconectarBanco();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

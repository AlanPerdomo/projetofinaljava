package Classes;

import java.sql.ResultSet;
import java.util.Random;
import java.util.Scanner;

import Data.DbContext;

public abstract class Conta {
    private int numeroConta;
    private String cpfPessoa;
    private double saldo;

    public Conta(int numeroConta, String cpfPessoa, double saldo) {
        this.numeroConta = numeroConta;
        this.cpfPessoa = cpfPessoa;
        this.saldo = saldo;
    }

    public int getNumeroConta() {
        return numeroConta;
    }

    public String getCpfPessoa() {
        return cpfPessoa;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public void depositar(double valor) {
        saldo += valor;
    }

    public boolean sacar(double valor) {
        if (valor <= saldo) {
            saldo -= valor;
            return true;
        } else {
            return false;
        }
    }

    public static void criarConta(Scanner scanner) {
        System.out.print("Digite o CPF da pessoa: ");
        String cpf = scanner.nextLine();

        DbContext database = new DbContext();

        try {
            database.conectarBanco();
            System.out.println();
            // Verificar se o CPF está cadastrado no banco de dados
            boolean pessoaExistente = Pessoas.verificarPessoaExistente(database, cpf);
            if (!pessoaExistente) {
                System.out.println("CPF não encontrado. Cadastre a pessoa antes de criar a conta.");
                return;
            }

            System.out.println("Escolha o tipo de conta:\n");
            System.out.println("1 - Conta Corrente");
            System.out.println("2 - Conta Poupança");
            System.out.print("\nOpção: ");
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
                        System.out.println("\n---------------------------------");
                        System.out.println("CONTA CORRENTE criada com sucesso!");
                        System.out.println("---------------------------------");
                        System.out.println("CPF do Titular da conta: " + cpf);
                        System.out.println("Numer da conta: " + numeroConta);
                        System.out.println("---------------------------------");
                    }
                    break;
                case 2:
                    statusQuery = database.executarUpdateSql(
                            "INSERT INTO public.contas(numeroconta, cpf, saldo, tipo) VALUES ('" + numeroConta + "', '"
                                    + cpf + "', 0, 'Poupança')");
                    if (statusQuery) {
                        System.out.println("\n---------------------------------");
                        System.out.println("CONTA POUPANÇA criada com sucesso!");
                        System.out.println("---------------------------------");
                        System.out.println("CPF do Titular da conta: " + cpf);
                        System.out.println("Numer da conta: " + numeroConta);
                        System.out.println("---------------------------------");
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

    public static void deletarConta(Scanner scanner) {
        System.out.print("Digite o número da conta a ser deletada: ");
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

        String cpf = conta.getCpfPessoa();
        Pessoas pessoa = Pessoas.buscarPessoaPorCPF(cpf);
        if (pessoa == null) {
            System.out.println("Dados da pessoa não encontrados. Tente novamente.");
            return;
        }

        String nome = pessoa.getNome();

        System.out.println("\n-------------------------------------");
        System.out.println("Nome: " + nome);
        System.out.println("CPF: " + cpf);
        System.out.println("Deseja realmente deletar a conta número " + numeroConta + "? (S/N)");
        String confirmacao = scanner.nextLine().toUpperCase();
        if (!confirmacao.equals("S")) {
            System.out.println("Operação cancelada.");
            return;
        }

        DbContext database = new DbContext();

        try {
            database.conectarBanco();

            boolean statusQuery = database.executarUpdateSql(
                    "DELETE FROM public.contas WHERE numeroconta = " + numeroConta);
            if (statusQuery) {
                System.out.println("\n-------------------------------------");
                System.out.println("\nConta número " + numeroConta + " foi deletada.");
            }

            database.desconectarBanco();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void listarContas() {
        DbContext database = new DbContext();

        try {
            database.conectarBanco();

            ResultSet resultSet = database.executarQuerySql("SELECT * FROM public.contas ORDER BY cpf");

            if (!resultSet.next()) {
                System.out.println("\nNenhuma conta cadastrada.");
                System.out.println("=====================================");
            } else {
                System.out.println("\nLista de Contas Cadastradas:");

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
                        System.out.println("\n=====================================");
                        System.out.println("CPF: " + cpfAtual + " | Nome: " + nomeAtual);
                        System.out.println("=====================================");
                    } else if (!nomeAtual.equals(nomeAnterior)) {
                        nomeAnterior = nomeAtual;
                        System.out.println("Nome: " + nomeAtual);
                    }

                    double saldo = resultSet.getDouble("saldo");
                    String tipoConta = resultSet.getString("tipo");

                    System.out.println("Número da Conta: " + numeroConta);
                    System.out.println("Saldo: R$ " + String.format("%.2f", saldo));
                    System.out.println("Tipo de Conta: " + tipoConta);
                    System.out.println("-------------------------------------");
                } while (resultSet.next());
            }

            resultSet.close();
            database.desconectarBanco();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void realizarDeposito(Scanner scanner) {
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
                String inputValor = scanner.nextLine();

                // Verificar se o valor é numérico
                valor = Double.parseDouble(inputValor);

                if (valor < 0) {
                    System.out.println("O valor do depósito não pode ser negativo. Tente novamente.");
                    return;
                }
            } catch (NumberFormatException e) {
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

    public static void realizarSaque(Scanner scanner) {
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
                System.out.println("\n=====================================");
                System.out.println("Conta não encontrada. Tente novamente.");
                System.out.println("=====================================\n");
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
                String inputValor = scanner.nextLine();

                // Verificar se o valor é numérico
                valor = Double.parseDouble(inputValor);

                if (valor < 0) {
                    System.out.println("O valor do saque não pode ser negativo. Tente novamente.");
                    return;
                }
            } catch (NumberFormatException e) {
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
                System.out.println("\n=====================================");
                System.out.println("Saque de R$" + valor + " realizado. Novo saldo: R$ "
                        + String.format("%.2f", conta.getSaldo()));
                System.out.println("=====================================\n");
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

    public static Conta buscarContaPorNumero(int numeroConta) {
        DbContext database = new DbContext();

        try {
            database.conectarBanco();

            ResultSet resultSet = database.executarQuerySql(
                    "SELECT * FROM public.contas WHERE numeroconta = " + numeroConta);

            if (resultSet.next()) {
                String cpf = resultSet.getString("cpf");
                double saldo = resultSet.getDouble("saldo");
                String tipo = resultSet.getString("tipo");

                Conta conta;
                if (tipo.equals("Corrente")) {
                    conta = new ContaCorrente(numeroConta, cpf, saldo);
                } else if (tipo.equals("Poupança")) {
                    conta = new ContaPoupanca(numeroConta, cpf, saldo);
                } else {
                    return null;
                }

                database.desconectarBanco();
                return conta;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
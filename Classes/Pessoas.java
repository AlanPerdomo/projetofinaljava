package Classes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import Data.DbContext;

public class Pessoas {
    // Atributos
    private String nome;
    private String cpf;

    // Construtor
    public Pessoas(String nome, String cpf) {
        this.nome = nome;
        this.cpf = cpf;
    }

    // Métodos
    public void imprimirDados() {
        System.out.println("Nome: " + nome);
        System.out.println("CPF: " + cpf);
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public static void cadastrarPessoa(Scanner scanner) {
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
                    System.out.println("-------------------------------------");
                    System.out.println("'" + nome + "' foi cadastrado(a)!");
                    System.out.println("-------------------------------------");
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

    public static void deletarPessoa(Scanner scanner) {
        System.out.print("Digite o CPF da pessoa a ser deletada: ");
        String cpf = scanner.nextLine();

        Pessoas pessoa = buscarPessoaPorCPF(cpf);
        if (pessoa == null) {
            System.out.println("CPF não encontrado. Tente novamente.");
            return;
        }

        System.out.println(
                "\nTodas as Contas da pessoa serão deletadas. \nDeseja realmente deletar a pessoa com CPF " + cpf
                        + " ? (S/N): ");
        String confirmacao = scanner.nextLine().toUpperCase();
        if (!confirmacao.equals("S")) {
            System.out.println("Operação cancelada.");
            return;
        }

        String nome = pessoa.getNome();

        DbContext database = new DbContext();

        try {
            database.conectarBanco();

            // Deletar as contas associadas ao CPF
            boolean statusContaQuery = database
                    .executarUpdateSql("DELETE FROM public.contas WHERE cpf = '" + cpf + "'");
            if (statusContaQuery) {
                System.out.println("\n Todas as contas associadas ao CPF " + cpf + " foram deletadas.");
            }

            // Deletar a pessoa com o CPF especificado
            boolean statusPessoaQuery = database
                    .executarUpdateSql("DELETE FROM public.pessoas WHERE cpf = '" + cpf + "'");
            if (statusPessoaQuery) {
                System.out.println("O usuário " + nome + " com CPF " + cpf + " foi deletado.");
            }

            database.desconectarBanco();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Pessoas buscarPessoaPorCPF(String cpf) {
        DbContext database = new DbContext();

        try {
            database.conectarBanco();

            ResultSet resultSet = database.executarQuerySql("SELECT * FROM public.pessoas WHERE cpf = '" + cpf + "'");
            if (resultSet.next()) {
                String nome = resultSet.getString("nome");
                Pessoas pessoa = new Pessoas(nome, cpf);
                return pessoa;
            }

            database.desconectarBanco();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void listarPessoas() {
        DbContext database = new DbContext();

        try {
            database.conectarBanco();
            ResultSet resultSet = database.executarQuerySql("SELECT * FROM public.pessoas");

            if (!resultSet.next()) {
                System.out.println("\nNenhuma pessoa cadastrada.");
            } else {
                System.out.println("\nLista de Pessoas Cadastradas:\n");
                do {
                    String nome = resultSet.getString("nome");
                    String cpf = resultSet.getString("cpf");
                    System.out.println("-------------------------------------");
                    System.out.println("Nome: " + nome + " | CPF: " + cpf);
                } while (resultSet.next());
                System.out.println("=====================================\n");
            }

            database.desconectarBanco();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import Data.DbContext;

public class Pessoas {
    // Atributos
    private String nome;
    private String cpf;

    // Lista de pessoas cadastradas
    private static List<Pessoas> pessoasCadastradas = new ArrayList<>();

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

    public static void cadastrarPessoa(String nome, String cpf) {
        Pessoas pessoa = new Pessoas(nome, cpf);
        pessoasCadastradas.add(pessoa);
        System.out.println("Pessoa cadastrada com sucesso!");
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
}

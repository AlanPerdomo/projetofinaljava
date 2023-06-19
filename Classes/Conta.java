package Classes;
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
            System.out.printf("Saque de %.2f realizado. Novo saldo: R$%.2f\n" , valor, saldo);
            return true;
        } else {
            System.out.println("Saldo insuficiente para saque.");
            return false;
        }
    }
}
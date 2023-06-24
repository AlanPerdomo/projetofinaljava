package Classes;

public class ContaCorrente extends Conta {
    private static final double CHEQUE_ESPECIAL = 500.00;

    public ContaCorrente(int numeroConta, String cpfPessoa, double saldo) {
        super(numeroConta, cpfPessoa, saldo);
    }

    @Override
    public void depositar(double valor) {
        setSaldo(getSaldo() + valor);
    }

    @Override
    public boolean sacar(double valor) {
        double saldoTotal = getSaldo() + CHEQUE_ESPECIAL;
        if (saldoTotal >= valor) {
            setSaldo(getSaldo() - valor);
            System.out.printf("Saque de %.2f realizado. Novo saldo: R$%.2f\n", valor, getSaldo());
            return true;
        } else {
            System.out.println("Saldo insuficiente para saque.");
            return false;
        }
    }
}
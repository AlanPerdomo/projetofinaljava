package Classes;

public class ContaPoupanca extends Conta {
    private double taxaJuros;

    public ContaPoupanca(int numeroConta, String cpfPessoa, double saldo) {
        super(numeroConta, cpfPessoa, saldo);
        this.taxaJuros = 0.005; // 0.005% (0.005/100)
    }

    public void setTaxaJuros(double taxaJuros) {
        this.taxaJuros = taxaJuros;
    }

    public double getTaxaJuros() {
        return taxaJuros;
    }

    @Override
    // Função para fazer deposito na conta poupança
    public void depositar(double valor) {
        super.depositar(valor);
        aplicarJuros();
    }

    // Função par aplicar o juros no momento do deposito
    private void aplicarJuros() {
        double juros = getSaldo() * taxaJuros;
        setSaldo(getSaldo() + juros);
    }
}

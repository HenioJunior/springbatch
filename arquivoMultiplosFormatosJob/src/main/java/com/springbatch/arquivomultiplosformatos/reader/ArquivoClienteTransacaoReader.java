package com.springbatch.arquivomultiplosformatos.reader;

import com.springbatch.arquivomultiplosformatos.dominio.Cliente;
import com.springbatch.arquivomultiplosformatos.dominio.Transacao;
import org.springframework.batch.item.*;

public class ArquivoClienteTransacaoReader<T> implements ItemStreamReader<Cliente> {

    private T objAtual;
    private ItemStreamReader<T> delegate;

    public ArquivoClienteTransacaoReader(ItemStreamReader<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Cliente read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if(objAtual == null)
            objAtual = delegate.read();//ler objeto

        Cliente cliente = (Cliente) objAtual;
        objAtual = null;

        if(cliente != null) {
            while (peek() instanceof Transacao)
                cliente.getTransacoes().add((Transacao) objAtual);
        }
            return cliente;
    }

    private T peek() throws Exception {
        objAtual = delegate.read(); //leitura do proximo item
        return objAtual;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        delegate.open(executionContext);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        delegate.update(executionContext);
    }

    @Override
    public void close() throws ItemStreamException {
        delegate.close();
    }
}

package aufood.com.br.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import aufood.com.br.R;
import aufood.com.br.model.ItemPedido;
import aufood.com.br.model.Pedido;


public class AdapterHistorico extends RecyclerView.Adapter<AdapterHistorico.MyViewHolder> {

    private List<Pedido> pedidos;
    private String pagamento;

    public AdapterHistorico(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_historico, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int i) {

        Pedido pedido = pedidos.get(i);
        holder.nome.setText( pedido.getNome() );
        holder.telefone.setText("Tel: "+ pedido.getTelefone());
        holder.endereco.setText( "Endereço: "+pedido.getEndereco() );
        holder.observacao.setText( "Obs: "+ pedido.getObservacao() );
        holder.dataPedido.setText("Data do pedido: "+ pedido.getDataPedido());
        holder.dataEntrega.setText("Data da entrega: "+ pedido.getDataEntrega());

        List<ItemPedido> itens = new ArrayList<>();
        itens = pedido.getItens();
        String descricaoItens = "";

        int numeroItem = 1;
        Double total = 0.0;
        for( ItemPedido itemPedido : itens ){

            int qtde = itemPedido.getQuantidade();
            Double preco = itemPedido.getPreco();
            total += (qtde * preco);

            String nome = itemPedido.getNomeProduto();
            descricaoItens += numeroItem + ") " + nome + " / (" + qtde + " x R$ " + df.format(preco) + ") \n";
            numeroItem++;
        }
        descricaoItens += "Total: R$ " + df.format(total);
        holder.itens.setText(descricaoItens);

        int metodoPagamento = pedido.getMetodoPagamento();
        if(metodoPagamento == 0){
            pagamento = "Dinheiro";
        }else if (metodoPagamento == 1){
            pagamento = "Cartão";
        }else {
            pagamento = "Pix";
        }
        holder.pgto.setText( "pgto: " + pagamento );

    }




    DecimalFormat df = new DecimalFormat("0.00");


    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nome;
        TextView telefone;
        TextView endereco;
        TextView pgto;
        TextView observacao;
        TextView itens;
        TextView dataPedido;
        TextView dataEntrega;

        public MyViewHolder(View itemView) {
            super(itemView);

            nome        = itemView.findViewById(R.id.textPedidoNome);
            telefone    = itemView.findViewById(R.id.textPedidoTelefone);
            endereco    = itemView.findViewById(R.id.textPedidoEndereco);
            pgto        = itemView.findViewById(R.id.textPedidoPgto);
            observacao  = itemView.findViewById(R.id.textPedidoObs);
            itens       = itemView.findViewById(R.id.textPedidoItens);
            dataPedido  = itemView.findViewById(R.id.textPedidoData);
            dataEntrega = itemView.findViewById(R.id.textEntregaData);
        }
    }

}

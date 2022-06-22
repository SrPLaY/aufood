package aufood.com.br.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

import aufood.com.br.R;
import aufood.com.br.model.ItemPedido;
import aufood.com.br.model.Produto;


public class AdapterCarrinho extends RecyclerView.Adapter<AdapterCarrinho.MyViewHolder>{

    private List<ItemPedido> itensDoCarrinho;
    private Context context;

    public AdapterCarrinho(List<ItemPedido> itensDoCarrinho, Context context) {
        this.itensDoCarrinho = itensDoCarrinho;
        this.context = context;
    }

    public AdapterCarrinho(List<Produto> produtos) {
        this.itensDoCarrinho = itensDoCarrinho;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_carrinho, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        ItemPedido pedido = itensDoCarrinho.get(i);

        DecimalFormat df = new DecimalFormat("0.00");

        holder.nome.setText(pedido.getNomeProduto());
        holder.descricao.setText(pedido.getDescricao());
        holder.valor.setText("R$ " + df.format(pedido.getPreco()));
        holder.qtd.setText("Qtd: "+ pedido.getQuantidade());
    }

    @Override
    public int getItemCount() {
        return itensDoCarrinho.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nome;
        TextView descricao;
        TextView valor;
        TextView qtd;

        public MyViewHolder(View itemView) {
            super(itemView);

            DecimalFormat df = new DecimalFormat("0.00");

            nome = itemView.findViewById(R.id.textNomeProdutoCarrinhoView);
            descricao = itemView.findViewById(R.id.textDescricaoProdutoCarrinhoView);
            valor = itemView.findViewById(R.id.textPrecoCarrinhoView);
            qtd = itemView.findViewById(R.id.textQtdCarrinhoView);
        }
    }
}

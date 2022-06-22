package aufood.com.br.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import aufood.com.br.R;
import aufood.com.br.adapter.AdapterProduto;
import aufood.com.br.helper.ConfiguracaoFirebase;
import aufood.com.br.helper.UsuarioFirebase;
import aufood.com.br.listener.RecyclerItemClickListener;
import aufood.com.br.model.Empresa;
import aufood.com.br.model.ItemPedido;
import aufood.com.br.model.Pedido;
import aufood.com.br.model.Produto;
import aufood.com.br.model.Usuario;
import dmax.dialog.SpotsDialog;

public class CardapioActivity extends AppCompatActivity {

    private RecyclerView recyclerProdutosCardapio;
    private ImageView imageEmpresaCardapio;
    private TextView textNomeEmpresaCardapio;
    private TextView textEmpresaCategoriaCardapio;
    private TextView textEmpresaTaxa;
    private TextView textEmpresaTempo;
    private Empresa empresaSelecionada;
    private AlertDialog dialog;

    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private List<ItemPedido> itensCarrinho = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idEmpresa;
    private String idUsuarioLogado;
    private Usuario usuario;
    private Pedido pedidoRecuperado;
    private  int qtdItensCarrinho;
    private Double totalCarrinho;
    private TextView textCarrinhoQtd, textCarrinhoTotal;
    private int metodoPagamento;
    private Pedido pedidoCarrinho;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardapio);

        //Configurações iniciais
        inicilizarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //recuperar empresa selecionada
        Bundle bunble = getIntent().getExtras();
        if( bunble != null){
            empresaSelecionada = (Empresa) bunble.getSerializable("empresa");

            textNomeEmpresaCardapio.setText(empresaSelecionada.getNome());
            textEmpresaCategoriaCardapio.setText(empresaSelecionada.getCategoria());
            textEmpresaTaxa.setText("R$: " + empresaSelecionada.getPrecoEntrega().toString());
            textEmpresaTempo.setText(empresaSelecionada.getTempo() + "Min");
            idEmpresa = empresaSelecionada.getIdUsuario();


            String url = empresaSelecionada.getUrlImagem();
            Picasso.get()
                    .load(url)
                    .into(imageEmpresaCardapio);



        }

        //Configuração Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Produtos ou Serviços");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configuração recyclerview
        recyclerProdutosCardapio.setLayoutManager(new LinearLayoutManager(this));
        recyclerProdutosCardapio.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        recyclerProdutosCardapio.setAdapter(adapterProduto);

        //Configurar evento de clique
        recyclerProdutosCardapio.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerProdutosCardapio,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                confirmarQuantidade(position);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

        //Recuperar produtos para empresa
        recuperarProdutos();
        recuperarDadosUsuario();

    }

    private void confirmarQuantidade(int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quantidade");
        builder.setMessage("Digite a quantidade");

        EditText editQuantidade = new EditText(this);
        editQuantidade.setText("1");

        builder.setView(editQuantidade);

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                String quantidade = editQuantidade.getText().toString();


                Produto produtoSelecionado = produtos.get(position);
                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setIdProduto(produtoSelecionado.getIdProduto() );
                itemPedido.setNomeProduto(produtoSelecionado.getNome() );
                itemPedido.setPreco(produtoSelecionado.getPreco() );
                itemPedido.setDescricao(produtoSelecionado.getDescricao());
                itemPedido.setQuantidade(Integer.parseInt(quantidade));


                qtdItensCarrinho = 0;
                int cont =0;

                if( !quantidade.isEmpty() && !quantidade.equals("0")){
                    itemPedido.setQuantidade(Integer.parseInt(quantidade));
                    if(itensCarrinho.size() != 0){
                        for (ItemPedido item : itensCarrinho){
                            if(item.getIdProduto().equals(itemPedido.getIdProduto())){



                                int qtde = item.getQuantidade();
                                int qtd2 = Integer.parseInt(quantidade);

                                qtdItensCarrinho += ( qtde + qtd2);

                                item.setQuantidade(qtdItensCarrinho);

                                exibirMensagem( "Esse item já foi escolhido!");
                                break;

                            }else
                                itensCarrinho.add(itemPedido);

                        }
                    }else
                        itensCarrinho.add(itemPedido);


                    pedidoRecuperado = new Pedido(idUsuarioLogado, idEmpresa);

                    pedidoRecuperado.setNome(usuario.getNome());
                    pedidoRecuperado.setEndereco(usuario.getEndereco());
                    pedidoRecuperado.setTelefone(usuario.getTelefone());
                    pedidoRecuperado.setItens(itensCarrinho);
                    pedidoRecuperado.salvar();
                }else {
                    exibirMensagem("Quantidade Inválida!");
                }

            }


        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void recuperarDadosUsuario(){

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando dados")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference usuarioRef = firebaseRef
                .child("usuarios")
                .child( idUsuarioLogado );

        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if( dataSnapshot.getValue() != null ){
                    usuario = dataSnapshot.getValue(Usuario.class);

                }
                recuperarPedido();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void recuperarPedido() {

        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos_usuario")
                .child( idEmpresa )
                .child( idUsuarioLogado );

        pedidoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                qtdItensCarrinho = 0;
                totalCarrinho = 0.0;
                itensCarrinho = new ArrayList<>();

                if(dataSnapshot.getValue() != null){

                    pedidoRecuperado = dataSnapshot.getValue(Pedido.class);
                    itensCarrinho = pedidoRecuperado.getItens();

                    for (ItemPedido itemPedido: itensCarrinho){

                        int qtde = itemPedido.getQuantidade();
                        Double preco = itemPedido.getPreco();

                        totalCarrinho += (qtde * preco);
                        qtdItensCarrinho += qtde;

                    }

                }

                DecimalFormat df = new DecimalFormat("0.00");

                textCarrinhoQtd.setText("Qtd: " + String.valueOf(qtdItensCarrinho) );
                textCarrinhoTotal.setText("R$: " + df.format(totalCarrinho) );


                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void recuperarProdutos(){

        DatabaseReference produtosRef = firebaseRef
                .child("produtos")
                .child( idEmpresa );

        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                produtos.clear();

                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    produtos.add( ds.getValue(Produto.class) );
                }

                adapterProduto.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cardapio, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuPedido:
                Intent i = new Intent(CardapioActivity.this, CarrinhoActivity.class);
                i.putExtra("empresa",empresaSelecionada);
                startActivity(i);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT)
                .show();
    }



    private void inicilizarComponentes(){
        recyclerProdutosCardapio = findViewById(R.id.recyclerProdutosCardapio);
        imageEmpresaCardapio = findViewById(R.id.imageEmpresaCardapio);
        textNomeEmpresaCardapio = findViewById(R.id.textNomeEmpresaCardapio);
        textEmpresaTempo = findViewById(R.id.textTempoEmpresaCardapio);
        textEmpresaTaxa = findViewById(R.id.textEntregaEmpresaCardapio);
        textEmpresaCategoriaCardapio = findViewById(R.id.textEntregaEmpresaCardapio);

        textCarrinhoQtd = findViewById(R.id.textCarrinhoQtd);
        textCarrinhoTotal = findViewById(R.id.textCarrinhoTotal);

    }

}
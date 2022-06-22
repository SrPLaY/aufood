package aufood.com.br.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import aufood.com.br.R;
import aufood.com.br.adapter.AdapterCarrinho;
import aufood.com.br.helper.ConfiguracaoFirebase;
import aufood.com.br.helper.UsuarioFirebase;
import aufood.com.br.listener.RecyclerItemClickListener;
import aufood.com.br.model.Empresa;
import aufood.com.br.model.ItemPedido;
import aufood.com.br.model.Pedido;
import aufood.com.br.model.Produto;
import aufood.com.br.model.Usuario;
import dmax.dialog.SpotsDialog;

public class CarrinhoActivity extends AppCompatActivity {

    private RecyclerView recyclerCarrinho;
    private Empresa empresaSelecionada;
    private AlertDialog dialog;

    private AdapterCarrinho adapterCarrinho;
    private List<ItemPedido> pedidos = new ArrayList<>();
    private List<Produto> produtos = new ArrayList<>();
    private List<ItemPedido> itensCarrinho = new ArrayList<>();
    private List<Pedido> peddidoSelecionado = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idEmpresa;
    private String idUsuarioLogado;
    private Usuario usuario;
    private Pedido pedidoRecuperado;
    private  int qtdItensCarrinho;
    private Double totalCarrinho;
    private TextView textCarrinhoQtd, textCarrinhoTotal;
    private int metodoPagamento;
    private Date dataPedido = new Date();
    private Button botaoAgendar;
    private String agendamento;
    private String hrAgendamento;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho);

        //Configurações iniciais
        inicilizarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();


        //recuperar empresa selecionada
        Bundle bunble = getIntent().getExtras();
        if( bunble != null){
            empresaSelecionada = (Empresa) bunble.getSerializable("empresa");
            idEmpresa = empresaSelecionada.getIdUsuario();

        }

        //Configuração Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Produtos ou Serviços");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        botaoAgendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(CarrinhoActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                calendar.set(year, month, day);
                                String format = "dd/MM/yyyy";
                                SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
                                Date date;

                                try {
                                    date = sdf.parse(sdf.format(calendar.getTime()));
                                    String dayS = new SimpleDateFormat("dd", Locale.ENGLISH).format(date);
                                    String monthS = new SimpleDateFormat("MM", Locale.ENGLISH).format(date);
                                    String yearS = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date);
                                    String hrS = new SimpleDateFormat("HH:MM", Locale.ENGLISH).format(date);

                                    agendamento = (dayS + "/" + monthS + "/" + yearS);

                                } catch (ParseException exception) {
                                    exception.printStackTrace();
                                }
                            }
                        }, year, month, day);
                datePickerDialog.show();
                datePickerDialog.getDatePicker();
            }

        });



        //Configuração recyclerview
        recyclerCarrinho.setLayoutManager(new LinearLayoutManager(this));
        recyclerCarrinho.setHasFixedSize(true);
        adapterCarrinho = new AdapterCarrinho(pedidos, this);
        recyclerCarrinho.setAdapter(adapterCarrinho);

        //Configurar evento de clique
        recyclerCarrinho.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerCarrinho,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                excluirProdutos(position);


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
        recuperarPedido();
        //recuperarPedidosCarrinho();
        recuperarDadosUsuario();

    }


    private void excluirProdutos(int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Excluir itens");
        builder.setMessage("Deseja excluir os itens do carrinho ?");

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Produto produtoSelecionado = produtos.get(position);
                ItemPedido itemPedido = new ItemPedido();
                



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
    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT)
                .show();
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
                pedidos.clear();

                if(dataSnapshot.getValue() != null){

                    pedidoRecuperado = dataSnapshot.getValue(Pedido.class);
                    itensCarrinho = pedidoRecuperado.getItens();

                    for (ItemPedido itemPedido: itensCarrinho){

                        int qtde = itemPedido.getQuantidade();
                        Double preco = itemPedido.getPreco();
                        String idProduto = itemPedido.getIdProduto();
                        String descricao = itemPedido.getDescricao();

                        pedidos.add(itemPedido);

                        totalCarrinho += (qtde * preco);
                        qtdItensCarrinho += qtde;

                    }

                }

                DecimalFormat df = new DecimalFormat("0.00");

                textCarrinhoQtd.setText("Qtd: " + String.valueOf(qtdItensCarrinho) );
                textCarrinhoTotal.setText("R$: " + df.format(totalCarrinho) );



                adapterCarrinho.notifyDataSetChanged();

                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_carrinho, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuCarrinho:
                confirmarPedido();

                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void confirmarPedido() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione um método de pagamento:");

        CharSequence[] itens = new CharSequence[]{
                "Dinheiro", "Máquina de cartão", "Pix"
        };
        builder.setSingleChoiceItems(itens, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                metodoPagamento = which;
            }
        });

        EditText editObservacao = new EditText(this);
        editObservacao.setHint("Digite uma observação");
        builder.setView(editObservacao);

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String data = new SimpleDateFormat("dd/mm/yyyy").format(dataPedido);
                String hora = new SimpleDateFormat("HH:mm:ss").format(dataPedido);



                String observacao = editObservacao.getText().toString();
                pedidoRecuperado.setMetodoPagamento(metodoPagamento);
                pedidoRecuperado.setObservacao(observacao);
                pedidoRecuperado.setDataPedido(data + " " + hora);
                pedidoRecuperado.setDataAgendamento(agendamento);
                pedidoRecuperado.setStatus("confirmado");
                pedidoRecuperado.confirmar();
                pedidoRecuperado.remover();
                pedidoRecuperado = null;

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

    private void inicilizarComponentes(){
        recyclerCarrinho = findViewById(R.id.recyclerProdutosCarrinhoView);
        textCarrinhoQtd = findViewById(R.id.textViewCarrinhoQtd);
        textCarrinhoTotal = findViewById(R.id.textViewCarrinhoTotal);
        botaoAgendar = findViewById(R.id.buttonAgendar);

    }

}



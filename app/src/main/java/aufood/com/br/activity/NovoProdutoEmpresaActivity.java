package aufood.com.br.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import aufood.com.br.R;
import aufood.com.br.helper.ConfiguracaoFirebase;
import aufood.com.br.helper.UsuarioFirebase;
import aufood.com.br.model.Empresa;
import aufood.com.br.model.Produto;

public class NovoProdutoEmpresaActivity extends AppCompatActivity {

    private EditText editProdutoNome, editProdutoDescricao,
            editProdutoPreco;
    private String idUsuarioLogado, idProduto;
    private DatabaseReference firebaseRef;
    private Produto produtoSlecionado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        //Configuração iniciais
        inicializarComponentes();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        firebaseRef = ConfiguracaoFirebase.getFirebase();

        //Recuperar empresa selecionada
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            produtoSlecionado = (Produto) bundle.getSerializable("produto");

            editProdutoNome.setText(produtoSlecionado.getNome());
            editProdutoPreco.setText(produtoSlecionado.getPreco().toString());
            editProdutoDescricao.setText(produtoSlecionado.getDescricao());
            idProduto = (produtoSlecionado.getIdProduto());

        }

        //Configuração Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo produto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }


    public void validarDadosProduto(View view){

        //Validar se os campos foram preenchidos
        String nome = editProdutoNome.getText().toString();
        String descricao = editProdutoDescricao.getText().toString();
        String preco = editProdutoPreco.getText().toString();

        if ( !nome.isEmpty()){
            if ( !descricao.isEmpty()){
                if ( !preco.isEmpty()){

                    Produto produto = new Produto();
                    produto.setIdUsuario( idUsuarioLogado );
                    produto.setNome( nome );
                    produto.setDescricao(descricao );
                    produto.setPreco( Double.parseDouble(preco) );

                    if(idProduto != null){

                        produto.setIdProduto(idProduto);
                        produto.editar();
                        finish();

                        exibirMensagem( "Produto salvo com sucesso!");

                    }else {

                        produto.salvar();
                        finish();

                        exibirMensagem( "Produto salvo com sucesso!");
                    }

                }else{
                    exibirMensagem("Digite um preço para o produto");

                }

            }else{
                exibirMensagem("Digite uma descrição para o produto ou serviço ");

            }

        }else{
            exibirMensagem("Digite um nome para a produto");

        }
    }

    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT)
                .show();
    }

    private void inicializarComponentes(){
        editProdutoDescricao = findViewById(R.id.editProdutoDescricao);
        editProdutoNome = findViewById(R.id.editProdutoNome);
        editProdutoPreco = findViewById(R.id.editProdutoPreco);
    }

}
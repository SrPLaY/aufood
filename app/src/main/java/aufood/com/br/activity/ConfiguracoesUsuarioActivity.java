package aufood.com.br.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import aufood.com.br.R;
import aufood.com.br.helper.ConfiguracaoFirebase;
import aufood.com.br.helper.UsuarioFirebase;
import aufood.com.br.model.Empresa;
import aufood.com.br.model.Usuario;
import dmax.dialog.SpotsDialog;

public class ConfiguracoesUsuarioActivity extends AppCompatActivity {

    private EditText editUsuarioNome, editTelefone, editEndereco;
    private String idUsuario;
    DatabaseReference firebaseRef;
    private String urlImagemSelecionada = "https://firebasestorage.googleapis.com/v0/b/aufood-cc438.appspot.com/o/Imagem%2FEmpresa%2FQH3x7QY6ZmSRSdwPbnN6t22kfSf2Jpeg?alt=media&token=a207bb48-66d7-4818-acfc-c92ac7a0fd55";
    private StorageReference storageReference;
    private static final int SELECAO_GALERIA = 200;
    private ImageView imagePerfilUsuario;
    String nomeArquivo = UUID.randomUUID().toString();
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_usuario);

        //Configuração iniciais
        inicializarComponentes();
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuario = UsuarioFirebase.getIdUsuario();

        //Criando mascara telefone
        SimpleMaskFormatter smf = new SimpleMaskFormatter("(NN)NNNNN-NNNN");
        MaskTextWatcher mtw = new MaskTextWatcher(editTelefone, smf);
        editTelefone.addTextChangedListener(mtw);


        //Configuração Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações usuário");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imagePerfilUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );
                if ( i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i,SELECAO_GALERIA);
                }
            }
        });

        //Recuperar dados do usuario
        recuperarDadosUsuario();

    }

    public void recuperarDadosUsuario(){

        DatabaseReference usuarioRef = firebaseRef
                .child("usuarios")
                .child( idUsuario );
        usuarioRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if( dataSnapshot.getValue() != null ){
                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                    editUsuarioNome.setText(usuario.getNome());
                    editTelefone.setText(usuario.getTelefone());
                    editEndereco.setText(usuario.getEndereco());

                    urlImagemSelecionada = usuario.getUrlImagem();
                    if( urlImagemSelecionada != "" ){
                        Picasso.get()
                                .load(urlImagemSelecionada)
                                .into(imagePerfilUsuario);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void validarDadosUsuario (View view){

        //Validar se os campos foram preenchidos
        String nome = editUsuarioNome.getText().toString();
        String endereco = editEndereco.getText().toString();
        String telefone = editTelefone.getText().toString();

        if ( !nome.isEmpty()){
            if ( !endereco.isEmpty()){
                if ( !telefone.isEmpty()){

                        Usuario usuario = new Usuario();
                        usuario.setIdUsuario( idUsuario );
                        usuario.setNome( nome );
                        usuario.setTelefone( telefone );
                        usuario.setEndereco( endereco );
                        usuario.setUrlImagem( urlImagemSelecionada );
                        usuario.salvar();

                    exibirMensagem("Dados atualizados com sucesso!");
                        finish();
                    startActivity(new Intent(ConfiguracoesUsuarioActivity.this,HomeActivity.class));

                }else {
                    exibirMensagem("Digite seu telefone!");

                }

            }else {
                exibirMensagem("Digite seu endereço completo!");

            }

        }else {
            exibirMensagem("Digite seu nome completo!");

        }
    }



    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {

                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri localImagem = data.getData();
                        imagem = MediaStore.Images
                                .Media
                                .getBitmap(getContentResolver(),
                                        localImagem
                                );
                        break;
                }

                if( imagem != null){

                    imagePerfilUsuario.setImageBitmap( imagem );

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte [] dadosImagem = baos.toByteArray();

                    final StorageReference imagemRef = storageReference.child("Imagem").child("Usuario")
                            .child(idUsuario + "Jpeg");

                    UploadTask uploadTask = imagemRef.putBytes( dadosImagem );
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(ConfiguracoesUsuarioActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //taskSnapshot.getDownloadUrl();
                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                    urlImagemSelecionada = String.valueOf(url);
                                }
                            });

                            Toast.makeText(ConfiguracoesUsuarioActivity.this,
                                    "Sucesso ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private void inicializarComponentes(){
        editUsuarioNome = findViewById(R.id.editUsuarioNome);
        editTelefone = findViewById(R.id.editTelefone);
        editEndereco = findViewById(R.id.editEndereco);
        imagePerfilUsuario = findViewById(R.id.imagePerfilUsuario);
    }

}
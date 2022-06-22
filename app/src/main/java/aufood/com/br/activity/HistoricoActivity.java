package aufood.com.br.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import aufood.com.br.R;
import aufood.com.br.adapter.AdapterHistorico;
import aufood.com.br.adapter.AdapterPedido;
import aufood.com.br.helper.ConfiguracaoFirebase;
import aufood.com.br.helper.UsuarioFirebase;
import aufood.com.br.listener.RecyclerItemClickListener;
import aufood.com.br.model.Pedido;
import dmax.dialog.SpotsDialog;

public class HistoricoActivity extends AppCompatActivity {

    private RecyclerView recyclerHistorico;
    private AdapterHistorico adapterHistorico;
    private List<Pedido> pedidos = new ArrayList<>();
    private AlertDialog dialog;
    private DatabaseReference firebaseRef;
    private String idEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        //Configurações iniciais
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idEmpresa = UsuarioFirebase.getIdUsuario();

        //Configuração Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Histórico de vendas");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configura recyclerview
        recyclerHistorico.setLayoutManager(new LinearLayoutManager(this));
        recyclerHistorico.setHasFixedSize(true);
        adapterHistorico = new AdapterHistorico(pedidos);
        recyclerHistorico.setAdapter(adapterHistorico);

        recuperarPedidos();

        adapterHistorico.notifyDataSetChanged();

    }

    private void recuperarPedidos() {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando dados")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference pedidoRef = firebaseRef
                .child("historico_pedidos")
                .child(idEmpresa);

        Query query = pedidoRef.orderByChild("status")
                .equalTo("finalizado");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                pedidos.clear();

                if (dataSnapshot.getValue() != null) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Pedido pedido = ds.getValue(Pedido.class);
                        pedidos.add(pedido);
                    }
                    adapterHistorico.notifyDataSetChanged();

                } else {
                    Toast.makeText(HistoricoActivity.this,
                            "Não há itens no histórico de pedidos",
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dialog.dismiss();

    }


        private void inicializarComponentes() {
        recyclerHistorico = findViewById(R.id.recyclerHistorico);

    }

}
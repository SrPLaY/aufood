package aufood.com.br.helper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class ConfiguracaoFirebase {

    private static DatabaseReference referenciaFirebase;
    private static FirebaseAuth referenciaAutenticadao;
    private static StorageReference referenciaStorage;



    //retorna a referencia do database
    public static DatabaseReference getFirebase(){
        if( referenciaFirebase == null){
            referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        }
        return referenciaFirebase;
    }

    //retorna a instacia do FirebaseAuth
    public static FirebaseAuth getFirebaseAutenticacao(){
        if( referenciaAutenticadao == null){
            referenciaAutenticadao = FirebaseAuth.getInstance();
        }
        return referenciaAutenticadao;
    }

    //retorna instacia do FirebaseStorage
    public static StorageReference getFirebaseStorage (){
        if( referenciaStorage == null){
            referenciaStorage = FirebaseStorage.getInstance().getReference();
        }
        return referenciaStorage;
    }

}

package aufood.com.br.model;

import com.google.firebase.database.DatabaseReference;

import aufood.com.br.helper.ConfiguracaoFirebase;

public class ItemPedido {

    private String idProduto;
    private String nomeProduto;
    private int quantidade;
    private Double preco;
    private String descricao;
    private String idUser;
    private String idEmp;

    public ItemPedido() {

    }

    public String getIdEmp() {
        return idEmp;
    }

    public void setIdEmp(String idEmp) {
        this.idEmp = idEmp;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public void remover() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference produtoRef = firebaseRef
                .child("pedidos_usuario")
                .child(getIdEmp())
                .child( getIdUser() )
                .child( getIdProduto() );
        produtoRef.removeValue();


    }
}

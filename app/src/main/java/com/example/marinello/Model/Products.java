package com.example.marinello.Model;

public class Products {
    private String nome_prodotto, descrizione,prezzo, immagine, categoria,
        pid, data, tempo;

    public Products(){

    }

    public Products(String nome_prodotto, String descrizione, String prezzo, String immagine, String categoria, String pid, String data, String tempo) {
        this.nome_prodotto = nome_prodotto;
        this.descrizione = descrizione;
        this.prezzo = prezzo;
        this.immagine = immagine;
        this.categoria = categoria;
        this.pid = pid;
        this.data = data;
        this.tempo = tempo;
    }

    public String getNome_prodotto() {
        return nome_prodotto;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getPrezzo() {
        return prezzo;
    }

    public String getImmagine() {
        return immagine;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getPid() {
        return pid;
    }

    public String getData() {
        return data;
    }

    public String getTempo() {
        return tempo;
    }

    public void setNome_prodotto(String nome_prodotto) {
        this.nome_prodotto = nome_prodotto;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public void setPrezzo(String prezzo) {
        this.prezzo = prezzo;
    }

    public void setImmagine(String immagine) {
        this.immagine = immagine;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }
}

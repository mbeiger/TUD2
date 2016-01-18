package pl.projekt.tp.service;
import  pl.projekt.tp.domain.Telefon;
import pl.projekt.tp.domain.Model;

import java.util.List;

public interface Manager {

    List<Telefon> dajWszystkieTelefony();
    List<Model> dajWszystkieModele();

    List<Telefon> WyszukajTelefonPoNazwie(String wzorzec);

    void usunZaleznosci(Model mod);

    Long dodaj(Telefon telefon);
    Long dodaj(Model model);

    void usun(Telefon tel);
    void usun(Model mod);

    void edycja(Telefon tel, Model model, String nazwa, String opis);
    void edycja(Model mod, String nazwa, String opis);

    Telefon pobierzTelefonPoId(Long id);
    Model pobierzModelPoId(Long id);


}

package pl.projekt.tp.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import pl.projekt.tp.domain.Model;
import pl.projekt.tp.domain.Telefon;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/beans.xml" })
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = true)
@Transactional
public class ManagerTest {

    @Autowired
    Manager manager;

    private List<Long>  dodaneTelefony = new ArrayList<Long>(); 
    private List<Long>  dodaneModele = new ArrayList<Long>();

    private final String model1 = "5s";
    private final String opisModele1 = "Niezawodny model telefonu ";
    private final String model2 = "Note";
    private final String opisModele2 = "Duży wyswietlacz";

    private final String telefon1 = "iPhone";
    private final String opisTelefony1 = "Latwy w obsludze";
    private final String telefon2 = "Samsung";
    private final String opisTelefony2 = "Najnowszy model telefonu";

    @Before // wykonaywane przed kazdym testem, dzieki temu przed kazdym testem nie musze dodawac danych wyzejv czyli model" 5s" itp
    public void sprawdzDodaneTelefonyModele() {

        List<Telefon> telefony = manager.dajWszystkieTelefony(); //pobiera wszystkie telefony z bazy
        List<Model> modele = manager.dajWszystkieModele(); //pobiera wszystkie modele z bazy

        for(Telefon telefon : telefony) // dodaje tel do listy wyzej
            dodaneTelefony.add(telefon.getId());

        for(Model model : modele) // dodaje modele do listy wyzej
            dodaneModele.add(model.getId());
    }

    @After // wykonywane po kazdym tescie, po kazdym tescie usuwa dane z tabel
    public void usunTestowaneWpisy() { 

        List<Telefon> telefony = manager.dajWszystkieTelefony(); //pobiera wszystkie telefony z bazy

        List<Model> modele = manager.dajWszystkieModele(); //pobiera wszystkie modele z bazy

        boolean usun;

        for(Telefon telefon : telefony) { 
            usun = true;
            for (Long telefon2 : dodaneTelefony) 
                if (telefon.getId() == telefon2) {
                usun = false;
                break;
                }
            if(usun)
                manager.usun(telefon);
        }

        for(Model model : modele) {
            usun = true;
            for (Long model2 : dodaneModele)
                if (model.getId() == model2)
                    {
                        usun = false;
                        break;
                    }
            if(usun)
                manager.usun(model);
        }
    }

    @Test
    public void Edycja() { 

        Model mod = new Model(); // tworze model 

        mod.setNazwa(model1); //ustawiam wartosci
        mod.setOpis(opisModele1);

        Telefon tel = new Telefon();  // tworze teledon 

        tel.setModel(mod); //ustawiam wartosci
        tel.setNazwa(telefon1);
        tel.setOpis(opisTelefony1); 

        Long modID = manager.dodaj(mod);// pobieram id modelu 
        Long telID = manager.dodaj(tel); // pobieram id telefonu

        List<Telefon> telefony = manager.dajWszystkieTelefony(); // pobieram listy 
        List<Model> modele = manager.dajWszystkieModele();

        manager.edycja(mod, model2, opisModele2); // wywowalnie funkcji z menagera
        manager.edycja(tel, mod, telefon2, opisTelefony2);


        int a = 0;
        int b = 0;
		
		//  lista1 ma liste edytowanych danych (manager.edycja), potem mam liste2 czy dane  z wejscia , porownuje je czy sie roznia
        List<Telefon> telefony2 = manager.dajWszystkieTelefony(); // druga lista 
        List<Model> modele2 = manager.dajWszystkieModele();

        for(Telefon telefon : telefony) { // musza byc listy takie same 
            for (Telefon tel2 : telefony2){
                if(telefon.getId() == tel2.getId()) {
                    if (telefon.getId() != telID) { //sprawdzamy ile jest roznych id a ile takich samych
                        assertEquals(tel2.getModel().getNazwa(), telefon.getModel().getNazwa());
                        assertEquals(tel2.getModel().getOpis(), telefon.getModel().getOpis());
                        assertEquals(tel2.getNazwa(), telefon.getNazwa());
                        assertEquals(tel2.getOpis(), telefon.getOpis());
                        a++;  // jesli pozosdtale beda takie same
                    } else if (telefon.getId() == telID) {  // jesli id beda takie same b+1
                        assertEquals(model2, telefon.getModel().getNazwa());
                        assertEquals(opisModele2, telefon.getModel().getOpis());
                        assertEquals(telefon2, telefon.getNazwa());
                        assertEquals(opisTelefony2, telefon.getOpis());
                        b++; // 1 elem. zostal zedytowany wiec musi wyjsc 1
                    }
                }
            }
        }
        assertEquals(b, 1); // jesli bedzie 0 to test nie przejdzie , musi wyjsc 1,
        assertEquals(a+b, telefony2.size());  
        assertEquals(telefony.size(), telefony2.size());
        a = 0;
        b = 0;
        for(Model model : modele) {
            for (Model mod2 : modele2){
                if(model.getId() == mod2.getId()) {
                    if (model.getId() != modID) {
                        assertEquals(modele2.get(a).getNazwa(), model.getNazwa());
                        assertEquals(modele2.get(a).getOpis(), model.getOpis());
                        a++;
                    } else if (model.getId() == modID) {
                        assertEquals(model2, model.getNazwa());
                        assertEquals(opisModele2, model.getOpis());
                        b++;
                    }
                }
            }
        }

        assertEquals(b, 1); // musi wyjsc 1
        assertEquals(a+b, modele2.size());
        assertEquals(modele.size(), modele2.size());
    }

    @Test
    public void WyszukanieTelefony(){


        Model mod1 = new Model(); // nowy obiekt
        Model mod2 = new Model();

        mod1.setNazwa(model1); // ustawiamy wartosci
        mod1.setOpis(opisModele1);

        mod2.setNazwa(model2);
        mod2.setOpis(opisModele2);

        manager.dodaj(mod1); // dodajemy do bazy danych
        manager.dodaj(mod2);

        Telefon tel1 = new Telefon(); // nowy obiekt
        Telefon tel2 = new Telefon();

        tel1.setModel(mod1); 
        tel1.setNazwa(telefon1); 
        tel1.setOpis(opisTelefony1);

        tel2.setModel(mod2);
        tel2.setNazwa(telefon1);
        tel2.setOpis(opisTelefony2);

        manager.dodaj(tel1); // dodajemy do bazy danych tel
        manager.dodaj(tel2);

        assertEquals(manager.WyszukajTelefonPoNazwie(telefon1).size(), 2); // 2 razy przypisalismy dlatego tyle, bo np. mod1 jest przypisany do tel dwa razy


        assertEquals(manager.WyszukajTelefonPoNazwie(telefon2).size(), 0); // ma wyjsc 0 ,bo do niczego nie jest przypisane

    }

    @Test
    public void PobraniePoId() { 

        Model model = new Model();

        model.setNazwa(model1);
        model.setOpis(opisModele1);

        Telefon telefon = new Telefon();

        telefon.setModel(model);
        telefon.setNazwa(telefon1);
        telefon.setOpis(opisTelefony1);

        Long modelId = manager.dodaj(model);
        Long telefonID = manager.dodaj(telefon);

        Telefon telefon2 = manager.pobierzTelefonPoId(telefonID); // pobieramy po id , nowa klasa telefon 2
        Model model2 = manager.pobierzModelPoId(modelId);

        assertEquals(modelId, model2.getId()); //spr czy mamy to samo co wyzej
        assertEquals(model1, model2.getNazwa());
        assertEquals(opisModele1, model2.getOpis());

        assertEquals(telefonID, telefon2.getId());
        assertEquals(model.getNazwa(), telefon2.getModel().getNazwa());
        assertEquals(model.getOpis(), telefon2.getModel().getOpis());
        assertEquals(telefon1, telefon2.getNazwa());
        assertEquals(opisTelefony1, telefon2.getOpis());

    }

    @Test
    public void WyszukanieTelefonyPoNazwie() {

        Model mod = new Model();

        mod.setNazwa(model1);
        mod.setOpis(opisModele1);

        Telefon tel = new Telefon();

        tel.setModel(mod);
        tel.setNazwa(telefon1);
        tel.setOpis(opisTelefony1);
        manager.dodaj(mod);
        manager.dodaj(tel);
        String wzor = telefon1;
        int ile = 0; 

        for(Long l : dodaneTelefony)
        {
            if(wzor == manager.pobierzTelefonPoId(l).getNazwa())
                ile++; // sprawdzam czy nie mam czegos wczesniej do bazy dodanego
        }
        List<Telefon> telefony = manager.WyszukajTelefonPoNazwie(wzor); // wyszkuje wedlug wzorca model1
        assertEquals(telefony.size(), ile+1); //ile bylo juz wczesniej + ten dodany w testach 
    }

    @Test
    public void PobieranieWszystkich() { 

        List<Telefon> telefony = manager.dajWszystkieTelefony(); //nowa listy
        List<Model> modele = manager.dajWszystkieModele();

        int ileTelefonow = telefony.size(); // ilosc do listy
        int ileModeli = modele.size();

        Model mod = new Model();

        mod.setNazwa(model1);
        mod.setOpis(opisModele1);

        Telefon tel = new Telefon();

        tel.setModel(mod);
        tel.setNazwa(telefon1);
        tel.setOpis(opisTelefony1);

        manager.dodaj(mod);
        manager.dodaj(tel);

        telefony = manager.dajWszystkieTelefony();
        modele = manager.dajWszystkieModele();

        assertEquals(ileTelefonow+1, telefony.size()); // dodalismy jeden wiec ilosc powinna byc +1
        assertEquals(ileModeli+1, modele.size());

        for(Telefon telefon : telefony) { //spr czy nie sa puste klasy
            tel = manager.pobierzTelefonPoId(telefon.getId());
            assertNotNull(tel);
        }
        for(Model model : modele) {
            mod = manager.pobierzModelPoId(model.getId());
            assertNotNull(mod);
        }
    }

    @Test
    public void Dodanie() {

        Model mod = new Model();

        mod.setNazwa(model1);
        mod.setOpis(opisModele1);

        Telefon tel = new Telefon();

        tel.setModel(mod);
        tel.setNazwa(telefon1);
        tel.setOpis(opisTelefony1);

        Long modId = manager.dodaj(mod);
        Long telId = manager.dodaj(tel);

        Telefon telPoId = manager.pobierzTelefonPoId(telId); //pobieramy to co dodalismy 
        Model modPoId = manager.pobierzModelPoId(modId);

        assertEquals(model1, modPoId.getNazwa());
        assertEquals(opisModele1, modPoId.getOpis());

        assertEquals(model1, telPoId.getModel().getNazwa()); //spr czy zostalo dodane
        assertEquals(opisModele1, telPoId.getModel().getOpis());
        assertEquals(telefon1, telPoId.getNazwa());
        assertEquals(opisTelefony1, telPoId.getOpis());

    }

    @Test
    public void UsuwanieZaleznosci() {
        Model mod1 = new Model();

        mod1.setNazwa(model1);
        mod1.setOpis(opisModele1);

        manager.dodaj(mod1);

        Telefon tel1 = new Telefon();
        Telefon tel2 = new Telefon();
        tel1.setModel(mod1);
        tel1.setNazwa(telefon1);
        tel1.setOpis(opisTelefony1);
        tel2.setModel(mod1);
        tel2.setNazwa(telefon2);
        tel2.setOpis(opisTelefony2);
        Long idTelefon1 = manager.dodaj(tel1);
        Long idTelefon2 = manager.dodaj(tel2);

        List<Telefon> telefony = manager.dajWszystkieTelefony();
        manager.usunZaleznosci(mod1);
        Telefon telefonPoId1 = manager.pobierzTelefonPoId(idTelefon1);
        Telefon telefonPoId2 = manager.pobierzTelefonPoId(idTelefon2);

        assertEquals(telefonPoId1, null);
        assertEquals(telefonPoId2, null);
        List<Telefon> telefony2 = manager.dajWszystkieTelefony();
        assertEquals(telefony2.size(), telefony.size()-2);

        int i = 0;

        for(Telefon telefon : telefony) {
            for(Telefon telefon2 : telefony2)
                if(telefon.getId() == telefon2.getId()) {
                    assertEquals(telefon2.getModel().getNazwa(), telefon.getModel().getNazwa());
                    assertEquals(telefon2.getModel().getOpis(), telefon.getModel().getOpis());
                    assertEquals(telefon2.getNazwa(), telefon.getNazwa());
                    assertEquals(telefon2.getOpis(), telefon.getOpis());
                    i++;
                }
        }

        assertEquals(telefony2.size(), i);
    }

    @Test
    public void UsuwanieKaskadowe() {
        Model mod1 = new Model();

        mod1.setNazwa(model1);
        mod1.setOpis(opisModele1);

        manager.dodaj(mod1);

        Telefon tel1 = new Telefon();
        Telefon tel2 = new Telefon();

        tel1.setModel(mod1);
        tel1.setNazwa(telefon1);
        tel1.setOpis(opisTelefony1);

        tel2.setModel(mod1);
        tel2.setNazwa(telefon2);
        tel2.setOpis(opisTelefony2);

        Long idTelefon1 = manager.dodaj(tel1);
        Long idTelefon2 = manager.dodaj(tel2);

        List<Telefon> telefony = manager.dajWszystkieTelefony();

        manager.usun(mod1);

        Telefon telefonPoId1 = manager.pobierzTelefonPoId(idTelefon1);
        Telefon telefonPoId2 = manager.pobierzTelefonPoId(idTelefon2);

        assertEquals(telefonPoId1, null);
        assertEquals(telefonPoId2, null);

        List<Telefon> telefony2 = manager.dajWszystkieTelefony();

        assertEquals(telefony2.size(), telefony.size()-2);

        int i = 0;

        for(Telefon telefon : telefony) {
            for(Telefon telefon2 : telefony2)
                if(telefon.getId() == telefon2.getId()) {
                    assertEquals(telefon2.getModel().getNazwa(), telefon.getModel().getNazwa());
                    assertEquals(telefon2.getModel().getOpis(), telefon.getModel().getOpis());
                    assertEquals(telefon2.getNazwa(), telefon.getNazwa());
                    assertEquals(telefon2.getOpis(), telefon.getOpis());
                    i++;
                }
        }

        assertEquals(telefony2.size(), i);
    }

    @Test
    public void Usuwanie() { 

        Model mod = new Model();

        mod.setNazwa(model1);
        mod.setOpis(opisModele1);

        Telefon tel = new Telefon();

        tel.setModel(mod);
        tel.setNazwa(telefon1);
        tel.setOpis(opisTelefony1);

        Long modId = manager.dodaj(mod);
        Long telId = manager.dodaj(tel);

        List<Telefon> telefony = manager.dajWszystkieTelefony(); // pobieramy wszystkie
        List<Model> modele = manager.dajWszystkieModele();

        manager.usun(tel); // usuwamy to co dodane wyzej w tescie
        manager.usun(mod);

        int ileTelefonow = telefony.size(); // spr wielkosci tab
        int ileModeli = modele.size();

        Telefon telPoId = manager.pobierzTelefonPoId(telId); // pobieramy to co usunelismy po id
        Model modPoId = manager.pobierzModelPoId(modId);

        assertEquals(telPoId, null); // spr czy jest puste, bo musi byc
        assertEquals(modPoId, null);

        List<Telefon> telefony2 = manager.dajWszystkieTelefony(); // druga lista 
        List<Model> modele2 = manager.dajWszystkieModele();

        assertEquals(telefony2.size(), ileTelefonow-1); // spr czy jest mniejsza o 1 
        assertEquals(modele2.size(), ileModeli-1);

        int i = 0;

        for(Telefon telefon : telefony) { // spr czy nie zostalo  usuniete to co nie mialo byc usuniete, ta reszta
            for(Telefon telefon2 : telefony2)
                if(telefon.getId() == telefon2.getId()) { // jesli id to samo co usuniety id to wtedy i++
                    assertEquals(telefon2.getModel().getNazwa(), telefon.getModel().getNazwa());
                    assertEquals(telefon2.getModel().getOpis(), telefon.getModel().getOpis());
                    assertEquals(telefon2.getNazwa(), telefon.getNazwa()); // spr czy mamy to samo co w pierwszej liscie
                    assertEquals(telefon2.getOpis(), telefon.getOpis());
                    i++;
                }
        }

        assertEquals(telefony2.size(), i); //spr czy mamy to samo w liscie 2 ( elem. po usunieciu ona ma) 

        i = 0;

        for(Model model : modele) {
            for(Model model2 : modele2)
            {
                if(model.getId() == model2.getId()) {
                    assertEquals(model2.getNazwa(), model.getNazwa());
                    assertEquals(model2.getOpis(), model.getOpis());
                    i++;
                }
            }
        }

        assertEquals(modele2.size(), i);
    }

}

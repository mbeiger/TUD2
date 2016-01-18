package pl.projekt.tp.service;

import pl.projekt.tp.domain.Telefon;
import pl.projekt.tp.domain.Model;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Transactional
public class ManagerImpl implements Manager {

    @Autowired
    private SessionFactory sf;

    public SessionFactory getSessionFactory() {
        return sf;
    }

    public void setSessionFactory(SessionFactory sf) {
        this.sf = sf;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Telefon> dajWszystkieTelefony() {
        return sf.getCurrentSession().getNamedQuery("all.telefony").list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Model> dajWszystkieModele() {
        return sf.getCurrentSession().getNamedQuery("all.modele").list();
    }

    @Override
    public List<Telefon> WyszukajTelefonPoNazwie(String wzorzec){
        return sf.getCurrentSession().getNamedQuery("nazwa.telefon").setString("nazwa", wzorzec).list();
    }

    @Override
    public void usunZaleznosci(Model mod){
        List<Telefon> telefony = dajWszystkieTelefony();
        for (Telefon tel : telefony)
        {
            if(tel.getModel().getId() == mod.getId())
                usun(tel);
        }
    }

    @Override
    public Long dodaj(Telefon telefon) {
        Long id = (Long) sf.getCurrentSession().save(telefon);
        telefon.setId(id);
        Model model = (Model) sf.getCurrentSession().get(Model.class, telefon.getModel().getId());
        model.getTelefony().add(telefon);
        return id;
    }

    @Override
    public Long dodaj(Model model) {
        Long id = (Long) sf.getCurrentSession().save(model);
        model.setId(id);
        return id;
    }

    @Override
    public void usun(Telefon tel) {
        tel = (Telefon) sf.getCurrentSession().get(Telefon.class, tel.getId());
        Model mod = (Model) sf.getCurrentSession().get(Model.class, tel.getModel().getId());
        mod.getTelefony().remove(tel);
        sf.getCurrentSession().delete(tel);
    }

    @Override
    public void usun(Model mod) {
        mod = (Model) sf.getCurrentSession().get(Model.class, mod.getId());
        sf.getCurrentSession().delete(mod);
    }

    @Override
    public void edycja(Telefon tel, Model model, String nazwa, String opis) {
        tel = (Telefon) sf.getCurrentSession().get(Telefon.class, tel.getId());
        tel.setModel(model);
        tel.setNazwa(nazwa);
        tel.setOpis(opis);
        sf.getCurrentSession().update(tel);
    }

    @Override
    public void edycja(Model mod, String model, String opis) {
        mod = (Model) sf.getCurrentSession().get(Model.class, mod.getId());
        mod.setNazwa(model);
        mod.setOpis(opis);
        sf.getCurrentSession().update(mod);
    }

    @Override
    public Telefon pobierzTelefonPoId(Long id) {
        return (Telefon) sf.getCurrentSession().get(Telefon.class, id);
    }

    @Override
    public Model pobierzModelPoId(Long id) {
        return (Model) sf.getCurrentSession().get(Model.class, id);
    }

}

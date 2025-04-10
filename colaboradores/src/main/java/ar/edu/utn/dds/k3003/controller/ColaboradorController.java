package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.Service.UtilsMetrics;
import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.model.ColaboradorDTO;
import ar.edu.utn.dds.k3003.model.*;
import ar.edu.utn.dds.k3003.model.exceptions.ErrorConParametrosException;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static ar.edu.utn.dds.k3003.model.FormasDeColaborarEnum.DONADOR;
import static ar.edu.utn.dds.k3003.model.FormasDeColaborarEnum.TRANSPORTADOR;
import static ar.edu.utn.dds.k3003.model.FormasDeColaborarEnum.TECNICO;

public class ColaboradorController {

  private final Fachada fachada;
  public ColaboradorController(Fachada fachada) {
    this.fachada = fachada;
  }

  public void agregar(Context context) {
    try {
      ColaboradorDTO colaboradorDTORta = this.fachada.agregar(context.bodyAsClass(ColaboradorDTO.class));
      context.json(colaboradorDTORta);
      context.status(HttpStatus.CREATED);
      UtilsMetrics.actualizarCantColaboradores(true);
      if(colaboradorDTORta.getFormas().size()>1) {
        UtilsMetrics.actualizarColaboradores(true, true);
        UtilsMetrics.actualizarColaboradores(true, false);
      }
      else UtilsMetrics.actualizarColaboradores(true, DONADOR.equals(colaboradorDTORta.getFormas().get(0)));
        //FUNCIONA?????*/
      context.json(colaboradorDTORta);
      // context.result("Colaborador agregado");
    } catch (NoSuchElementException e) {
      context.result(e.getLocalizedMessage());
      context.status(HttpStatus.BAD_REQUEST);
    }
  }

  public void suscribirse(Context context) {
    try {
      SuscripcionDTO2 suscripcion  = context.bodyAsClass(SuscripcionDTO2.class);
      this.fachada.suscribirse(suscripcion);
      context.status(HttpStatus.CREATED);
    } catch (NoSuchElementException ex) {
      context.result(ex.getLocalizedMessage());
      context.status(HttpStatus.NOT_FOUND);
    }
  }

  public void obtenerColaborador(Context context) {
    var id = context.pathParamAsClass("id", Long.class).get();
    try {
      var colaboradorRta = this.fachada.buscarXId(id);
      context.json(colaboradorRta);
    } catch (NoSuchElementException ex) {
      context.result(ex.getLocalizedMessage());
      context.status(HttpStatus.NOT_FOUND);
    } catch (NullPointerException ex) {
      context.result(ex.getLocalizedMessage());
      context.status(HttpStatus.NOT_FOUND);
    }

  }

  public void modificarColaboracion(Context context) {
    try {
      Long id = context.pathParamAsClass("id", Long.class).get();

      List<FormasDeColaborarEnum> formasDeColaborar = context.bodyAsClass(FormasDTO.class).getFormas();
      List<FormasDeColaborarEnum> formasDeColaborarViejo = this.fachada.modificar(id, formasDeColaborar);//CUIDADO SI LE AGREGAMOS FUNCONABILIDAD A FORMADECOLABORARENUM
      compararFormas(formasDeColaborarViejo,formasDeColaborar);
      context.status(HttpStatus.OK);
    } catch (NoSuchElementException ex) {
      context.result(ex.getLocalizedMessage());
      context.status(HttpStatus.NOT_FOUND);
    }
  }

  public void obtenerPuntos(Context context) {

    Long id = context.pathParamAsClass("id", Long.class).get();
    try {
      Double puntos = this.fachada.puntos(id);
      context.json(puntos);
      context.status(HttpStatus.OK);
    } catch (NoSuchElementException ex) {
      context.result(ex.getLocalizedMessage());
      context.status(HttpStatus.NOT_FOUND);
    }
  }




  public void reportarHeladera(Context context) {
    Integer heladeraId = context.pathParamAsClass("heladeraId", Integer.class).get();
    try {
      this.fachada.reportarHeladera(heladeraId);
      context.status(HttpStatus.OK);
    } catch (NoSuchElementException ex) {
      context.result(ex.getLocalizedMessage());
      context.status(HttpStatus.NOT_FOUND);
    }
  }
  public void repararHeladera(Context context) {
    Integer heladeraId = context.pathParamAsClass("heladeraId", Integer.class).get();
    Long id = context.pathParamAsClass("id", Long.class).get();
    try {
      this.fachada.repararHeladera(id,heladeraId);
      context.status(HttpStatus.OK);
    } catch (NoSuchElementException ex) {
      context.result(ex.getLocalizedMessage());
      context.status(HttpStatus.NOT_FOUND);
    }catch (IllegalArgumentException e){
      context.result("Error al reparar la heladera porque el colaborador no es un tecnico: " + e.getMessage());
      context.status(HttpStatus.BAD_REQUEST);;
    }
  }

  public void avisar(Context context) {
    SuscripcionDTO2 suscripcion= context.bodyAsClass(SuscripcionDTO2.class);
    try {
      this.fachada.avisar(suscripcion);
      context.status(HttpStatus.OK);
    } catch (NoSuchElementException ex) {
      context.result(ex.getLocalizedMessage());
      context.status(HttpStatus.NOT_FOUND);
    }
  }
  
  public void obtenerFormas(Context context) {
    try {
      Long id = context.pathParamAsClass("id", Long.class).get();

      List<FormasDeColaborarEnum> formas = this.fachada.obtenerFormas(id);
      context.json(formas);
      context.status(HttpStatus.OK);
    } catch (NoSuchElementException ex) {
      context.result(ex.getLocalizedMessage());
      context.status(HttpStatus.NOT_FOUND);
    }
  }

  public void donarPesos(Context context) {
    Integer cantPesos = context.pathParamAsClass("cantPesos", Integer.class).get();
    Long id = context.pathParamAsClass("id", Long.class).get();
    try {
      this.fachada.sumarPesos(id,cantPesos);
      context.status(HttpStatus.OK);
    } catch (NoSuchElementException ex) {
      context.result(ex.getLocalizedMessage());
      context.status(HttpStatus.NOT_FOUND);
    }
  }

  public void actualizarPesosPuntos(Context context) {

    try {
      PesosPuntos pesosPuntos = context.bodyAsClass(PesosPuntos.class);
      //pesosPuntos.
      this.fachada.actualizarPesosPuntos(pesosPuntos.getPesosDonados(),pesosPuntos.getViandas_Distribuidas(),pesosPuntos.getViandasDonadas(),pesosPuntos.getTarjetasRepartidas(),pesosPuntos.getHeladerasActivas(),pesosPuntos.getHeladerasReparadas());
      context.status(HttpStatus.CREATED);
      context.result("Puntos actualizados");
    } catch (ErrorConParametrosException ex) {
      context.result(ex.getLocalizedMessage());
      context.status(HttpStatus.CONFLICT);    //ESTA BIEN ESTO?
    }
  }

  public void clean(Context context) {
    fachada.borrarDB();
  }

  private void compararFormas(List<FormasDeColaborarEnum> formasDeColaborarViejo,List<FormasDeColaborarEnum> formasDeColaborar)
  {
    if(formasDeColaborarViejo.contains(DONADOR) &&  !formasDeColaborar.contains(DONADOR))
    UtilsMetrics.actualizarColaboradores(false, true);

    if(!formasDeColaborarViejo.contains(DONADOR) &&  formasDeColaborar.contains(DONADOR))
    UtilsMetrics.actualizarColaboradores(true, true);

    if(formasDeColaborarViejo.contains(TRANSPORTADOR) &&  !formasDeColaborar.contains(TRANSPORTADOR))
    UtilsMetrics.actualizarColaboradores(false, false);

    if(!formasDeColaborarViejo.contains(TRANSPORTADOR) &&  formasDeColaborar.contains(TRANSPORTADOR))
    UtilsMetrics.actualizarColaboradores(true, false);
  }

}


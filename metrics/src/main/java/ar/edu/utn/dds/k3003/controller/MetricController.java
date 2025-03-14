package ar.edu.utn.dds.k3003.controller;

import ar.edu.utn.dds.k3003.service.DDMetricsUtils;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.micrometer.core.instrument.Gauge;
import lombok.extern.slf4j.Slf4j;

import java.util.AbstractMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class MetricController {
  private final DDMetricsUtils metricsUtils;

  private final AtomicInteger incidentesActivos = new AtomicInteger(0);
  private final AtomicInteger fallasEnHeladeras = new AtomicInteger(0);
  private final AtomicInteger aperturasHeladera = new AtomicInteger(0);
  private final AtomicInteger trasladosEnCurso = new AtomicInteger(0);
  private AtomicInteger cantColaboradores = new AtomicInteger(0);
  private AtomicInteger cantDonadores = new AtomicInteger(0);
  private AtomicInteger cantTransportadores = new AtomicInteger(0);
  private final AtomicInteger viandasCreadas = new AtomicInteger(0);
  private final AtomicInteger viandasEnTransporte = new AtomicInteger(0);
  private final AtomicInteger viandasVencidas = new AtomicInteger(0);

  public MetricController(DDMetricsUtils metricsUtils) {
    this.metricsUtils = metricsUtils;
      metricsUtils.getRegistry().gauge("incidentesActivos", incidentesActivos, AtomicInteger::get);
      metricsUtils.getRegistry().gauge("fallasEnHeladeras", fallasEnHeladeras, AtomicInteger::get);
      metricsUtils.getRegistry().gauge("aperturaHeladera", aperturasHeladera, AtomicInteger::get);
      metricsUtils.getRegistry().gauge("cantColaboradores", cantColaboradores, AtomicInteger::get);
      metricsUtils.getRegistry().gauge("cantTransportadores", cantTransportadores, AtomicInteger::get);
      metricsUtils.getRegistry().gauge("cantDonadores   ", cantDonadores, AtomicInteger::get);
      metricsUtils.getRegistry().gauge("viandasEnTransporte   ", viandasEnTransporte, AtomicInteger::get);
      metricsUtils.getRegistry().gauge("trasladosEnCurso", trasladosEnCurso, AtomicInteger::get);
  }

    // Registro del gauge en la inicialización de tu aplicación o clase

  public void aperturaHeladera(Context context) {
    try {
      aperturasHeladera.incrementAndGet();
      context.status(HttpStatus.OK);
      log.info("Apertura de heladera registrada.");
    } catch (Exception e) {
      context.status(HttpStatus.INTERNAL_SERVER_ERROR);
      log.error("Error al registrar la apertura de la heladera", e);
    }
  }
    public void incidentesActivos(Context context) {
        String accion = context.pathParamAsClass("accion", String.class).get();

        if ("incrementar".equals(accion)) {
            incidentesActivos.incrementAndGet();
            log.info("cantidad de Incidentes incrementados.");
            context.result("cantidad de Incidentes incrementados");
        } else if ("disminuir".equals(accion)) {
            incidentesActivos.decrementAndGet();
            log.info("cantidad de Incidentes disminuidos.");
            context.result("cantidad de Incidentes disminuidos");
        } else {
            throw new IllegalArgumentException("Acción no válida. Debe ser 'incrementar' o 'disminuir'.");
        }
        context.status(HttpStatus.OK);
  }

  public void fallaHeladeras(Context context){
    fallasEnHeladeras.incrementAndGet();
    try{context.status(HttpStatus.OK);
    log.info("Falla Registrada en una heladera.");
    } catch (Exception e) {
    context.status(HttpStatus.INTERNAL_SERVER_ERROR);
    log.error("Error al registrar una falla en una heladera", e);
    }
  }
  public void trasladosRealizados(Context context) {
    try {
      metricsUtils.getRegistry().counter("trasladoFinalizado").increment();
      context.status(HttpStatus.OK);
      log.info("Nuevo traslado finalizado");
    } catch (Exception e) {
      context.status(HttpStatus.INTERNAL_SERVER_ERROR);
      log.error("Error al registrar traslado finalizado", e);
    }
  }



  public void trasladosEnCurso(Context context) {
    try {
        String accion = context.pathParamAsClass("accion", String.class).get();

      if ("incrementar".equals(accion)) {
        trasladosEnCurso.incrementAndGet();
        log.info("Traslados en curso incrementados.");
          context.result("cantColaboradores incrementados");
      } else if ("disminuir".equals(accion)) {
        trasladosEnCurso.decrementAndGet();
        log.info("Traslados en curso disminuidos.");
          context.result("cantColaboradores disminuidos");
      } else {
        throw new IllegalArgumentException("Acción no válida. Debe ser 'incrementar' o 'disminuir'.");
      }
      context.status(HttpStatus.OK);
    } catch (Exception e) {
      context.status(HttpStatus.INTERNAL_SERVER_ERROR);
      log.error("Error al actualizar los traslados en curso", e);
    }
  }

    // Método para manejar el incremento/disminución
    public void CantColaboradores(Context context) {
        try {
            String accion = context.pathParamAsClass("accion", String.class).get();

            if ("incrementar".equals(accion)) {
                cantColaboradores.incrementAndGet();  // Incrementamos el contador
                log.info("cantColaboradores incrementados.");
                context.result("cantColaboradores incrementados");
            } else if ("disminuir".equals(accion)) {
                cantColaboradores.decrementAndGet();  // Disminuimos el contador
                log.info("cantColaboradores disminuidos.");
                context.result("cantColaboradores disminuidos");
            } else {
                throw new IllegalArgumentException("Acción no válida. Debe ser 'incrementar' o 'disminuir'.");
            }

            context.status(HttpStatus.OK);
        } catch (Exception e) {
            context.status(HttpStatus.INTERNAL_SERVER_ERROR);
            log.error("Error al actualizar cantColaboradores", e);
        }
    }

  public void cantDonadores(Context context) {
    try {
        String accion = context.pathParamAsClass("accion", String.class).get();

      if ("incrementar".equals(accion)) {
        cantDonadores.incrementAndGet();
        log.info("cantDonadores incrementados.");
          context.result("cantColaboradores incrementados");
      } else if ("disminuir".equals(accion)) {
        cantDonadores.decrementAndGet();
        log.info("cantDonadores disminuidos.");
          context.result("cantColaboradores disminuidos");
      } else {
        throw new IllegalArgumentException("Acción no válida. Debe ser 'incrementar' o 'disminuir'.");
      }
      context.status(HttpStatus.OK);
    } catch (Exception e) {
      context.status(HttpStatus.INTERNAL_SERVER_ERROR);
      log.error("Error al actualizar la cantidad de donadores", e);
    }
  }

  public void cantTransportadores(Context context) {
    try {
        String accion = context.pathParamAsClass("accion", String.class).get();

        if ("incrementar".equals(accion)) {
        cantTransportadores.incrementAndGet();
        log.info("cantidad de transportadores incrementados.");
        context.result("cantidad de transportadores incrementados");
      } else if ("disminuir".equals(accion)) {
        cantTransportadores.decrementAndGet();
        log.info("cantidad de transportadores disminuidos.");
            context.result("cantidad de transportadores disminuidos");
      } else {
        throw new IllegalArgumentException("Acción no válida. Debe ser 'incrementar' o 'disminuir'.");
      }
      context.status(HttpStatus.OK);
    } catch (Exception e) {
      context.status(HttpStatus.INTERNAL_SERVER_ERROR);
      log.error("Error al actualizar la cantidad de transportadores", e);
    }
  }
  
  public void viandasCreadas(Context context) {
	  try {
		  String accion = context.pathParamAsClass("accion", String.class).get();
	      metricsUtils.getRegistry().counter("viandasCreadas").increment();
	      
	      log.info("viandasCreadas aumento.");
	      
	      context.result("cantidad de viandas creadas aumento");
	      context.status(HttpStatus.OK);
	    } catch (Exception e) {
	      context.status(HttpStatus.INTERNAL_SERVER_ERROR);
	      log.error("Error al actualizar la cantidad de viandas creadas", e);
	    }
	  }
  public void viandasEnTransporte(Context context) {
	  try {
		  String accion = context.pathParamAsClass("accion", String.class).get();

		  if ("incrementar".equals(accion)) {
			  viandasEnTransporte.incrementAndGet();
			  
		      log.info("aumento cantidad de viandas en traslado.");
		      context.result("aumento cantidad de viandas en traslado");
		  } else if ("disminuir".equals(accion)) {
		      viandasEnTransporte.decrementAndGet();
		      
		      log.info("disminuyo cantidad de viandas en traslado.");
		      context.result("disminuyo cantidad de viandas en traslado");
		  } else {
		      throw new IllegalArgumentException("Acción no válida. Debe ser 'incrementar' o 'disminuir'.");
		  }
	      context.status(HttpStatus.OK);
	    } catch (Exception e) {
	      context.status(HttpStatus.INTERNAL_SERVER_ERROR);
	      log.error("Error al actualizar la cantidad de viandas en transporte", e);
	    }
	  }
  public void viandasVencidas(Context context) {
	  try {
		  String accion = context.pathParamAsClass("accion", String.class).get();
		  metricsUtils.getRegistry().counter("viandasVencidas").increment();
		  
	      log.info("viandasVencidas aumento.");
	      
	      context.result("cantidad de viandas creadas aumento");
	      context.status(HttpStatus.OK);
	    } catch (Exception e) {
	      context.status(HttpStatus.INTERNAL_SERVER_ERROR);
	      log.error("Error al actualizar la cantidad de viandas vencidas", e);
	    }
	  }
    public void resetearMetricas(Context context) {
        cantColaboradores.set(0);
        cantDonadores.set(0);
        cantTransportadores.set(0);
        System.out.println("La métrica 'cantColaboradores' ha sido reseteada a 0.");
    }
}

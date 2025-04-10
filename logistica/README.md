[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-24ddc0f5d75046c5622901739e7c5dd533143b0c8e959d652212380cedb1ea36.svg)](https://classroom.github.com/a/KXg_hGCY)
# {Abdel Nour, Nazarena}

Template para TP DDS 2024 - Entrega 1

deploy link: https://logistica-3vjf.onrender.com
deploy base de datos: https://dashboard.render.com/d/dpg-cpkvhbq0si5c73d322t0-a/logs


Endopoints

Crear rutas

POST/rutas:
 - application/json

request body:
{
    
    "colaboradorId": 1,
    
    "heladeraIdOrigen": 1,
    
    "heladeraIdDestino": 2
}


Asignar traslado a un colaborador

POST/traslados

 - application/json

request body:
{
  
    "qrVianda": "abcd",

    "status": "CREADO",
  
    "fechaTraslado": "2024-05-15T21:10:40Z",

    "heladeraOrigen": 1,

    "heladeraDestino": 2
}


Obtener un traslado por id del colaborador, un mes y un año

GET//traslados/search/findByColaboradorId

Request:

    Parameters: id (long) - ID del colaborador

    Parameters: anio (integer) - anio del traslado

    Parameters: mes (integer) - mes del traslado


Obtener traslado por un id

GET//traslados/{id}

Request:
  
    Parameters: id (long) - id del traslado


Modificar un traslado dado su id

PATCH//traslados/{id}

Request

  - Parameters: qrVianda (string) - qr de la vianda

request body

{ 

    "status": "EN_VIAJE" o "ENTREGADO" 

}




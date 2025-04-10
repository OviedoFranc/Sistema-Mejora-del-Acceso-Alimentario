[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-24ddc0f5d75046c5622901739e7c5dd533143b0c8e959d652212380cedb1ea36.svg)](https://classroom.github.com/a/KXg_hGCY)
# {Dyzenchauz, Theo}

Template para TP DDS 2024 - Entrega 5

render:https://two024-tp-entrega-3-theo1d.onrender.com

Enpoints:

POST /colaboradores
Agregar un nuevo colaborador

Example:

{
  "nombre": "juan",
  "formas": [
    "DONADOR"
  ]
}

GET/colaboradores/{id}
Obtener un colaborador por id. (reemplazar {id} por el numero de colaborador)

PATCH/colaboradores/{id}
Modificar colaboracion

Example:

{
  "formas": [
    "DONADOR",
    "TRANSPORTADOR"
  ]
}

PUT/formula
Alterar los pesos de los puntos

Example:

{
  "pesosDonados": 1.0,
  "viandasDistribuidas": 3.0,
  "viandasDonadas": 5.0,
  "tarjetasRepartidas": 1.0,
  "heladerasActivas": 1.0
}

GET/colaboradores/{id}/puntos
Obtener puntos de colaborador según el parámetro id (reemplazar {id} por el numero de colaborador)

DELETE/clear
Borrar todos los colaboradores y los pesos de los puntos.
